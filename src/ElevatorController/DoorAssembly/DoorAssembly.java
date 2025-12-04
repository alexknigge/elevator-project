package ElevatorController.DoorAssembly;

import Bus.SoftwareBus;
import Bus.SoftwareBusCodes;
import Message.Message;

/**
 * Corrected DoorAssembly with proper message draining,
 * correct fullyOpen/fullyClosed logic, and correct sensor handling.
 */
public class DoorAssembly {

    private final SoftwareBus softwareBus;
    private final int currentElevatorId;

    private boolean obstructed;
    private boolean fullyClosed;
    private boolean fullyOpened;
    private boolean overCapacity;

    // Topic codes
    private static final int TOPIC_DOOR_CONTROL = SoftwareBusCodes.doorControl;
    private static final int TOPIC_DOOR_SENSOR  = SoftwareBusCodes.doorSensor;
    private static final int TOPIC_CABIN_LOAD   = SoftwareBusCodes.cabinLoad;
    private static final int TOPIC_DOOR_STATUS  = SoftwareBusCodes.doorStatus;

    // Body codes
    private static final int OPEN_CODE            = SoftwareBusCodes.doorOpen;
    private static final int CLOSE_CODE           = SoftwareBusCodes.doorClose;
    private static final int OBSTRUCTED_CODE      = SoftwareBusCodes.obstructed;
    private static final int NOT_OBSTRUCTED_CODE  = SoftwareBusCodes.clear;
    private static final int OVER_CAPACITY_CODE   = SoftwareBusCodes.overloaded;
    private static final int NOT_OVER_CAPACITY_CODE = SoftwareBusCodes.normal;

    public DoorAssembly(SoftwareBus softwareBus, int currentElevatorId) {
        this.softwareBus = softwareBus;
        this.currentElevatorId = currentElevatorId;

        obstructed = false;
        fullyClosed = false;
        fullyOpened = true;
        overCapacity = false;

        softwareBus.subscribe(TOPIC_DOOR_SENSOR, currentElevatorId);
        softwareBus.subscribe(TOPIC_CABIN_LOAD, currentElevatorId);
        softwareBus.subscribe(TOPIC_DOOR_STATUS, currentElevatorId);
    }

    public void open() {
        softwareBus.publish(new Message(TOPIC_DOOR_CONTROL, currentElevatorId, OPEN_CODE));
    }

    public void close() {
        softwareBus.publish(new Message(TOPIC_DOOR_CONTROL, currentElevatorId, CLOSE_CODE));
    }

    private Message drain(int topic) {
        Message msg = softwareBus.get(topic, currentElevatorId);
        Message last = null;

        while (msg != null) {
            last = msg;
            msg = softwareBus.get(topic, currentElevatorId);
        }
        return last;
    }

    public boolean fullyOpen() {
        Message msg = drain(TOPIC_DOOR_STATUS);
        if (msg != null) {
            int body = msg.getBody();
            if (body == OPEN_CODE) {
                fullyOpened = true;
                fullyClosed = false;
            } else if (body == CLOSE_CODE) {
                fullyOpened = false;
                fullyClosed = true;
            }
        }
        return fullyOpened;
    }

    public boolean fullyClosed() {
        Message msg = drain(TOPIC_DOOR_STATUS);
        if (msg != null) {
            int body = msg.getBody();
            if (body == CLOSE_CODE) {
                fullyClosed = true;
                fullyOpened = false;
            } else if (body == OPEN_CODE) {
                fullyClosed = false;
                fullyOpened = true;
            }
        }
        return fullyClosed;
    }

    public boolean obstructed() {
        Message msg = drain(TOPIC_DOOR_SENSOR);
        if (msg != null) {
            int body = msg.getBody();
            if (body == OBSTRUCTED_CODE) {
                obstructed = true;
            } else if (body == NOT_OBSTRUCTED_CODE) {
                obstructed = false;
            }
        }
        return obstructed;
    }

    public boolean overCapacity() {
        Message msg = drain(TOPIC_CABIN_LOAD);
        if (msg != null) {
            int body = msg.getBody();
            if (body == OVER_CAPACITY_CODE) {
                overCapacity = true;
            } else if (body == NOT_OVER_CAPACITY_CODE) {
                overCapacity = false;
            }
        }
        return overCapacity;
    }
}
