import usb.core
import usb.util
import logging
import os
logging.basicConfig(level=logging.INFO, filename=os.path.basename(__file__)[:-3]+'.log', format='%(asctime)s %(message)s')


class PixelRing:
    TIMEOUT = 8000

    def __init__(self, dev):
        self.dev = dev

    def trace(self):
        self.write(0)

    def mono(self, color):
        self.write(1, [(color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, 0])

    def set_color(self, rgb=None, r=0, g=0, b=0):
        if rgb:
            self.mono(rgb)
        else:
            self.write(1, [r, g, b, 0])

    def off(self):
        self.mono(0)

    def listen(self, direction=None):
        self.write(2)

    wakeup = listen

    def speak(self):
        self.write(3)

    def think(self):
        self.write(4)

    wait = think

    def spin(self):
        self.write(5)

    def show(self, data):
        self.write(6, data)

    customize = show

    def set_brightness(self, brightness):
        self.write(0x20, [brightness])

    def set_color_palette(self, a, b):
        self.write(
            0x21,
            [
                (a >> 16) & 0xFF,
                (a >> 8) & 0xFF,
                a & 0xFF,
                0,
                (b >> 16) & 0xFF,
                (b >> 8) & 0xFF,
                b & 0xFF,
                0,
            ],
        )

    def set_vad_led(self, state):
        self.write(0x22, [state])

    def set_volume(self, volume):
        self.write(0x23, [volume])

    def change_pattern(self, pattern):
        if pattern == "echo":
            self.write(0x24, [1])
        else:
            self.write(0x24, [0])

    def write(self, cmd, data=[0]):
        self.dev.ctrl_transfer(
            usb.util.CTRL_OUT
            | usb.util.CTRL_TYPE_VENDOR
            | usb.util.CTRL_RECIPIENT_DEVICE,
            0,
            cmd,
            0x1C,
            data,
            self.TIMEOUT,
        )

    @property
    def version(self):
        return self.dev.ctrl_transfer(
            usb.util.CTRL_IN
            | usb.util.CTRL_TYPE_VENDOR
            | usb.util.CTRL_RECIPIENT_DEVICE,
            0,
            0x80 | 0x40,
            0x1C,
            24,
            self.TIMEOUT,
        ).tostring()

    def close(self):
        """
        close the interface
        """
        usb.util.dispose_resources(self.dev)


def respeaker_pixelring(vid=0x2886, pid=0x0018):
    dev = usb.core.find(idVendor=vid, idProduct=pid)
    if not dev:
        return

    # configuration = dev.get_active_configuration()

    # interface_number = None
    # for interface in configuration:
    #     interface_number = interface.bInterfaceNumber

    #     if dev.is_kernel_driver_active(interface_number):
    #         dev.detach_kernel_driver(interface_number)

    return PixelRing(dev)


# if __name__ == "__main__":
#     import time

#     pixel_ring = find()
#     print(pixel_ring.version)
#     while True:
#         try:
#             pixel_ring.wakeup(180)
#             time.sleep(3)
#             pixel_ring.listen()
#             time.sleep(3)
#             pixel_ring.think()
#             time.sleep(3)
#             pixel_ring.set_volume(8)
#             time.sleep(3)
#             pixel_ring.off()
#             time.sleep(3)
#         except KeyboardInterrupt:
#             break

# pixel_ring.off()