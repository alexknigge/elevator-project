import java.util.ArrayList;
import java.util.List;

public class CabinPassengerPanel {

    private final int totalFloors;
    private final boolean[] floorButtons; // this is true if the button is pressed
    private final List<Integer> pressedFloorsQueue;
    private int currentFloor;
    private String direction; // "UP" "Down" and "IDLE"
    private boolean fireKeyActive;

    public CabinPassengerPanel(int totalFloors) {
        this.totalFloors = totalFloors;
        this.floorButtons = new boolean[totalFloors];
        this.pressedFloorsQueue = new ArrayList<>();
        this.currentFloor = 1;
        this.direction = "IDLE";
        this.fireKeyActive = false;
    }

    /// Simulate pressing a floor button
    public void pressFloorButton(int floorNumber) {
        if (floorNumber >= 1 && floorNumber <= totalFloors && !floorButtons[floorNumber - 1]) {
            floorButtons[floorNumber - 1] = true;
            pressedFloorsQueue.add(floorNumber);
        }
    }

    /// Returns all pressed floor numbers since the last poll
    public List<Integer> getPressedFloors() {
        return new ArrayList<>(pressedFloorsQueue);
    }

    /// Clears all stored pressed floor events (after being serviced)
    public void clearPressedFloors() {
        pressedFloorsQueue.clear();
    }

    /// Resets a specific floor button’s indicator
    public void resetFloorButton(int floorNumber) {
        if (floorNumber >= 1 && floorNumber <= totalFloors) {
            floorButtons[floorNumber - 1] = false;
        }
    }

    /// Updates the cabin display to show the current floor and direction
    public void setDisplay(int currentFloor, String direction) {
        this.currentFloor = currentFloor;
        this.direction = direction;
        System.out.println("Display: Floor " + currentFloor + " | Direction: " + direction);
    }

    /// Plays the arrival chime sound
    public void playCabinArrivalChime() {
        // for now im simulating a DING sound to see if it works
        System.out.println("*Ding!* Elevator arrived at floor " + currentFloor);
    }

    /// Plays the overload warning buzz
    public void playCabinOverloadWarning() {
        // same as chime
        System.out.println("*Buzz!* Overload detected — please reduce cabin weight.");
    }

    /** Reads the fire key state */
    public boolean isFireKeyActive() {
        return fireKeyActive;
    }

    /** Toggles the fire key (for simulation) */
    public void toggleFireKey() {
        this.fireKeyActive = !this.fireKeyActive;
    }
}