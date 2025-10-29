package DeviceOne;

import DeviceComp.Device;
import DeviceComp.SoftwareBus;

public class DeviceOne extends Device {
    private final DeviceOneDisplay display;
    private SoftwareBus softwareBus;


    public DeviceOne(int portNumber) {
        super(portNumber);
        display = new DeviceOneDisplay(this);
        softwareBus = new SoftwareBus();
    }

    public DeviceOneDisplay getDisplay() {
        return display;
    }
}
