# EACare - Memory Bot

## Farmi

This furhat skill is dependent on an external system, Farmi.

*If you're on OS X:*

You might be able to run it locally or on a virtual linux machine.
One way of doing this is with paralells (https://www.parallels.com/blogs/linux-on-mac/).

*If you're on Windows:*

You might be able to use the linux sub-system 

#### Running locally

1. Clone the repository (or get it on your virtual machine)
2. Install dependencies 
```
sudo apt install python3 python3-pip portaudio19-dev
```
3. Create folders for logs
```$bash
cd path/to/ea-care-datacollection/
mkdir logs farmi_logs
```
4. create a python virtual env and install requirements 
```$bash
python3 -m venv path/to/ea-care-datacollection/venv
source path/to/ea-care-datacollection/venv/bin/activate
pip install -r path/to/ea-care-datacollection/recording/requirements.txt`
```
5. Start the farmi system

Activate venv
```
source path/to/ea-care-datacollection/venv/bin/activate
```

Start recording 
```
python path/to/ea-care-datacollection/recording/main.py
```

Start farmi server ( Should be in your path after activating venv )
```
farmi-server
```

#### Installing on furhat (Ubuntu 16.04)

1. copy over files (scp can be used if you're on mac) to furhat
2. Install dependencies
 
```
ssh furnix@<furhat-ip>
sudo add-apt-repository ppa:deadsnakes/ppa
sudo apt update
sudo apt install python3.6 python3.6-dev python3.6-venv portaudio19-dev
```

3. Create folders for logs
```$bash
cd path/to/ea-care-datacollection/
mkdir logs farmi_logs
```
4. create a python virtual env and install requirements 
```$bash
cd path/to/ea-care-datacollection/
python3.6 -m venv ./venv
source ./venv/bin/activate
pip install -r ./recording/requirements.txt`
```

#### Running on furhat

Activate venv
```
source path/to/ea-care-datacollection/venv/bin/activate
```

Start recording 
```
python path/to/ea-care-datacollection/recording/main.py
```

Start farmi-server ( should be in your path ) 
```
farmi-server
```

If you want to maintian the procesess after disconnecting from the shell, you can start them in a [tmux](https://github.com/tmux/tmux/wiki) session
