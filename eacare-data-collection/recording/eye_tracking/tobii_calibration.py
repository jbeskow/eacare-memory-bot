#!/usr/bin/env python


import argparse
import json
import time
import os

import numpy as np

import tobii_research as tr
from farmi import Publisher, Subscriber
from websocket_server import WebsocketServer
import sys
sys.path.append('recording')
from setup_logging import setup_logging

logger = setup_logging(__name__)

save_path = "farmi_logs"

"""Check that farmi_logs directory exists, otherwise create it"""
if not os.path.exists(save_path):
    try:
        os.mkdir(save_path)
    except OSError:
        print("Failed to create directory ", save_path)
    else:
        print("Successfully created directory ", save_path)

parser = argparse.ArgumentParser()
parser.add_argument("farmi_server")
args = parser.parse_args()


found_eyetrackers = tr.find_all_eyetrackers()

if not found_eyetrackers:
    raise Exception("no connected tobii trackers")

eyetracker = found_eyetrackers[0]
# da = eyetracker.get_display_area()
# print(da.bottom_left, da.bottom_right, da.top_left, da.top_right, da.width, da.height)

socket_server = None
recording = False
server = None
farmi_address = "tcp://" + args.farmi_server + ":5555"
pub = Publisher(
    "tobii_eyetracker", local_save="farmi_logs", directory_service_address=farmi_address
)


def gaze_callback(gaze_data):
    gaze_points = zip(
        gaze_data["left_gaze_point_on_display_area"],
        gaze_data["right_gaze_point_on_display_area"],
    )

    d = [
        None if np.isnan(p1) or np.isnan(p2) else (p1 + p2) / 2
        for p1, p2 in gaze_points
    ]

    data = json.dumps(["gaze_data"] + d)
    # print(socket_server, data)
    if socket_server is not None:
        socket_server.send_message_to_all(data)
        pub.send(gaze_data)
    if recording:
        pub.send(gaze_data)


eyetracker.subscribe_to(tr.EYETRACKER_GAZE_DATA, gaze_callback, as_dictionary=True)
# sub = Subscriber(directory_service_address=farmi_address)


def new_client(client, server):
    global socket_server
    socket_server = server
    calibration = tr.ScreenBasedCalibration(eyetracker)

    # Enter calibration mode.
    calibration.enter_calibration_mode()
    try:
        logger.info("Entered calibration mode for eye tracker with serial number {0}.".format(
            eyetracker.serial_number
        ))
        

        # Define the points on screen we should calibrate at.
        # The coordinates are normalized, i.e. (0.0, 0.0) is the upper left corner and (1.0, 1.0) is the lower right corner.
        points_to_calibrate = [
            (0.5, 0.5),
            (0.1, 0.1),
            (0.1, 0.9),
            (0.9, 0.1),
            (0.9, 0.9),
        ]

        for point in points_to_calibrate:
            logger.info("Show a point on screen at {0}.".format(point))
            server.send_message_to_all(json.dumps(["calib_point"] + list(point)))
            # Wait a little for user to focus.
            time.sleep(1)
            while (
                calibration.collect_data(point[0], point[1])
                != tr.CALIBRATION_STATUS_SUCCESS
            ):
                time.sleep(0.3)
                # print("Collecting data at {0}.".format(point))
                logger.info("retrying..")
            # if calibration.collect_data(point[0], point[1]) != tr.CALIBRATION_STATUS_SUCCESS:
            # Try again if it didn't go well the first time.
            # Not all eye tracker models will fail at this point, but instead fail on ComputeAndApply.
            #    calibration.collect_data(point[0], point[1])
        
        logger.info("Computing and applying calibration.")
        calibration_result = calibration.compute_and_apply()
        logger.info("Compute and apply returned {0} and collected at {1} points.".format(
                calibration_result.status, len(calibration_result.calibration_points)
        ))
        
        server.send_message_to_all(json.dumps(["ALMOST_DONE"]))
        server.send_message_to_all(json.dumps(["calib_point", 0, 0]))
    finally:
        calibration.leave_calibration_mode()
    time.sleep(5)
    server.send_message_to_all(json.dumps(["DONE"]))


def response(topic, time_, msg):
    logger.debug("received farmi start_stop msg %s, %s, %s", topic, time_, msg)
    global recording, server
    if msg == ["start"]:
        logger.debug("START")
        recording = True
    elif msg == ["stop"]:
        logger.debug("STOP")
        recording = False
        pub.end_recording()
    elif msg == ["calibrate_tobii"]:
        logger.debug("CALIBRATION")
        server = WebsocketServer(8765, host="0.0.0.0")
        server.set_fn_new_client(new_client)
        server.run_forever()


# sub.subscribe_to("start_stop", response)
logger.info("running")
server = WebsocketServer(8765, host="0.0.0.0")
server.set_fn_new_client(new_client)
server.run_forever()
eyetracker.unsubscribe_from(tr.EYETRACKER_GAZE_DATA, gaze_callback)
pub.end_recording()
logger.info("RECORDING ENDED.")
