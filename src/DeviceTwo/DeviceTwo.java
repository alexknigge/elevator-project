package DeviceTwo;

import DeviceComp.Device;
import DeviceComp.SoftwareBus;

public class DeviceTwo extends Device {
    private DeviceTwoDisplay display;
    private SoftwareBus softwareBus;
    private int deviceID = 2;

    public DeviceTwo(int portNumber) {
        super(portNumber);
        display = new DeviceTwoDisplay(this);
        softwareBus = new SoftwareBus();
    }

    public DeviceTwoDisplay getDisplay() {
        return display;
    }
    public int getDeviceID() {
        return deviceID;
    }
}