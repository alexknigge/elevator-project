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

    // Door Commands
    int DOOR_OPEN = 1;
    int DOOR_CLOSE = 2;

    int DIR_IDLE = 0;
    int DIR_UP = 1;
    int DIR_DOWN = 2;

    int FIRE_OFF = 0;
    int FIRE_ON = 1;

    // Initialize the MUX
    public void initialize() {
        System.out.println("ready bldg");
        bus.subscribe(Topic.FIRE_ALARM, 0);
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
                msg = bus.get(Topic.FIRE_ALARM, 0);
                if (msg != null) {
                    handleFireAlarm(msg);
                }
                msg = bus.get(Topic.CALL_RESET, 0);
                if (msg != null) {
                    handleCallReset(msg);
                }
                msg = bus.get(Topic.HALL_CALL, 0);
                if (msg != null) {
                    handleHallCall(msg);
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

    // Handle Hall Call Message
    public void handleHallCall(Message msg) {
        int floor = msg.getSubTopic();
        int directionCode = msg.getBody();
        String dir = "IDLE";
        if (directionCode == DIR_UP) { dir = "UP"; } 
        else if (directionCode == DIR_DOWN) { dir = "DOWN"; }
        listener.onCallCar(floor, dir);
    }

    // Handle Mode Set Message (Building Only Cares about Fire Safety)
    public void handleFireAlarm(Message msg) {
        int modeCode = msg.getBody();
        if (modeCode == FIRE_ON) {
            listener.onFireAlarm(true);
        } else if(modeCode == FIRE_OFF){
            listener.onFireAlarm(false);
        }
    }

    // Handle Call Reset Message
    public void handleCallReset(Message msg) {
        int floor = msg.getSubTopic();
        listener.onCallReset(floor);
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
