package ElevatorController.LowerLevel;

import Bus.SoftwareBus;
import ElevatorController.Util.ConstantsElevatorControl;
import ElevatorController.Util.Direction;
import ElevatorController.Util.FloorNDirection;
import ElevatorController.Util.Timer;

/**
 * The cabin provides a means for the elevator controller to send the elevator to a destination.
 * The cabin indirectly controls the motor by sending messages to the Software Bus.
 * Additionally, the cabin indirectly receives messages from physical sensors through the Software Bus.
 */
public class Cabin implements Runnable {
    private int currDest;
    private Direction currDirection;
    private int currFloor;
    private int topAlign;
    private int botAlign;
    private Timer timeStop;
    private SoftwareBus softwareBus;

    public Cabin(int elevatorID, SoftwareBus softwareBus){
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
        while (true) {
            stepTowardsDest();
            System.out.println("");
        }
    }

    public void gotoFloor(int floor){
        currDest = floor;
    }
    public FloorNDirection currentStatus(){return null;}
    public boolean arrived(){return false;}
    public int getTargetFloor(){return -1;}


    // Internal methods

    private synchronized void stepTowardsDest() {
        topAlign = topAlignment();
        botAlign = bottomAlignment();
        if (timeStop().timeout()) stopMotor();
        switch (currDirection) {
            case UP -> {
                if (sensorToFloor(botAlign) == currDest) {
                    timeStop = timeStop();
                }
            }
            case DOWN -> {
                if (sensorToFloor(topAlign) == currDest) {
                    timeStop = timeStop();
                }
            }
        }
    }
    private int closestFloor() {
        return 0;
    }

    private int sensorToFloor(int sensorPos) {
        return sensorPos/2 + 1;
    }

    private Timer timeStop() {
        return new Timer(ConstantsElevatorControl.TIME_TO_STOP);
    }

    //Wrapper methods for software bus messages
    private void startMotor() {}

    private void stopMotor() {}
    private void setDirection(Direction d){}

    private int topAlignment() {return 0;}
    private int bottomAlignment() {return 0;}

}