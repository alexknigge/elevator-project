package DeviceComp;

import java.util.List;
import java.util.Map;

public class DeviceServer {
    //List of connected devices to the server
    private List<Device> connectedDevices;

    public DeviceServer() {

    }

    public void addDevice(Device device) {
        connectedDevices.add(device);
    }

}
