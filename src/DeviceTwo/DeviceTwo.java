package DeviceTwo;

import DeviceComp.Device;
import DeviceComp.SoftwareBus;

public class DeviceTwo extends Device {
    private DeviceTwoDisplay display;
    private SoftwareBus softwareBus;
    private int deviceID = 2;

    public DeviceTwo(int portNumber) {
        super(portNumber);
        softwareBus = new SoftwareBus(this);
        display = new DeviceTwoDisplay(softwareBus, this);
    }

    public DeviceTwoDisplay getDisplay() {
        return display;
    }

    public int getDeviceID() {
        return deviceID;
    }
}