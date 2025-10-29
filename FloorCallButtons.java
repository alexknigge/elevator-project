class FloorCallButtons implements FloorCallButtonsAPI {

    private final int floorNumber;           // which landing this panel belongs to
    private final int totalFloors;           // total number of building floors
    private final boolean hasUp;             // top floor has no Up
    private final boolean hasDown;           // bottom floor has no Down

    private boolean upPressed;               // true if Up call is active
    private boolean downPressed;             // true if Down call is active

    public FloorCallButtons(int floorNumber, int totalFloors) {
        this.floorNumber = floorNumber;
        this.totalFloors = totalFloors;
        this.hasUp = floorNumber < totalFloors;
        this.hasDown = floorNumber > 1;
        this.upPressed = false;
        this.downPressed = false;
    }

    /// Simulate pressing the Up call
    public void pressUpCall() {
        if (hasUp) {
            upPressed = true;
        }
    }

    /// Simulate pressing the Down call
    public void pressDownCall() {
        if (hasDown) {
            downPressed = true;
        }
    }

    // True if the landing panel’s “Up” call is active (not functonal for the top floor).
    @Override
    public boolean isUpCallPressed() {
        return hasUp && upPressed;
    }

    // True if the landing panel’s “Down” call is active (not functional for the bottom floor).
    @Override
    public boolean isDownCallPressed() {
        return hasDown && downPressed;
    }

    // Reset the specified call indicator ("Up" or "Down") after service.
    @Override
    public void resetCallButton(Direction direction) {
        if (direction == Direction.UP && hasUp) {
            upPressed = false;
        } else if (direction == Direction.DOWN && hasDown) {
            downPressed = false;
        }
    }
}
