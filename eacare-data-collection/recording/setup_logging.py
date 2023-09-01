import logging
from glob import glob
import os

def setup_logging(name, filename=None):
    formatter = '%(asctime)s - %(levelname)s - %(pathname)s(%(funcName)s:%(lineno)d) - %(message)s'
    if not filename:
        filename = max(glob('logs/*.log'), key=os.path.getctime)

    logging.basicConfig(level=logging.DEBUG, filename=filename, format=formatter)
    logging.getLogger("socketio").setLevel(logging.ERROR)
    logging.getLogger("engineio").setLevel(logging.ERROR)
    logging.getLogger("werkzeug").setLevel(logging.ERROR)

    # set up logging to console
    console = logging.StreamHandler()
    console.setLevel(logging.INFO)
    # set a format which is simpler for console use
    formatter = logging.Formatter(formatter)
    console.setFormatter(formatter)
    # add the handler to the root logger
    logging.getLogger(name).addHandler(console)

    return logging.getLogger(name)