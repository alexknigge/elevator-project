package ElevatorController.Buttons;

import Bus.SoftwareBus;
import ElevatorController.Util.Destination;

/**
 * The buttons object enables the Elevator Controller to track and schedule its destinations. The buttons object
 * indirectly receives floor requests via the physical buttons on the panel inside the cabin, as well as the call
 * buttons on each level. These button events are being received via the software bus.
 * The buttons object does not post any messages to the Software Bus.
 */
public class Buttons {
    private SoftwareBus softwareBus;
    private int currentElevatorId;

    public Buttons(SoftwareBus softwareBus, int currentElevatorId) {
        this.softwareBus = softwareBus;
        this.currentElevatorId = currentElevatorId;
    }

    public void clearCall(Destination destination) {

    }

    public void enableCalls() {

    }

    public void disableCalls() {

    }

    public void clearRequest(int floor) {

    }

    public void enableSingleRequests(int floor) {
    }

    public void enableMultipleRequests(int floor) {

    }

    public Destination nextService(Destination destination) {

        return null;
    }
}
