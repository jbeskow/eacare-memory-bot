# -*- coding: utf-8 -*-

import json
import os
import logging
import re
import time
import urllib.parse as urlparse
from threading import Thread

from farmi import Publisher, Subscriber
from flask import Flask, render_template, request, url_for
from flask_socketio import SocketIO, emit
from datetime import datetime
from setup_logging import setup_logging


datestamp = datetime.now().isoformat().replace(':', '_').replace('.', '_')

logger = setup_logging(__name__, filename=f'logs/session_log_{datestamp}.log')

#logging.basicConfig(level=logging.DEBUG, filename=max(glob.glob('logs/*.log), key=os.path.getctime, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')

logger.info('Started recording script')

# logging.basicConfig(level=logging.INFO, filename=os.path.basename(__file__)[:-3]+'.log', format='%(asctime)s %(message)s')



app = Flask(__name__)
app.config["SECRET_KEY"] = "secret!"

socketio = SocketIO(app)

logger.info('waiting for farmi')
start_stop = Publisher("start_stop", local_save="farmi_logs")
ipad_drawing = Publisher("ipad_drawing", local_save="farmi_logs")
ipad_screen = Publisher("ipad_screen", local_save="farmi_logs")
flic_button = Publisher("flic_button", local_save="farmi_logs")
iphone_stat = Publisher("iphone_stat", local_save="farmi_logs")
flic_button.send("dummy_msg")

logger.info('Started all farmi publishers')
logger.info('farmi time offset: %s %s', time.time(), start_stop.time_offset)

BASE_STATUS = {"connected": False, "last": None, "msg": "Not connected", "timeout": 10}

DEVICES = [
    "info.patient_iphone",
    "image.patient_iphone",
    "image.flir_camera",
    "info.clinician_iphone",
    "image.clinician_iphone",
    "image.overview_camera",
    "microphone",
    "tobii_eyetracker",
]

STATUS = {name: BASE_STATUS.copy() for name in DEVICES}


PATIENT = {"connected": False, "last": None}
CLINICIAN = {"connected": False, "last": None}

recording = False
watch_status = "READY"
latest_drawing_timestamp = None


@app.route('/dump_furhat_data', methods=['POST'])
def upload_file():      
    file_ = request.files['file']
    date_stamp = datetime.now().isoformat().replace(':', '_').replace('.', '_')
    file_.save(os.path.join('farmi_logs', f"{date_stamp}_furhat_log.json"))
    return 'OK'


@app.route("/timesync")
def watch_timesync_legacy():
    return json.dumps(
        {
            "client_time": float(request.args.get("client_time")),
            "server_time": time.time() * 1000,
        }
    )


@app.route("/watch/timesync")
def watch_timesync():
    return str(time.time())


@app.route("/watch/servertest")
@app.route("/servertest")
def watch_servertest():
    return "RUNNING"


@app.route("/watch/data", methods=["POST"])
@app.route("/data", methods=["POST"])
def watch_data():
    global watch_status
    watch_status = "RECEIVING_DATA"
    request.get_data()
    data = urlparse.parse_qs(request.data.decode("utf-8"))
    watch_status = "READY"

    return "DONE"


@app.route("/watch/heartbeat", methods=["POST"])
@app.route("/heartbeat", methods=["POST"])
def watch_heartbeat():
    request.get_data()
    data1 = urlparse.parse_qs(request.data.decode("utf-8"))

    msg = "NOT STARTED"

    # If recording has ended then END watch recording
    if watch_status == "WAITING_FOR_DATA":
        msg = "END"

    if recording:
        msg = "RECORDING SESSION"

    data1["response"] = msg
    # dump_data("watch_heartbeat", data1)

    return json.dumps(data1)


@socketio.on("heartbeat")
def heartbeat(json):
    logger.debug('heartbeat %s', json)
    cur = time.time()
    if PATIENT["connected"] and cur - PATIENT["last"] > 1000:
        PATIENT["connected"] = False
    if CLINICIAN["connected"] and cur - CLINICIAN["last"] > 1000:
        CLINICIAN["connected"] = False

    if json["role"] == "PATIENT":
        PATIENT["connected"] = True
        PATIENT["last"] = cur
    if json["role"] == "CLINICIAN":
        CLINICIAN["connected"] = True
        CLINICIAN["last"] = cur

    emit(
        "paired_status",
        {
            "paired": PATIENT["connected"] and CLINICIAN["connected"],
            "is_recording": recording,
        },
    )


@socketio.on("status")
def handle_status():
    cur = time.time()
    if PATIENT["connected"] and cur - PATIENT["last"] > 1000:
        PATIENT["connected"] = False
    if CLINICIAN["connected"] and cur - CLINICIAN["last"] > 1000:
        CLINICIAN["connected"] = False

    emit(
        "status",
        {
            "PATIENT": PATIENT["connected"],
            "CLINICIAN": CLINICIAN["connected"],
            "is_recording": recording,
        },
        json=True,
    )


@socketio.on("get_topic_status")
def new_status_handle():
    global STATUS
    for topic in STATUS:

        if (
            STATUS[topic]["connected"]
            and time.time() - STATUS[topic]["last"] > STATUS[topic]["timeout"] * 1000
        ):
            STATUS[topic]["connected"] = False
    emit("get_topic_status", STATUS, json=True)


@socketio.on("drawing")
def handle_json(json):
    socketio.emit("drawing", json)


@socketio.on("broadcast_action")
def broadcast_action(json):
    logger.info('broadcast_acition %s', json)
    if recording:
        ipad_screen.send({"from": json["role"], "action": json["action"]})
    socketio.emit("control_action", {"from": json["role"], "action": json["action"]})


@socketio.on("time_sync")
def time_sync(json):
    json["client_ping_received"] = time.time() * 1000
    emit("time_sync", json, json=True)


@app.route('/furhat_page/<page>')
def furhat_page(page):
    global latest_drawing_timestamp
    latest_drawing_timestamp = None
    logger.info(f"furhat requested to change page to '{page}'")
    broadcast_action({"role": "FURHAT", "action": page})
    return 'OK'


@app.route("/latest_user_interaction")
def latest_user_interaction():
    logger.debug(f"latest_drawing_timestamp: {latest_drawing_timestamp}; current_time: {time.time()}")
    if not latest_drawing_timestamp:
        return 'null'
    return str(time.time() - latest_drawing_timestamp)


@socketio.on("draw_data")
def handle_draw_data(json):
    global latest_drawing_timestamp
    latest_drawing_timestamp = time.time()
    ipad_drawing.send(json)


@app.route("/")
def table():
    return render_template("table_draw.html")


@app.route("/start_recording")
def start_recording():
    logger.debug('start recording')
    global recording
    recording = True
    start_stop.send(["start"])
    return "OK"


@app.route("/stop_recording")
def stop_recording():
    logger.debug('stop recording')
    global recording
    recording = False
    start_stop.send(["stop"])

    for i, recorder in enumerate((
        start_stop,
        ipad_drawing,
        ipad_screen,
        flic_button,
        iphone_stat,
    )):

        recorder.end_recording()

    return "OK"


@app.route("/flic/<string:click_type>")
def flic(click_type):
    logger.debug('flic button clicked %s', click_type)
    if recording:
        flic_button.send(click_type)
    return "OK"


@app.route("/control")
def control_draw():
    return render_template("control_draw.html")


@app.route("/status")
def status_page():
    return render_template("status.html")


@app.route("/calibrate_tobii")
def calibrate_tobii():
    return render_template("calibrate_tobii.html")


@app.route("/start_calibrate_tobii")
def do_calibrate_tobii():
    logger.debug('start_calibrate_tobii')
    socketio.emit(
        "control_action",
        {
            "from": "TECHNICIAN",
            "action": "CALIBRATE_TOBII",
            "location": url_for("calibrate_tobii"),
        },
    )
    return "OK"


@app.route("/change/<page>")
def change(page):

    socketio.send("/table/{}".format(page))
    return "OK"


def emit_video(topic, time_, msg):
    if topic == "image.overview_camera":
        socketio.emit("overview_camera", msg)
    elif topic == "image.patient_iphone":
        socketio.emit("video_patient", msg)
    elif topic == "image.clinician_iphone":
        socketio.emit("video_clinician", msg)
    elif topic == "image.flir_camera":
        socketio.emit("flir_video", msg)


def received_heartbeat(topic, time_, msg):
    global STATUS
    if STATUS.get(topic):
        STATUS[topic]["connected"] = True
        STATUS[topic]["last"] = time_


def info(topic, time_, msg):
    msg['topic'] = topic
    iphone_stat.send(msg)
    socketio.emit("IphoneINFO", {"topic": topic, "msg": msg})


def listener():
    logger.debug('started iphone listeners')
    s = Subscriber()
    s.subscribe_to(re.compile("info\.patient_iphone"), info)
    s.subscribe_to(re.compile("info\.clinician_iphone"), info)
    s.subscribe_to(re.compile("info\.flir_camera"), info)
    s.subscribe_to(re.compile("info\.overview_camera"), info)
    s.subscribe_to(re.compile("image\.patient_iphone"), emit_video)
    s.subscribe_to(re.compile("image\.clinician_iphone"), emit_video)
    s.subscribe_to(re.compile("image\.flir_camera"), emit_video)
    s.subscribe_to(re.compile("image\.overview_camera"), emit_video)
    s.subscribe_to("HEARTBEAT", received_heartbeat)

    logger.debug('farmi subscribe')
    s.listen()


@app.before_first_request
def hellothere():
    t = Thread(target=listener)
    t.daemon = True
    t.start()


# socketio.start_background_task(target=listener)
# eventlet.spawn(listener)


if __name__ == "__main__":
    app.run(host="0.0.0.0", debug=False)
    # socketio.run(app, host='0.0.0.0', port=5000, debug=True)
