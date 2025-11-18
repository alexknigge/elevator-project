package pfdAPI;

import bus.Message;
import bus.SoftwareBus;
import bus.Topic;

/**
 * Class that defines the functionality of the Elevator doors. Represents
 * the pair of doors that open to a specific elevator on each floor.
 * API:
 *      public void open()
 *      public void close()
 *      public boolean isObstructed()
 *      public boolean isFullyOpen()
 *      public boolean isFullyClosed()
 */
public class ElevatorDoorsAssembly {
    // False when the doors are fully closed
    private boolean isOpen;
    // True when an obstruction is placed
    private boolean isObstructed;
    // Represents whether the doors are actively opening/closing
    private boolean isMoving;
    // The ID of the associated elevator
    private int carId;
    private final int OPEN = 0;
    private final int CLOSED = 1;
    private final int OBSTRUCTED = 0;
    private final int CLEAR = 1;
    private final SoftwareBus bus = new SoftwareBus(false);

    /**
     * Constructor of the ElevatorDoorsAssembly.
     * @param carId The ID of the associated elevator
     */
    public ElevatorDoorsAssembly(int carId) {
        this.isOpen = false;
        this.isObstructed = false;
        this.isMoving = false;
        this.carId = carId;
    }

    /**
     * Commands the door assembly to open.
     * If an obstruction is detected, opening is halted automatically.
     */
    public synchronized void open(){
        if(isObstructed) {
            System.out.println("[Doors] Obstruction detected.");
        }
        if (!isOpen) {
            isMoving = true;
            System.out.println("[Doors] Opening...");
            
            simulateDelay(2000);
            isOpen = true;
            isMoving = false;
            System.out.println("[Doors] Fully open.");
            bus.publish(new Message(Topic.DOOR_STATUS, carId, OPEN));

        }
    }

    /**
     * Commands the door assembly to close.
     * If obstruction occurs during closing, doors reopen automatically.
     */
    public synchronized void close() {
        if (isObstructed) {
            System.out.println("[Doors] Obstruction detected reopening.");
            bus.publish(new Message(Topic.DOOR_SENSOR, carId, OBSTRUCTED));
            open();
            return;
        }

        bus.publish(new Message(Topic.DOOR_SENSOR, carId, CLEAR));

        if (isOpen) {
            isMoving = true;
            System.out.println("[Doors] Closing...");
            //mux.getListener().onDoorStateChanged(carId, "CLOSING");
            simulateDelay(1000);
            if (!isObstructed) {
                isOpen = false;
                System.out.println("[Doors] Fully closed.");
                bus.publish(new Message(Topic.DOOR_STATUS, carId, CLOSED));
            } else {
                System.out.println("[Doors] Reopening due to obstruction.");
                open();
            }
            isMoving = false;
        }
    }

    /**
     * Simulates time delay for the open and close animations. For simulation purposes.
     * @param ms time to be elapsed
     */
    private synchronized void simulateDelay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Returns whether the doors are currently obstructed.
     * @return boolean isObstructed
     */
    public synchronized boolean isObstructed() {
        return isObstructed;
    }

    /**
     * Sets obstruction state manually for simulation/testing.
     * @param obstructed whether the GUI has the obstruction box present
     */
    public synchronized void setObstruction(boolean obstructed) {
        this.isObstructed = obstructed;
        if (obstructed) bus.publish(new Message(Topic.DOOR_SENSOR, carId, OBSTRUCTED));
        else bus.publish(new Message(Topic.DOOR_SENSOR, carId, CLEAR));

    }

    /**
     * Returns whether the doors are completely open (not closed, not half-open).
     * Elevator cannot be allowed to move.
     * @return boolean isOpen
     */
    public synchronized boolean isFullyOpen() {
        return isOpen;
    }

    /**
     * Returns whether the doors are completely closed (not open, not half-open).
     * Elevator can now move.
     * TODO: This returns true when the doors are half-open. This should not be possible. Need separate variables.
     * @return boolean isOpen
     */
    public synchronized boolean isFullyClosed() {
        return !isOpen;
    }


}
