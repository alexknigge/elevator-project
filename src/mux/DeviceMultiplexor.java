package mux;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import bus.Message;
import bus.SoftwareBus;
import bus.Topic;
import javafx.application.Platform;
import pfdAPI.Elevator;

/**
 * Class that defines the DeviceMultiplexor, which coordinates communication from the Elevator
 * Command Center to the relevant devices. Communication is accomplished via the software bus,
 * and both the PFDs and the motor assembly devices are subject to control.
 */
public class DeviceMultiplexor {

    public DeviceMultiplexor(){ initialize(); }

    // Listener for GUI/API integration
    private DeviceListener listener;
    public void setListener(DeviceListener listener) { this.listener = listener; }
    public DeviceListener getListener() { return this.listener; }
    
    Set<Integer> cars = ConcurrentHashMap.newKeySet();
    private final Map<Integer, Elevator> carsById = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> lastFloor = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> lastDir = new ConcurrentHashMap<>();
    private final SoftwareBus bus = new SoftwareBus(false);


    // Register elevator so MUX can target it by id
    public void registerCar(Elevator elev) {
        if (elev == null) return;
        cars.add(elev.carId);
        carsById.put(elev.carId, elev);
    }

    // Initialize the MUX  (placeholder example subscriptions)
    public void initialize() {
        System.out.println("ready " + cars);
        bus.subscribe(Topic.DOOR_CON, 0);
        bus.subscribe(Topic.DISPLAY_FLOOR, 0);
        bus.subscribe(Topic.DISPLAY_DIR, 0);
        bus.subscribe(Topic.CAR_DISPATCH, 0);
        bus.subscribe(Topic.MODE_SET, 0);
        bus.subscribe(Topic.CALL_RESET, 0);
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
                msg = bus.get(Topic.DOOR_CON, 0);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println("")); // Placeholder
                }
                msg = bus.get(Topic.DISPLAY_FLOOR, 0);
                if (msg != null) {
                    Platform.runLater(() -> System.out.println(""));
                }
                msg = bus.get(Topic.DISPLAY_DIR, 0);
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
                msg = bus.get(Topic.CALL_RESET, 0);
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

    // Change the mode of a car or all cars (scale overload, fire safety, etc.)
    public void setMode(int carId, String mode) {
        if (listener != null) {
            Platform.runLater(() -> listener.onModeChanged(carId, mode));
        }
    }

    // GUI image interaction tracking
    public void imgInteracted(String imageType, int imageIndex, String interactionType, String additionalData) {
        System.out.println("Image interaction: " + imageType + "[" + imageIndex + "] - " + interactionType + " : " + additionalData);
        if (listener != null) {
            Platform.runLater(() -> listener.onImageInteraction(imageType, imageIndex, interactionType, additionalData));
        }
    }

    // Pass information through the MUX to the console & publish to bus if needed
    public void emit(String msg, boolean publish) {
        System.out.println("EMIT: " + msg);

        // Publish to bus
        if(publish){ 
            Message message = Message.parseStringToMsg(msg); // Expects TOPIC-SUBTOPIC-BODY format
            bus.publish(message); 
        }
    }

    /**
     * Listener Functions for GUI reflection
     */

    // Device Listener Interface
    public interface DeviceListener {
        void onDisplayUpdate(int carId, int floor, String text);
        void onDoorStateChanged(int carId, String state);
        void onCarArrived(int carId, int floor, String direction);
        void onCallReset(int floor);
        void onModeChanged(int carId, String mode);
        void onImageInteraction(String imageType, int imageIndex, String interactionType, String additionalData);

        void onHallCall(int floor, String direction);
        void onCabinSelect(int carId, int floor);
        void onDoorSensor(int carId, boolean blocked);
        void onCabinLoad(int carId, int weight);
        void onCarPosition(int carId, int floor, String direction);
    }

    // Notify gui of door state changes
    public void notifyDoorChanged(int carId, String state) {
        if (listener != null) {
            Platform.runLater(() -> listener.onDoorStateChanged(carId, state));
        }
    }
    
    // Notify gui to reset call button lights
    public void notifyCallReset(int floor) {
        if (listener != null) {
            Platform.runLater(() ->  listener.onCallReset(floor));
        }
    }
}
