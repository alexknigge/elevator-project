package DeviceTwo;


import Message.Message;

public class DeviceTwoInput {
    private final DeviceTwoDisplay display;
    private final DeviceTwo device;
    public DeviceTwoInput(DeviceTwo device, DeviceTwoDisplay display) {
        this.device = device;
        this.display = display;
    }

    public void handleButtonClick(int count) {
        //When button is clicked, we should have a message be sent, simulates what happens in the elevator
        int deviceID = device.getDeviceID();
        String buttonNum = String.valueOf(count);
        Message newMessage = new Message(2, "ButtonClick", buttonNum, deviceID);

        device.sendMessage(newMessage);
        display.updateSendMessage(newMessage);
    }
}
