public class ElevatorFloorDisplay {
    private int currentFloor;
    private String direction; // "UP" "DOWN" "IDLE"

    // Optional GUI listener
    private static gui.listener guiListener = null;

    public static void setGuiListener(gui.listener l) {
        guiListener = l;
    }

    public ElevatorFloorDisplay() {
        this.currentFloor = 1;
        this.direction = "IDLE";
    }

    /**
     * This updates the display to show the elevator's current floor and direction
     * @param currentFloor the floor currently displayed
     * @param direction the direction the elevator is going
     */
    public void updateFloorIndicator(int currentFloor, String direction) {
        this.currentFloor = currentFloor;
        this.direction = direction;
        System.out.println("[Display]");
        if (guiListener != null) guiListener.notify("FloorDisplay.update", currentFloor + ":" + direction);
    }

    /**
     * Simulates the arrival noise
     */
    public void playArrivalChime() {
        // again simulating the Ding noise
        System.out.println("*Ding! Elevator has arrived at floor");
        if (guiListener != null) guiListener.notify("FloorDisplay.arrivalChime", Integer.toString(currentFloor));
    }

    /**
     * simulates the overload buzz
     */
    public void playOverLoadWarning() {
        // simulating the buzzing noise
        System.out.println("*Buzz! Warning: Overload detected at floor" + currentFloor);
        if (guiListener != null) guiListener.notify("FloorDisplay.overloadWarning", Integer.toString(currentFloor));
    }

    /**
     * this next functions I'm just going to add just in case
     * we need them in the future they just return direction
     * and the current floor.
     */
    public int getCurrentFloor() {
        return currentFloor;
    }

    public String getDirection() {
        return direction;
    }
}
