package DeviceOne;

import Message.Message;


public class DeviceOneInput {
    private DeviceOne device;
    private DeviceOneDisplay display;
    public DeviceOneInput(DeviceOne device, DeviceOneDisplay display) {
        this.device = device;
        this.display = display;

    }
    public void handleSubmit() {
        String message = display.getMessageToBeSent().getText();
        if(!message.isEmpty()) {
            //If the text field isn't empty, we now need to create a message
            // object to be sent.
            String[] split = message.split("-");

            Message messageToBeSent = new Message();

            device.sendMessage(messageToBeSent);
        }

    }

}
