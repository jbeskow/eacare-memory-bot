import threading
import time
import wave
import os
import numpy as np
import pyaudio
import sounddevice as sd

import cronus.beat as beat
from farmi import Publisher, Subscriber
from mic_tuning import find_microphone
from pixel_ring_control import respeaker_pixelring
import sys
sys.path.append('recording')
from setup_logging import setup_logging

logger = setup_logging(__name__)

qd = sd.query_devices()
dev_detected = False
for i in range(len(qd)):
    # if name=='ReSpeaker 4 Mic Array (UAC1.0)  (ReSpeaker 4 Mic Array (UAC1.0))' and default_samplerate==16000 and max_input_channels==6 and hostapi==3:
    if qd[i]["name"] == "ReSpeaker 4 Mic Array (UAC1.0)":
        dev_detected = True
        break

if dev_detected:
    logger.info("Microphone successfully detected!")
    detected_dev_no = i
else:
    logger.error("Cannot find the the right microphone!")
    exit()

dev = find_microphone()
if not dev:
    logger.error("No respeaker microphone found")
    exit()

FORMAT = pyaudio.paInt16
CHANNELS = 6
RATE = 16000
CHUNK = 2000
DEVICE_INDEX = detected_dev_no


record = False
stream = None
started = False
frames = []
wave_output_filename = None


# Disable led ring
pixel_ring = respeaker_pixelring()
pixel_ring.off()
pixel_ring.set_vad_led(False)

logger.info("Disabled LED ring")

# Initiate farmi
pub = Publisher("microphone", local_save="farmi_logs")
logger.info("setup microphone farmi publisher")
doa_pub = Publisher("doa_pub", local_save="farmi_logs")

logger.info("setup doa farmi publisher")
sub = Subscriber()


def callback(in_data, frame_count, time_info, status):
    if record:
        pub.send(np.frombuffer(in_data, dtype=np.uint16))
        frames.append(in_data)
        return None, pyaudio.paContinue
    return None, None


def mic_data_reader():
    beat.set_rate(15)
    while beat.true():
        if record:
            angle = dev.read("DOAANGLE")
            doa_pub.send(angle)
        try:
            beat.sleep()
        except:
            print('failed sleeping')

    dev.close()


threading.Thread(target=mic_data_reader).start()


def response(topic, time_, msg):
    global stream, record, wave_output_filename

    logger.debug("received farmi start_stop msg %s, %s, %s", topic, time_, msg)
    if not record and msg == ["start"]:
        logger.info("START")
        record = True
        wave_output_filename = f"audio/microphone-{time.time()}.wav"
        stream = pyaudio.PyAudio().open(
            format=FORMAT,
            channels=CHANNELS,
            rate=RATE,
            input_device_index=DEVICE_INDEX,
            input=True,
            frames_per_buffer=CHUNK,
            stream_callback=callback,
        )
    elif record and msg == ["stop"]:
        logger.info("STOP")
        record = False
        stream.stop_stream()
        stream.close()

        wf = wave.open(wave_output_filename, "wb")
        wf.setnchannels(CHANNELS)
        wf.setsampwidth(pyaudio.PyAudio().get_sample_size(FORMAT))
        wf.setframerate(RATE)
        wf.writeframes(b"".join(frames))
        wf.close()

        pub.end_recording()
        doa_pub.end_recording()


sub.subscribe_to("start_stop", response)
logger.info("subscribing to start_stop")
sub.listen()
