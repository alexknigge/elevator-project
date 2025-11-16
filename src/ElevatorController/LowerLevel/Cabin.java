package ElevatorController.LowerLevel;

import Bus.SoftwareBus;
import ElevatorController.Util.Direction;
import ElevatorController.Util.FloorNDirection;

/**
 * The cabin provides a means for the elevator controller to send the elevator to a destination.
 * The cabin indirectly controls the motor by sending messages to the Software Bus.
 * Additionally, the cabin indirectly receives messages from physical sensors through the Software Bus.
 */
public class Cabin implements Runnable {
    private int currDest;
    private Direction currDirection;
    private SoftwareBus softwareBus;

    public Cabin(SoftwareBus softwareBus){
        //TODO may need to take in int for elevator number for software bus subscription
        //TODO call subscribe on softwareBus w/ relevant topic/subtopic

        this.softwareBus = softwareBus;
        this.currDest = 0;
        this.currDirection = Direction.STOPPED;

        //Start Cabin Thread
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * Run the Cabin
     */
    @Override
    public void run() {

    }

    public void gotoFloor(int floor){}
    public FloorNDirection currentStatus(){return null;}
    public boolean arrived(){return false;}
    public int getTargetFloor(){return -1;}

}
