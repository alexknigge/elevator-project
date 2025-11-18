package mux;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bus.Message;
import bus.SoftwareBus;
import bus.Topic;
import javafx.application.Platform;

/**
 * Class that defines the BuildingMultiplexor, which coordinates communication from the Elevator
 * Command Center to the relevant devices. Communication is accomplished via the software bus,
 * and both the PFDs and the motion devices are subject to control.
 * 
 * Note: car and elevator are used interchangeably in this context.
 */
public class BuildingMultiplexor {

    public BuildingMultiplexor(){ 
        initialize(); 
    }

    // Listener for GUI/API integration
    private BuildingDeviceListener listener;
    public void setListener(BuildingDeviceListener listener) { this.listener = listener; }
    public BuildingDeviceListener getListener() { return this.listener; }

    private final Map<Integer, Integer> lastFloor = new ConcurrentHashMap<>(); // Last Floor (for bookkeeping)
    private final Map<Integer, Integer> lastDir = new ConcurrentHashMap<>(); // Last Direction (for bookkeeping)
    private final SoftwareBus bus = new SoftwareBus(false);

    // Initialize the MUX
    public void initialize() {
        System.out.println("ready bldg");
        bus.subscribe(Topic.DISPLAY_FLOOR, 0);
        bus.subscribe(Topic.DISPLAY_DIRECTION, 0);
        bus.subscribe(Topic.MODE_SET, 0);
        bus.subscribe(Topic.CALL_RESET, 0);
        bus.subscribe(Topic.HALL_CALL, 0);
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
                msg = bus.get(Topic.DISPLAY_FLOOR, 0);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.DISPLAY_DIRECTION, 0);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.MODE_SET, 0);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println("")); // Placeholder
                }
                msg = bus.get(Topic.CALL_RESET, 0);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.HALL_CALL, 0);
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
        System.out.println("Building-Image-Interaction: " + imageType + "[" + imageIndex + "] - " + interactionType + " : " + additionalData);
        if (listener != null) {
            Platform.runLater(() -> listener.onImageInteraction(imageType, imageIndex, interactionType, additionalData));
        }
    }

    // Pass information through the MUX to the console & publish to bus if needed
    public void emit(String msg, boolean publish) {
        System.out.println("Building-EMIT: " + msg);

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
    public interface BuildingDeviceListener {
        void onImageInteraction(String imageType, int imageIndex, String interactionType, String additionalData);

        void onDisplayUpdate(int carId, int floor, String text);
        void onCallCar(int floor, String direction);
        void onCallReset(int floor);

        void onFireAlarm(boolean active);
    }
}
