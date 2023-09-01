import argparse
import pathlib
import queue
import threading
import time
import os
import cv2

from farmi import Publisher, Subscriber
import logging

logging.basicConfig(level=logging.INFO, filename=os.path.basename(__file__)[:-3]+'.log', format='%(asctime)s %(message)s')

parser = argparse.ArgumentParser()
parser.add_argument("--camera", default=0, type=int)


args = parser.parse_args()

camera_id = args.camera


out = None
camera = None
pub = Publisher(
    "webcam",
    local_save="farmi_logs",
    directory_service_address="tcp://192.168.1.176:5555",
)

started = True
recording = False
sub = Subscriber(directory_service_address="tcp://192.168.1.176:5555")

# width, height = 1920, 1080
width, height = 1280, 720
# width = camera.get(cv2.CAP_PROP_FRAME_WIDTH)  # float
# height = camera.get(cv2.CAP_PROP_FRAME_HEIGHT)  # float


def response(topic, time_, msg):
    global recording, out, started
    if msg == ["start"]:
        print("recording")
        recording = True
    elif msg == ["stop"]:
        print("stopped recording")
        recording = False
        pub.end_recording()


sub.subscribe_to("start_stop", response)
q = queue.Queue()


def video_saver():
    last_time = 0

    info_pub = Publisher("webcam_image")
    while started:
        frame, time_ = q.get()
        _, img = cv2.imencode(".jpg", frame)
        if recording:
            pub.send(img, timestamp=time_)
        if time_ > last_time + 5:
            last_time = time_
            info_pub.send(img.tobytes())


t = threading.Thread(target=video_saver)
t.daemon = True
t.start()

t = threading.Thread(target=sub.listen)
t.daemon = True
t.start()


camera = cv2.VideoCapture(camera_id)

camera.set(cv2.CAP_PROP_FRAME_WIDTH, width)  # float
camera.set(cv2.CAP_PROP_FRAME_HEIGHT, height)  # float
camera.set(cv2.CAP_PROP_AUTOFOCUS, 0)  # disable autofocus
camera.set(cv2.CAP_PROP_FPS, 30)


while started:
    _, frame = camera.read()
    time_ = time.time()
    # out.write(frame)
    q.put((frame, time_))
