package ElevatorController.LowerLevel;

import Bus.SoftwareBus;
import ElevatorController.Util.FloorNDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * The buttons object enables the Elevator Controller to track and schedule its destinations. The buttons object
 * indirectly receives floor requests via the physical buttons on the panel inside the cabin, as well as the call
 * buttons on each level. These button events are being received via the software bus.
 * The buttons object does not post any messages to the Software Bus.
 */
public class Buttons {
    private boolean callEnabled;
    private boolean multipleRequests;
    private List<FloorNDirection> destinations;
    private SoftwareBus softwareBus;


    public Buttons(SoftwareBus softwareBus) {
        //TODO may need to take in int for elevator number for software bus subscription
        //TODO call subscribe on softwareBus w/ relevant topic/subtopic

        this.callEnabled = true;
        this.multipleRequests = false;
        this.destinations = new ArrayList<>();
        this.softwareBus = softwareBus;
    }

    /**
     * Call publish on the softwareBUs with a message that the call button the gien floor, and given direction can be
     * turned off
     * @param floorNDirection The call button and direction which is no longer relevant
     */
    public void callReset(FloorNDirection floorNDirection) {}

    /**
     * Call publish on softwareBus with a message that the call button on the given floor, and given direction can be
     * turned off
     * @param floor the floor request button that is no longer relevant
     */
    public void requestReset(int floor) {}

    /**
     * In normal mode, level call buttons are enabled
     */
    public void enableCalls(){
        this.callEnabled = true;
    }

    /**
     * In fire mode, and controlled mode call buttons are disabled
     */
    public void disableCalls(){
        this.callEnabled = false;
    }

    /**
     * In Normal mode, all request buttons are enabled
     */
    public void enableAllRequests(){
        this.multipleRequests = true;
    }

    /**
     * In Fire mode, the request buttons in the cabin are mutually exclusive
     */
    public void enableSingleRequest(){
        this.multipleRequests = false;
    }


    /*
     * Note; call events have associated directions, and request events do not
     *
     * If multipleRequests enabled, keep calling get() on software bus, add them to associated lists, use given
     * floorNDirection to determine which is best.
     *
     * If multipleRequests are disabled, keep calling get() on software bus, ignore all but the most recent.
     *
     * If calls are disabled, ignore all call button presses (those associated with level)
     *
     * @param floorNDirection record holding the floor and direction
     * @return An int representing the floor????
     */
    public FloorNDirection nextService(FloorNDirection floorNDirection) {return null;}
}
