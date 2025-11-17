package ElevatorController.Util;

public class Timer {
    private long timeout;

    /**
     * Makes a new timer
     * @param timeout time to timeout millis
     */
    public Timer(long timeout) {
        this.timeout = System.currentTimeMillis() + timeout;
    }
    public boolean timeout() {
        return System.currentTimeMillis() > timeout;
    }
}