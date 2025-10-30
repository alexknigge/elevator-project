class FloorCallButtons implements FloorCallButtonsAPI {

    // Optional GUI listener
    private static gui.listener guiListener = null;

    public static void setGuiListener(gui.listener l) {
        guiListener = l;
    }

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
            if (guiListener != null) guiListener.notify("FloorCall.pressUp", Integer.toString(floorNumber));
        }
    }

    /// Simulate pressing the Down call
    public void pressDownCall() {
        if (hasDown) {
            downPressed = true;
            if (guiListener != null) guiListener.notify("FloorCall.pressDown", Integer.toString(floorNumber));
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
    public void resetCallButton(String direction) {
        if (direction.equals("UP") && hasUp) {
            upPressed = false;
            if (guiListener != null) guiListener.notify("FloorCall.resetCallButton", "UP:" + floorNumber);
        } else if (direction.equals("DOWN") && hasDown) {
            downPressed = false;
            if (guiListener != null) guiListener.notify("FloorCall.resetCallButton", "DOWN:" + floorNumber);
        }
    }
}