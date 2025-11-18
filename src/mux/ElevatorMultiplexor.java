package mux;

import bus.Message;
import bus.SoftwareBus;
import bus.Topic;
import javafx.application.Platform;
import pfdAPI.*;

/**
 * Class that defines the ElevatorMultiplexor, which coordinates communication from the Elevator
 * Command Center to the relevant devices. Communication is accomplished via the software bus,
 * and both the PFDs and the motion devices are subject to control.
 * 
 * Note: car and elevator are used interchangeably in this context.
 */
public class ElevatorMultiplexor {

    // Constructor
    public ElevatorMultiplexor(int ID){
        this.ID = ID;
        this.elev = new Elevator(ID, 10);
        initialize(); 
    }

    // Globals
    private final int ID;
    private final Elevator elev;
    private final SoftwareBus bus = new SoftwareBus(false);

    // Initialize the MUX  (placeholder example subscriptions)
    public void initialize() {
        bus.subscribe(Topic.DOOR_CONTROL, 0);
        bus.subscribe(Topic.DISPLAY_FLOOR, 0);
        bus.subscribe(Topic.DISPLAY_DIRECTION, 0);
        bus.subscribe(Topic.CAR_DISPATCH, 0);
        bus.subscribe(Topic.MODE_SET, 0);
        bus.subscribe(Topic.CABIN_SELECT, 0);
        bus.subscribe(Topic.CAR_POSITION, 0);
        bus.subscribe(Topic.DOOR_SENSOR, 0);
        bus.subscribe(Topic.DOOR_STATUS, 0);
        bus.subscribe(Topic.CABIN_LOAD, 0);
        bus.subscribe(Topic.FIRE_KEY, 0);
        startBusPoller();
    }


    /**
     * Incoming Event Handling Functions
     */

    // Polls the software bus for messages and handles them accordingly
    public void startBusPoller() {
        Thread t = new Thread(() -> {
            // keep polling
            while (true) {
                Message msg;
                msg = bus.get(Topic.DOOR_CONTROL, ID);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println("")); // Placeholder
                }
                msg = bus.get(Topic.DISPLAY_FLOOR, ID);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.DISPLAY_DIRECTION, ID);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.CAR_DISPATCH, ID);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.MODE_SET, 0);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.CABIN_SELECT, ID);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.CAR_POSITION, ID);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.DOOR_SENSOR, ID);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.DOOR_STATUS, ID);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.CABIN_LOAD, ID);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.FIRE_KEY, ID);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    /**
     * Outgoing Emitter Function
     */

    // Pass information through the MUX to the console & publish to bus if needed
    public void emit(String msg, boolean publish) {
        System.out.println("Elevator-EMIT: " + msg);

        // Publish to bus
        if(publish){ 
            Message message = Message.parseStringToMsg(msg); // Expects TOPIC-SUBTOPIC-BODY format
            bus.publish(message); 
        }
    }
}
