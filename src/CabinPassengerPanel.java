import java.util.ArrayList;
import java.util.List;

class CabinPassengerPanel implements CabinPassengerPanelAPI {

    // Optional GUI listener (static so the simple GUI can register once).
    private static gui.listener guiListener = null;

    public static void setGuiListener(gui.listener l) {
        guiListener = l;
    }

    private final int totalFloors;
    private final boolean[] floorButtons; // this is true if the button is pressed
    private final List<Integer> pressedFloorsQueue;
    private int currentFloor;
    private String direction; // "UP" "DOWN" and "IDLE"
    private boolean fireKeyActive;
    
    private final int carId;

    public CabinPassengerPanel(int carId, int totalFloors) {
        this.carId = carId;
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
            if (guiListener != null) guiListener.notify("Cabin.pressFloorButton", Integer.toString(floorNumber));
            DeviceMultiplexor.getInstance().emitCabinPanelClick(carId, carId - 1, floorNumber);
            DeviceMultiplexor.getInstance().emitCabinSelect(carId, floorNumber);
        }
    }

    /// Returns all pressed floor numbers since the last poll
    @Override
    public List<Integer> getPressedFloors() {
        if (guiListener != null) guiListener.notify("Cabin.getPressedFloors", null);
        return new ArrayList<>(pressedFloorsQueue);
    }
 
    /// Clears all stored pressed floor events (after being serviced)
    @Override
    public void clearPressedFloors() {
        pressedFloorsQueue.clear();
        if (guiListener != null) guiListener.notify("Cabin.clearPressedFloors", null);
    }

    /// Resets a specific floor button’s indicator
    @Override
    public void resetFloorButton(int floorNumber) {
        if (floorNumber >= 1 && floorNumber <= totalFloors) {
            floorButtons[floorNumber - 1] = false;
            if (guiListener != null) guiListener.notify("Cabin.resetFloorButton", Integer.toString(floorNumber));
            DeviceMultiplexor.getInstance().onDisplaySet(carId, currentFloor + " " + direction);
        }
    }

    /// Updates the cabin display to show the current floor and direction
    @Override
    public void setDisplay(int currentFloor, String direction) {
        this.currentFloor = currentFloor;
        this.direction = direction;
        System.out.println("Display: Floor " + currentFloor + " | Direction: " + direction);
        if (guiListener != null) guiListener.notify("Cabin.setDisplay", currentFloor + ":" + direction);
    }

    /// Plays the arrival chime sound
    @Override
    public void playCabinArrivalChime() {
        // for now im simulating a DING sound to see if it works
        System.out.println("*Ding!* Elevator arrived at floor " + currentFloor);
        if (guiListener != null) guiListener.notify("Cabin.playCabinArrivalChime", Integer.toString(currentFloor));
    }

    /// Plays the overload warning buzz
    @Override
    public void playCabinOverloadWarning() {
        // same as chime
        System.out.println("*Buzz!* Overload detected — please reduce cabin weight.");
        if (guiListener != null) guiListener.notify("Cabin.playCabinOverloadWarning", null);
    }

    /** Reads the fire key state */
    @Override
    public boolean isFireKeyActive() {
        return fireKeyActive;
    }

    /** Toggles the fire key (for simulation) */
    public void toggleFireKey() {
        this.fireKeyActive = !this.fireKeyActive;
        if (guiListener != null) guiListener.notify("Cabin.fireKeyToggled", Boolean.toString(this.fireKeyActive));
    }
}