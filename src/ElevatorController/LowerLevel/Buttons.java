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
    private List<FloorNDirection> callButtons;
    private List<Integer> requestButtons;
    private SoftwareBus softwareBus;

    public Buttons(SoftwareBus softwareBus) {
        //TODO may need to take in int for elevator number for software bus subscription
        //TODO call subscribe on softwareBus w/ relevant topic/subtopic

        this.callEnabled = true;
        this.multipleRequests = false;
        this.requestButtons = new ArrayList<>();
        this.callButtons = new ArrayList<>();
        this.softwareBus = softwareBus;
    }
    public void callReset(FloorNDirection floorNDirection) {}
    public void requestReset(int floor) {}

    public void enableCalls(){
        this.callEnabled = true;
    }

    public void disableCalls(){
        this.callEnabled = false;
    }

    public void enableAllRequests(){
        this.multipleRequests = true;
    }

    public void enableSingleRequest(){
        this.multipleRequests = false;
    }


    /*
     * If multipleRequests enabled, keep calling get() on software bus, add them to associated lists, use given
     * floorNDirection to determine which is best.
     *
     * @param floorNDirection record holding the floor and direction
     * @return An int representing the floor????
     */
    public FloorNDirection nextService(FloorNDirection floorNDirection) {return null;}
}
