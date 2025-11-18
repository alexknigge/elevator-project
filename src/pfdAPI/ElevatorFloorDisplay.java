package pfdAPI;

import mux.ElevatorMultiplexor;

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
    private final ElevatorMultiplexor mux;

    /**
     * Constructs the ElevatorFloorDisplay.
     * @param carId the ID of the associated elevator
     */
    public ElevatorFloorDisplay(int carId, ElevatorMultiplexor mux) {
        this.carId = carId;
        this.mux = mux;
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
        mux.emit("111-"+ carId + "-" + currentFloor, true);
        if(direction.equals("UP")){
            mux.emit("112-"+ carId +"-0" + "", true);
        }else if(direction.equals("DOWN")){
            mux.emit("112-"+ carId +"-2", true);
        } else{
            mux.emit("112-"+ carId +"-1", true);
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
