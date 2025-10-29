package DeviceOne;

import DeviceComp.Device;

public class DeviceOne extends Device {
    private final DeviceOneDisplay display;

    public DeviceOne(int portNumber) {
        super(portNumber);
        display = new DeviceOneDisplay(this);


    }

    public DeviceOneDisplay getDisplay() {
        return display;
    }
}
