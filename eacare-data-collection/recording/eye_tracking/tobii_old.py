import argparse

import tobiiresearch.interop.interop as tr
from farmi import Publisher, Subscriber
from tobiiresearch.implementation.EyeTracker import (EYETRACKER_GAZE_DATA,
                                                     EyeTracker)

parser = argparse.ArgumentParser()
parser.add_argument("--farmi_ip", default="tcp://192.168.1.176:5555")


args = parser.parse_args()

started = False

pub = Publisher(
    "webcam", local_save="farmi_logs", directory_service_address=args.farmi_ip
)
found_eyetrackers = tr.find_all_eyetrackers()
if not found_eyetrackers:
    raise Exception("no connected tobii trackers")

my_eyetracker = found_eyetrackers[0]
et = EyeTracker(my_eyetracker.address)


record = False


def gaze_callback(gaze_data):
    if record:
        pub.send(gaze_data)


et.subscribe_to(EYETRACKER_GAZE_DATA, gaze_callback, as_dictionary=True)


sub = Subscriber()


def response(topic, time_, msg):
    print(topic, time_, msg)
    global record
    if msg == ["start"]:
        record = True
    elif msg == ["stop"]:
        record = False
        sub.close()


sub.subscribe_to("start_stop", response)

sub.listen()

et.unsubscribe_from(EYETRACKER_GAZE_DATA, gaze_callback)
