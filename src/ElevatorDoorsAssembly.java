public class ElevatorDoorsAssembly {
    private boolean isOpen;
    private boolean isObstructed;
    private boolean isMoving;
    private int carId;

    // Optional GUI listener
    private static gui.listener guiListener = null;

    public static void setGuiListener(gui.listener l) {
        guiListener = l;
    }

    public ElevatorDoorsAssembly(int carId) {
        this.isOpen = false;
        this.isObstructed = false;
        this.isMoving = false;
        this.carId = carId;

    }

    /**
     * Commands the door assembly to open.
     * if an obstruction is detected, opening is halted automatically
     */
    public void open(){
        if(isObstructed) {
            System.out.println("[Doors} Cannot open - obstruction detected.");
            if (guiListener != null) guiListener.notify("Doors.openAttemptBlocked", null);
            return;
        }
        if (!isOpen) {
            isMoving = true;
            System.out.println("[Doors] Opening...");
            if (guiListener != null) guiListener.notify("Doors.opening", null);
            DeviceMultiplexor.getInstance().notifyDoorChanged(carId, "OPENING");
            simulateDelay(2000);
            isOpen = true;
            isMoving = false;
            System.out.println("[Doors] Fully open.");
            if (guiListener != null) guiListener.notify("Doors.opened", null);
            DeviceMultiplexor.getInstance().notifyDoorChanged(carId, "OPENED");

        }
    }

    /**
     * Commands the door assembly to close
     * If obstruction occurs during closing, doors reopen automatically
     */
    public void close() {
        if (isObstructed) {
            System.out.println("[Doors] obstruction detected reopening.");
            if (guiListener != null) guiListener.notify("Doors.closeBlockedObstruction", null);
            DeviceMultiplexor.getInstance().emitDoorSensor(carId, true);
            open();;
            return;
        }
        if (isOpen) {
            isMoving = true;
            System.out.println("[Doors] Closing...");
            if (guiListener != null) guiListener.notify("Doors.closing", null);
            DeviceMultiplexor.getInstance().notifyDoorChanged(carId, "CLOSING");
            simulateDelay(1000);
            if (!isObstructed) {
                isOpen = false;
                System.out.println("[Doors] Fully closed.");
                if (guiListener != null) guiListener.notify("Doors.closed", null);
                DeviceMultiplexor.getInstance().notifyDoorChanged(carId, "CLOSED");
            } else {
                System.out.println("[Doors] Reopening due to obstruction.");
                open();
            }
            isMoving = false;
        }
    }

    /**
     * simulates time delay for open and close animation
     * @param ms time
     */
    private void simulateDelay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     *  Returns if instruction is there.
     */
    public boolean isObstructed() {
        return isObstructed;
    }

    /**
     * sets obstruction state manually (for simulation/testing).
     */
    public void setObstruction(boolean obstructed) {
        this.isObstructed = obstructed;
        if (guiListener != null) guiListener.notify("Doors.obstructionSet", Boolean.toString(obstructed));
        DeviceMultiplexor.getInstance().emitDoorSensor(carId, obstructed);

    }

    /**
     * Return true if doors are fully open.
     */
    public boolean isFullyOpen() {
        return isOpen;
    }

    /**
     * Return true if doors are fully closed.
     */
    public boolean isFullyClosed() {
        return !isOpen;
    }


}
