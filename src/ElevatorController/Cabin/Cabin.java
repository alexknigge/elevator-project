package ElevatorController.Cabin;

import Bus.SoftwareBus;
import Bus.SoftwareBusCodes;
import ElevatorController.Util.ConstantsElevatorControl;
import ElevatorController.Util.Timer;
import Message.Message;
import ElevatorController.Util.Destination;
import ElevatorController.Util.Direction;

/**
 * The cabin provides a means for the elevator controller to send the elevator to a destination.
 * The cabin indirectly controls the motor by sending messages to the Software Bus.
 * Additionally, the cabin indirectly receives messages from physical sensors through the Software Bus.
 */
public class Cabin implements Runnable {
    private SoftwareBus softwareBus;
    private int currentElevatorId;

    private int currentFloor;
    private Direction currentDirection; //or target
    private int currentDestination;

    private int topAlignment;
    private int bottomAlignment;

    private boolean motorStatus;

    private Timer timeToStop;

    public Cabin(SoftwareBus softwareBus, int currentElevatorId) {
        this.softwareBus = softwareBus;
        this.currentElevatorId = currentElevatorId;

        currentFloor = 0;
        currentDirection = Direction.STOPPED;
        currentDestination = 0;

        //Subcribe to elevator motion sensors
        softwareBus.subscribe(SoftwareBusCodes.topSensor, currentElevatorId);
        softwareBus.subscribe(SoftwareBusCodes.bottomSensor, currentElevatorId);

        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * Run the Cabin, updates the movement of the cabin
     */
    @Override
    public void run() {
        while (true) {
            moveElevator();
        }
    }

    /**
     * Move elevator towards target floor
     */
    private void moveElevator() {
        //Update current elevator alignment
        updateTopAlignment();
        updateBottomAlignment();
        updateCurrentFloor();

        boolean finalSensor;
        if (currentDirection == Direction.DOWN) {
            finalSensor = (sensorToFloor(topAlignment) == currentDestination);
        } else {
            finalSensor = (sensorToFloor(bottomAlignment) == currentDestination);
        }

        //If the motor is on, and we have stopped moving, we can turn of the
        // motor
        if (motorStatus && finalSensor) {
            if (timeToStop != null && timeToStop.timeout()) {
                stopElevatorMotor();
            } else if (timeToStop == null) {
                timeToStop = timeStop();
            }
            //Check if motor not turn on yet, if so need to turn on
        } else if (!motorStatus && currentFloor != currentDestination) {
            updateCurrentDirection(currentDestination);
            startElevatorMotor(currentDirection);
        } else {
            timeToStop = null;
        }
    }

    /**
     * Update top alignment value
     */
    private void updateTopAlignment() {
        Message message = softwareBus.get(currentElevatorId, SoftwareBusCodes.topSensor);
        topAlignment = message.getBody();
    }

    /**
     * Update bottom alignment value
     */
    private void updateBottomAlignment() {
        Message message = softwareBus.get(currentElevatorId, SoftwareBusCodes.bottomSensor);
        bottomAlignment = message.getBody();
    }

    private void updateCurrentFloor() {
        if (currentDirection == Direction.UP) {
            currentFloor = bottomAlignment / 2 + 1;
        } else if (currentDirection == Direction.DOWN) {
            currentFloor = topAlignment / 2 + 1;
        }
    }

    private void startElevatorMotor(Direction direction) {
        motorStatus = true;
        if (direction == Direction.UP) {
            softwareBus.publish(new Message(SoftwareBusCodes.carDispatch, currentElevatorId, SoftwareBusCodes.up));
        } else if (direction == Direction.DOWN) {
            softwareBus.publish(new Message(SoftwareBusCodes.carDispatch, currentElevatorId, SoftwareBusCodes.down));
        }
    }

    private void stopElevatorMotor() {
        motorStatus = false;
        softwareBus.publish(new Message(SoftwareBusCodes.carStop, currentElevatorId, 0));
    }

    private Timer timeStop() {
        return new Timer(ConstantsElevatorControl.TIME_TO_STOP);
    }

    /**
     * Update direction and destination value
     *
     * @param floor destination
     */
    public void gotoFloor(int floor) {
        //Determine the direction
        updateCurrentDirection(floor);
        currentDestination = floor;
    }

    /**
     * Get current target floor
     *
     * @return
     */
    public int getTargetFloor() {
        return currentDestination;
    }

    /**
     * Checks to see if the elevator has stopped moving
     *
     * @return True if not moving, else false
     */
    public boolean stopped() {
        //Can assume we stopped moving if we reached our destination
        return currentFloor == currentDestination;
    }

    /**
     * Return current floor and direction elevator is moving
     *
     * @return Current floor and direction
     */
    public Destination getDestination() {
        return new Destination(currentFloor, currentDirection);
    }

    /**
     * Return current elevator floor
     *
     * @return Elevator floor
     */
    public int getCurrentFloor() {
        return currentFloor;
    }

    /**
     * Translate sensor to floor
     * @param sensorPosition Sensor position
     * @return Floor number
     */
    private int sensorToFloor(int sensorPosition) {
        return sensorPosition / 2 + 1;
    }

    private void updateCurrentDirection(int floor) {
        if (currentFloor < floor) {
            currentDirection = Direction.UP;
        } else if (currentFloor > floor) {
            currentDirection = Direction.DOWN;
        } else if (currentFloor == floor) {
            currentDirection = Direction.STOPPED;
        }
    }


}
