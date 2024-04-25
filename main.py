import time
import serial
import serial.tools.list_ports

from WaterRowerConnection import WaterRowerConnection

INFO_STORAGE = open("WaterRowerDataPython.txt", 'w')

def lerp(start, end, t):
    return start * (1 - t) + end * t

class Example:

    def __init__(self):
        self.port = None
        self.connection = None
        self.pulses = 0
        self.delta = 0

    def onDisconnect(self):
        self.connection = None

    def run(self):
        # Start monitoring in the background, calling onEvent when data comes in.
        self.connect()

        # This is where you run your main application. For instance, you could start a Flask app here,
        # run a GUI, do a full-screen blessed virtualization, or just about anything else.
        while self.connection:
            # print("Do awesome stuff here! Total pulses:", self.pulses)
            self.delta = lerp(self.delta, self.pulses, 0.5)
            print()
            file_content = "Delta: " + str(round(self.delta))
            INFO_STORAGE.write(file_content + "\n")
            print(" "*round(self.delta)+"-")
            self.pulses = 0
            self.connection.requestStatistic("total_distance_m")
            self.connection.requestStatistic("heart_rate")
            time.sleep(0.05)

    def onEvent(self, event):
        """Called when any data comes."""
        if event["type"] == "pulse":
            self.pulses += event["value"]
        else:
            pass
            # print('event', event, flush=True)


    def connect(self):
        """This will start a thread in the background, that will call the onEvent method whenever data comes in."""
        print("Connecting to WaterRower...")
        self.port = self.findPort()
        print("Connecting to WaterRower on port %s" % self.port)
        self.connection = WaterRowerConnection(self.port, self.onDisconnect, self.onEvent)

    def findPort(self):
        attempts = 0
        while True:
            attempts += 1
            ports = serial.tools.list_ports.comports()
            for path, name, _ in ports:
                if "WR" in name:
                    print("port found: %s" % path)
                    return path

            # message every ~10 seconds
            if attempts % 10 == 0:
                print("Port not found in %d attempts; retrying every 5s" % attempts)

            time.sleep(1)


Example().run()
