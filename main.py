import time
import serial
import serial.tools.list_ports

from WaterRowerConnection import WaterRowerConnection

# File Write Prio
# Strokes pm
# Time
# Distance

info_dict = {}

def lerp(start, end, t):
    return start * (1 - t) + end * t

class Example:

    def __init__(self):
        self.port = None
        self.connection = None
        self.pulses = 0
        self.delta = 0
        info_dict["total_distance_m"] = info_dict["heart_rate"] = info_dict['total_strokes'] = info_dict['watts'] = info_dict['total_kcal'] = info_dict['avg_distance_cmps'] = info_dict['total_speed_cmps'] = info_dict['display_sec_dec'] = info_dict['display_sec'] = info_dict['display_min'] = info_dict['display_hr'] = info_dict['500mps'] = info_dict['stroke_rate'] = info_dict['avg_time_stroke_whole'] = info_dict['avg_time_stroke_pull'] = info_dict['tank_volume'] = 0

    def onDisconnect(self):
        self.connection = None

    def run(self):
        # Start monitoring in the background, calling onEvent when data comes in.
        self.connect()

        # This is where you run your main application. For instance, you could start a Flask app here,
        # run a GUI, do a full-screen blessed virtualization, or just about anything else.
        while self.connection:
            # print("Do awesome stuff here! Total pulses:", self.pulses)

            info_storage = open("WaterRowerDataPython.txt", 'w')
            self.delta = lerp(self.delta, self.pulses, 0.5)
            print()
            file_content = "delta:" + str(round(self.delta))
            for k, v in info_dict.items():
                file_content += "\n" + str(k) + ":" + str(v)

            info_storage.write(file_content + "\n")
            info_storage.close()
            print(" "*round(self.delta)+"-")
            self.pulses = 0
            self.connection.requestStatistic("total_distance_m")
            self.connection.requestStatistic("heart_rate")
            self.connection.requestStatistic("total_strokes")
            self.connection.requestStatistic("watts")
            self.connection.requestStatistic("total_kcal")
            self.connection.requestStatistic("avg_distance_cmps")
            self.connection.requestStatistic("total_speed_cmps")
            self.connection.requestStatistic("display_sec_dec")
            self.connection.requestStatistic("display_sec")
            self.connection.requestStatistic("display_min")
            self.connection.requestStatistic("display_hr")
            self.connection.requestStatistic("500mps")
            self.connection.requestStatistic("stroke_rate")
            self.connection.requestStatistic("avg_time_stroke_whole")
            self.connection.requestStatistic("avg_time_stroke_pull")
            self.connection.requestStatistic("tank_volume")
            time.sleep(0.05)

    def onEvent(self, event):
        """Called when any data comes."""
        if event["type"] == "pulse":
            self.pulses += event["value"]
        else:
            info_dict[event['type']] = event['value']


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
