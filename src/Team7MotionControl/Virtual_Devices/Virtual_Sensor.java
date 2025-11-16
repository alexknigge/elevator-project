package Team7MotionControl.Virtual_Devices;

import Bus.SoftwareBus;
import Message.Message;
import javafx.application.Platform;

public class Virtual_Sensor {
    //Weather or not the sensor is aligned with the elevator
    private boolean triggered=false;

    private int currentTopic; // some topic that means sensors?

    private int currentSubtopic; // some topic that means this sensor?

    //UNLESS we want to do it like the body defines what sensor we're using and
    //this is handled more outside the house.

    private SoftwareBus softwareBus;

    /**
     *
     */

    /**
     * Returns if the sensor is triggered or not
     * @return
     */
    public boolean is_triggered() {
        //Query software bus
        return false;
    }

    private void checkForIncomingMessage() {
        Thread thread = new Thread(() -> {
            while (true) {
                Message message = softwareBus.get(currentTopic, currentSubtopic);
                if (message != null) {
                    Platform.runLater(() -> {
                        handleNewMessage(message);
                    });

                }
            }
        });
        thread.start();
    }

    /**
     *
     * @param message message sent by the hardware
     */

    private void handleNewMessage (Message message){
        //TODO For this topic and subtopic, should only be, on, off and direction

    }



}
