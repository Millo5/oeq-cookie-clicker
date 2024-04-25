from example.Python.WaterRowerConnector import WaterRowerConnector
from example.Python.WaterRowerConnection import WaterRowerConnection


def on_event():
    print("on")


def on_disconnect():
    print("disconnect")


def main():
    connection = WaterRowerConnection(None, on_event, on_disconnect)
    water_rower = WaterRowerConnector(connection)

    water_rower.connect()


if __name__ == "__main__":
    main()
