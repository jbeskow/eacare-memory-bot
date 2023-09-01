# from common import FARMI_DIRECTORY_SERVICE_IP
from farmi import Publisher, Subscriber
from tobiiresearch.implementation.EyeTracker import EyeTracker, EYETRACKER_GAZE_DATA
import tobiiresearch.interop.interop as tr
#import threading
#git checkout master
#git fetch --all
#git reset --hard origin/master
import logging

logging.basicConfig(level=logging.INFO, filename=os.path.basename(__file__)[:-3]+'.log', format='%(asctime)s %(message)s')


started = False
# pub = FarmiUnit('tobii', local_save='farmi_logs', directory_service_ip=FARMI_DIRECTORY_SERVICE_IP)
pub = Publisher('tobii_eyetracker', local_save='farmi_logs', directory_service_address="tcp://192.168.1.180:5555")
#print("here8")

found_eyetrackers = tr.find_all_eyetrackers()
if not found_eyetrackers:
    raise Exception('no connected tobii trackers')
#print("here7")

my_eyetracker = found_eyetrackers[0]
et = EyeTracker(my_eyetracker.address)
#print("here6")


record = True

def gaze_callback(gaze_data):
    if record:
        #print(gaze_data)
        pub.send(gaze_data)
        print("dump to file", gaze_data)	

# tobii_handler()
#print("here1")

et.subscribe_to(EYETRACKER_GAZE_DATA, gaze_callback, as_dictionary=True)
#print("here2")
sub = Subscriber()


#def response(topic, time_, msg):
#    print(topic, time_, msg)
#    global record
#    if msg == ['start']:
#        print('recording')
#        record = True
#    elif msg == ['stop']:
#        # if camera:
#        #     camera.release()
#        # if out:
#            # out.release()
#
#        recording = False
#        sub.close()

sub.subscribe_to('start_stop', response)
input("press enter")
#threading.Thread(target=sub.listen).start()
et.unsubscribe_from(EYETRACKER_GAZE_DATA, gaze_callback)


















# et.subscribe_to(EYETRACKER_GAZE_DATA, gaze_data_callback, as_dictionary=True)
# time.sleep(5)
# et.unsubscribe_from(EYETRACKER_GAZE_DATA, gaze_data_callback)
