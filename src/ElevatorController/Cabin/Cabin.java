package ElevatorController.Cabin;

import Bus.SoftwareBus;
import Bus.SoftwareBusCodes;
import ElevatorController.Util.ConstantsElevatorControl;
import ElevatorController.Util.Timer;
import ElevatorController.Util.Destination;
import ElevatorController.Util.Direction;
import Message.Message;

public class Cabin implements Runnable {

    private final SoftwareBus softwareBus;
    private final int elevatorId;

    // Current state
    private int currentFloor = 1;
    private Direction currentDirection = Direction.STOPPED;
    private int currentDestination = 1;

    // Sensor alignment values
    private int topAlignment = 0;
    private int bottomAlignment = 0;

    // Motor, timing
    private boolean motorRunning = false;
    private Timer timeToStop = null;

    private static final int TOPIC_TOP_SENSOR    = SoftwareBusCodes.topSensor;
    private static final int TOPIC_BOTTOM_SENSOR = SoftwareBusCodes.bottomSensor;
    private static final int TOPIC_CAR_DISPATCH  = SoftwareBusCodes.carDispatch;
    private static final int TOPIC_CAR_STOP      = SoftwareBusCodes.carStop;

    public Cabin(SoftwareBus softwareBus, int elevatorId) {
        this.softwareBus = softwareBus;
        this.elevatorId = elevatorId;

        softwareBus.subscribe(TOPIC_TOP_SENSOR, elevatorId);
        softwareBus.subscribe(TOPIC_BOTTOM_SENSOR, elevatorId);

        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            step();
        }
    }

    /**
     * The working-branch movement cycle.
     */
    private synchronized void step() {

        drainTopSensor();
        drainBottomSensor();
        updateCurrentFloor();

        boolean alignedAtDestination =
                (currentDirection == Direction.DOWN
                        ? sensorToFloor(topAlignment) == currentDestination
                        : sensorToFloor(bottomAlignment) == currentDestination);

        if (motorRunning && alignedAtDestination) {

            if (timeToStop != null && timeToStop.timeout()) {
                stopMotor();
            } else if (timeToStop == null) {
                timeToStop = new Timer(ConstantsElevatorControl.TIME_TO_STOP);
            }

        } else if (!motorRunning && currentFloor != currentDestination) {

            updateCurrentDirection(currentDestination);
            startMotor(currentDirection);

        } else {
            timeToStop = null;
        }
    }

    private void drainTopSensor() {
        Message msg = softwareBus.get(TOPIC_TOP_SENSOR, elevatorId);
        while (msg != null) {
            topAlignment = msg.getBody();
            msg = softwareBus.get(TOPIC_TOP_SENSOR, elevatorId);
        }
    }

    private void drainBottomSensor() {
        Message msg = softwareBus.get(TOPIC_BOTTOM_SENSOR, elevatorId);
        while (msg != null) {
            bottomAlignment = msg.getBody();
            msg = softwareBus.get(TOPIC_BOTTOM_SENSOR, elevatorId);
        }
    }

    private void updateCurrentFloor() {
        if (currentDirection == Direction.UP) {
            currentFloor = bottomAlignment / 2 + 1;
        } else if (currentDirection == Direction.DOWN) {
            currentFloor = topAlignment / 2 + 1;
        }
    }

    /**
     * Converting sensor alignment to floor index.
     */
    private int sensorToFloor(int sensor) {
        return sensor / 2 + 1;
    }

    private void startMotor(Direction dir) {
        motorRunning = true;
        int code;

        if (dir == Direction.UP) {
            code = SoftwareBusCodes.up;
        } else if (dir == Direction.DOWN) {
            code = SoftwareBusCodes.down;
        } else {
            return;
        }

        softwareBus.publish(new Message(TOPIC_CAR_DISPATCH, elevatorId, code));
    }

    private void stopMotor() {
        motorRunning = false;
        softwareBus.publish(new Message(TOPIC_CAR_STOP, elevatorId, 0));
    }

    public void gotoFloor(int floor) {
        updateCurrentDirection(floor);
        currentDestination = floor;
    }

    public int getTargetFloor() {
        return currentDestination;
    }

    public boolean stopped() {
        return currentFloor == currentDestination;
    }

    public Destination getDestination() {
        return new Destination(currentFloor, currentDirection);
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    private void updateCurrentDirection(int floor) {
        if (currentFloor < floor) {
            currentDirection = Direction.UP;
        } else if (currentFloor > floor) {
            currentDirection = Direction.DOWN;
        } else {
            currentDirection = Direction.STOPPED;
        }
    }
}
