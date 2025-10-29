package DeviceTwo;


import DeviceComp.SoftwareBus;
import Message.Message;

public class DeviceTwoInput {
    private final DeviceTwoDisplay display;
    private SoftwareBus softwareBus;
    private final DeviceTwo device;

    public DeviceTwoInput(SoftwareBus softwareBus,
                          DeviceTwo device, DeviceTwoDisplay display) {
        this.softwareBus = softwareBus;
        this.device = device;
        this.display = display;
    }

    public void handleButtonClick(int count) {
        //When button is clicked, we should have a message be sent, simulates what happens in the elevator
        int deviceID = device.getDeviceID();
        String buttonNum = String.valueOf(count);
        Message newMessage = new Message(2, "ButtonClick", buttonNum, deviceID);

        softwareBus.publish(newMessage);
        display.updateSendMessage(newMessage);
    }
}
