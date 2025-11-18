package mux;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bus.Message;
import bus.SoftwareBus;
import bus.Topic;
import javafx.application.Platform;

/**
 * Class that defines the ElevatorMultiplexor, which coordinates communication from the Elevator
 * Command Center to the relevant devices. Communication is accomplished via the software bus,
 * and both the PFDs and the motion devices are subject to control.
 * 
 * Note: car and elevator are used interchangeably in this context.
 */
public class ElevatorMultiplexor {

    public ElevatorMultiplexor(){
        initialize(); 
    }

    // Listener for GUI/API integration
    private ElevatorDeviceListener listener;
    public void setListener(ElevatorDeviceListener listener) { this.listener = listener; }
    public ElevatorDeviceListener getListener() { return this.listener; }

    private final Map<Integer, Integer> lastFloor = new ConcurrentHashMap<>(); // Last Floor (for bookkeeping)
    private final Map<Integer, Integer> lastDir = new ConcurrentHashMap<>(); // Last Direction (for bookkeeping)
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
                msg = bus.get(Topic.DOOR_CONTROL, 0);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println("")); // Placeholder
                }
                msg = bus.get(Topic.DISPLAY_FLOOR, 0);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.DISPLAY_DIRECTION, 0);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.CAR_DISPATCH, 0);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.MODE_SET, 0);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.CABIN_SELECT, 0);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.CAR_POSITION, 0);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.DOOR_SENSOR, 0);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.DOOR_STATUS, 0);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.CABIN_LOAD, 0);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.FIRE_KEY, 0);
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
     * Outgoing Emitter Functions
     */

    // GUI image interaction tracking
    public void imgInteracted(String imageType, int imageIndex, String interactionType, String additionalData) {
        System.out.println("Elevator-Image-Interaction: " + imageType + "[" + imageIndex + "] - " + interactionType + " : " + additionalData);
        if (listener != null) {
            Platform.runLater(() -> listener.onImageInteraction(imageType, imageIndex, interactionType, additionalData));
        }
    }

    // Pass information through the MUX to the console & publish to bus if needed
    public void emit(String msg, boolean publish) {
        System.out.println("Elevator-EMIT: " + msg);

        // Publish to bus
        if(publish){ 
            Message message = Message.parseStringToMsg(msg); // Expects TOPIC-SUBTOPIC-BODY format
            bus.publish(message); 
        }
    }

    /**
     * Listener Functions for GUI reflection
     */

    // Device Listener Interface (Event handling)
    public interface ElevatorDeviceListener {
        void onImageInteraction(String imageType, int imageIndex, String interactionType, String additionalData);

        void onDisplayUpdate(int carId, int floor, String text);

        void onPanelButtonSelect(int carId, int floor);
        void onDoorStateChanged(int carId, String state);
        void onDoorObstructed(int carId, boolean blocked);

        void onCabinOverload(int carId, boolean overloaded);
    }
}
