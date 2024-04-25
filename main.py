from example.Python.WaterRowerConnector import WaterRowerConnector
from example.Python.WaterRowerConnection import WaterRowerConnection

def onEvent():
    print("on")

def onDisconnect():
    print("disconnect")


def main():
    connection = WaterRowerConnection(None, onEvent, onDisconnect)
    water_rower = WaterRowerConnector(connection)

    water_rower.connect()


if __name__ == "__main__":
    main()
