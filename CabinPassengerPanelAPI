import java.util.List;
import java.util.Collections;

public abstract class CabinPassengerPanelAPI {

    // Return all floor selections made since last poll, panel queues presses so none are missed.
    public List<Integer> getPressedFloors() {
        return Collections.emptyList();
    }

    // Clear the pending floor selections after they’ve been processed.
    public void clearPressedFloors() {
    }

    // Turn off the lamp for a serviced floor button.
    public void resetFloorButton(int floorNumber) {
    }

    // Update the in-cabin display with current floor and travel direction.
    public void setDisplay(int currentFloor, String direction) {
    }

    // Play the arrival chime (“ding”) upon arrival/leveling.
    public void playCabinArrivalChime() {
    }

    // Play the overload warning (“buzz”) when cabin load exceeds max.
    public void playCabinOverloadWarning() {
    }

    // Read fire service key switch state for emergency operations.
    public boolean isFireKeyActive() {
        return false;
    }
}

abstract class FloorCallButtonsAPI {

    // True if the landing panel’s “Up” call is active (not functonal for the top floor).
    public boolean isUpCallPressed() {
        return false;
    }

    // True if the landing panel’s “Down” call is active (not functional for the bottom floor).
    public boolean isDownCallPressed() {
        return false;
    }

    // Reset the specified call indicator ("Up" or "Down") after service.
    public void resetCallButton(String direction) {
    }
}
