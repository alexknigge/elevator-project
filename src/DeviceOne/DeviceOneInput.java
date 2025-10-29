package DeviceOne;

import DeviceComp.SoftwareBus;
import Message.Message;


public class DeviceOneInput {
    private SoftwareBus softwareBus;
    private DeviceOne device;
    private DeviceOneDisplay display;

    public DeviceOneInput(SoftwareBus softwareBus,
                          DeviceOne device, DeviceOneDisplay display) {
        this.softwareBus = softwareBus;
        this.device = device;
        this.display = display;
    }

    public void handleSubmit() {
        String message = display.getMessageToBeSent().getText();
        if (!message.isEmpty()) {
            //If the text field isn't empty, we now need to create a message
            // object to be sent.
            String[] split = message.split("-");
            if (split.length != 3) {
                //Invalid message size received, shouldn't do anything
                display.getMessageToBeSent().clear();
                return;
            }

            int topic = Integer.parseInt(split[0]);
            String subTopic = split[1];
            String body = split[2];
            int deviceID = device.getDeviceID();

            Message messageToBeSent = new Message(topic, subTopic, body, deviceID);
            display.handleSendMessage(messageToBeSent);

            softwareBus.publish(messageToBeSent);
        }
    }
}
