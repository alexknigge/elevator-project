package ElevatorController.Cabin;

import Bus.SoftwareBus;
import ElevatorController.Util.Destination;

/**
 * The cabin provides a means for the elevator controller to send the elevator to a destination.
 * The cabin indirectly controls the motor by sending messages to the Software Bus.
 * Additionally, the cabin indirectly receives messages from physical sensors through the Software Bus.
 */
public class Cabin {
    private SoftwareBus softwareBus;
    private int currentElevatorId;

    public Cabin(SoftwareBus softwareBus, int currentElevatorId) {
        this.softwareBus = softwareBus;
        this.currentElevatorId = currentElevatorId;
    }

    public void gotoFloor(int floor) {

    }

    public int getTargetFloor() {
        return 0;
    }

    public boolean stopped() {
        return false;
    }

    public int getCurrentFloor() {
        return 0;
    }

    public Destination getDestination() {
        return null;
    }

    public boolean overweightStatus() {
        return false;
    }


}
