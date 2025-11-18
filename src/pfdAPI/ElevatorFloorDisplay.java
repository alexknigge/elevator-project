package pfdAPI;

import bus.Message;
import bus.SoftwareBus;
import bus.Topic;

/**
 * Class that defines the functionality of the Elevator floor displays. Represents
 * the panel above elevator doors that show the elevator's location and direction of movement.
 * API:
 *      public void updateFloorIndicator(int currentFloor, String direction)
 *      public void playArrivalChime()
 *      public void playOverLoadWarning()
 * For GUI purposes:
 *      public static void setGuiListener(gui.listener l)
 */
public class ElevatorFloorDisplay {
    // The current floor location of the elevator
    private int currentFloor;
    // The current movement direction of the elevator, "UP" "DOWN" "IDLE"
    private String direction;
    // The ID of the associated elevator
    private final int carId;
    // Direction constants for bus messages
    private final int UP = 0;
    private final int DOWN = 1;
    private final int IDLE = 2;
    private final SoftwareBus bus = new SoftwareBus(false);

    /**
     * Constructs the ElevatorFloorDisplay.
     * @param carId the ID of the associated elevator
     */
    public ElevatorFloorDisplay(int carId) {
        this.carId = carId;
        this.currentFloor = 1;
        this.direction = "IDLE";
    }

    /**
     * Updates the display to show the elevator's current floor and direction.
     * @param currentFloor the floor currently displayed
     * @param direction the direction the elevator is going (UP/DOWN/IDLE)
     */
    public synchronized void updateFloorIndicator(int currentFloor, String direction) {
        this.currentFloor = currentFloor;
        this.direction = direction;
        bus.publish(new Message(Topic.DISPLAY_FLOOR, carId, currentFloor));
        if(direction.equals("UP")){
            bus.publish(new Message(Topic.DISPLAY_DIRECTION, carId, UP));
        }else if(direction.equals("DOWN")){
            bus.publish(new Message(Topic.DISPLAY_DIRECTION, carId, DOWN));
        } else{
            bus.publish(new Message(Topic.DISPLAY_DIRECTION, carId, IDLE));
        }
    }

    /**
     * Simulates the arrival noise.
     */
    public synchronized void playArrivalChime() {
        // again simulating the Ding noise
        System.out.println("*Ding! Elevator has arrived at floor");
    }

    /**
     * Simulates the overload buzz.
     */
    public synchronized void playOverLoadWarning() {
        // simulating the buzzing noise
        System.out.println("*Buzz! Warning: Overload detected at floor" + currentFloor);
    }

    /**
     * TODO: Remove.
     * this next functions I'm just going to add just in case
     * we need them in the future they just return direction
     * and the current floor.
     */
    public synchronized int getCurrentFloor() {
        return currentFloor;
    }

    public synchronized String getDirection() {
        return direction;
    }
}
