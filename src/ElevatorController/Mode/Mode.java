package ElevatorController.Mode;

import Bus.SoftwareBus;
import ElevatorController.Util.Destination;
import ElevatorController.Util.State;

/**
 * The mode serves as a means for the Elevator Controller to be put into and track its current mode.
 * The mode is indirectly being updated by the Control Room, a separate entity outside of the Elevator Controller system.
 * Additionally, the mode is responsible for taking in demands from the Control Room when the elevator is being remotely controlled.
 * The mode object receives messages via the software bus but does not post messages to the software bus.
 */
public class Mode {
    private SoftwareBus softwareBus;
    private Destination currentDestination;

    private int elevatorID;

    public Mode(SoftwareBus softwareBus, int elevatorId) {
        this.softwareBus = softwareBus;
        this.elevatorID = elevatorId;

    }

    public State getMode() {

        return null;
    }

}
