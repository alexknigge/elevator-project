package DeviceOne;

import DeviceComp.Device;
import DeviceComp.SoftwareBus;

public class DeviceOne extends Device {
    private final DeviceOneDisplay display;
    private SoftwareBus softwareBus;
    private int deviceID = 1;


    public DeviceOne(int portNumber) {
        super(portNumber);
        softwareBus = new SoftwareBus(this);
        display = new DeviceOneDisplay(softwareBus, this);
    }

    public DeviceOneDisplay getDisplay() {
        return display;
    }

    public int getDeviceID() {
        return deviceID;
    }
}
