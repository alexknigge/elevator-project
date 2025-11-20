package pfdAPI;

import pfdGUI.gui;

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

    // Constants
    private final int OPEN = 0;
    private final int CLOSED = 1;
    private final int OBSTRUCTED = 0;
    private final int CLEAR = 1;

    // GUI Control reference
    private final gui.GUIControl guiControl;
    // Car ID reference
    private final int carId;

    /**
     * Constructor of the ElevatorDoorsAssembly.
     */
    public ElevatorDoorsAssembly(int carId, gui.GUIControl guiControl) {
        this.carId = carId;
        this.guiControl = guiControl;
        this.isOpen = true;
        this.isObstructed = false;
        this.isMoving = false;
    }

    /**
     * Commands the door assembly to open.
     * If an obstruction is detected, opening is halted automatically.
     */
    public synchronized void open() {
        // Do NOT start a new open if moving or obstructed
        if (isMoving) {
            System.out.println("[Doors] Cannot open - door is currently moving.");
            return;
        }
        if (isObstructed) {
            System.out.println("[Doors] Cannot open - obstruction detected.");
            return;
        }

        if (!isOpen) {
            isMoving = true;
            System.out.println("[Doors] Opening...");
            simulateDelay(500);
            isOpen = true;
            guiControl.changeDoorState(carId, true); // true = open
            isMoving = false;
            System.out.println("[Doors] Fully open.");
        }
    }


    /**
     * Commands the door assembly to close.
     * If obstruction occurs during closing, doors reopen automatically.
     */
    public synchronized void close() {
        if (isObstructed) {
            System.out.println("[Doors] Obstruction detected reopening.");
            isOpen = true;   
            open();
            return;
        }

        if (isOpen) {
            isMoving = true;
            System.out.println("[Doors] Closing...");
            //mux.getListener().onDoorStateChanged(carId, "CLOSING");
            simulateDelay(500);
            if (!isObstructed) {
                isOpen = false;
                guiControl.changeDoorState(carId, isOpen);
                System.out.println("[Doors] Fully closed.");
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
        guiControl.setDoorObstruction(carId, obstructed);
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
        return !isOpen && !isMoving;
    }


}
