package ElevatorController.DoorAssembly;

import Bus.SoftwareBus;
import Bus.SoftwareBusCodes;
import Message.Message;
import PFDGUI.gui;

/**
 * The door assembly is a virtualization of the physical interfaces which
 * comprise the doors: fully open sensors, fully closed sensors, door
 * obstruction sensors, the scale, and the door motor. The door assembly posts
 * and receives messages from its physical counterparts via the software bus;
 * posting to the motor; receiving from the fully closed sensors, fully open
 * sensors, the scale, and the door obstruction sensors.
 */
public class DoorAssembly {
    private SoftwareBus softwareBus;
    private int currentElevatorId;
//    private boolean opened;
//    private boolean closed;
    private boolean obstructed;
    private boolean fullyClosed;
    private boolean fullyOpened;
    private boolean overCapacity;

    // Constants for topic codes
    private static final int TOPIC_DOOR_CONTROL = SoftwareBusCodes.doorControl;
    private static final int TOPIC_DOOR_SENSOR = SoftwareBusCodes.doorSensor;
    private static final int TOPIC_CABIN_LOAD = SoftwareBusCodes.cabinLoad;
    private static final int TOPIC_DOOR_STATUS = SoftwareBusCodes.doorStatus;

    //Constants for body codes
    private static final int OPEN_CODE = SoftwareBusCodes.doorOpen;
    private static final int CLOSE_CODE = SoftwareBusCodes.doorClose;
    private static final int OBSTRUCTED_CODE = SoftwareBusCodes.obstructed;
    private static final int NOT_OBSTRUCTED_CODE = SoftwareBusCodes.clear;
    private static final int OVER_CAPACITY_CODE = SoftwareBusCodes.overloaded;
    private static final int NOT_OVER_CAPACITY_CODE = SoftwareBusCodes.normal;



    public DoorAssembly(SoftwareBus softwareBus, int currentElevatorId) {
        this.softwareBus = softwareBus;
        this.currentElevatorId = currentElevatorId;
//        this.opened = true;
//        this.closed = false;
        this.obstructed = false;
        this.fullyClosed = false;
        this.fullyOpened = true;
        this.overCapacity = false;
    }

    public void open() {
        softwareBus.publish(new Message(TOPIC_DOOR_CONTROL, currentElevatorId, OPEN_CODE));

    }

    public void close() {
        softwareBus.publish(new Message(TOPIC_DOOR_CONTROL, currentElevatorId, CLOSE_CODE));
    }

    public boolean fullyOpen() {
        Message message = softwareBus.get(TOPIC_DOOR_STATUS, currentElevatorId);
        if (message != null ) {
            if (message.getBody() == OPEN_CODE) fullyOpened = true;
            if (message.getBody() == OPEN_CODE) fullyOpened = false;
        }
        return fullyOpened;
    }

    public boolean fullyClosed() {
        Message message = softwareBus.get(TOPIC_DOOR_STATUS, currentElevatorId);

        if (message != null ) {
            if (message.getBody() == OPEN_CODE) fullyClosed = false;
            if (message.getBody() == CLOSE_CODE) fullyClosed = true;
            else System.out.println("Unexpected body in SoftwareBusCodes.doorStatus Message in DoorAssembly: body = " + message.getBody());
        }

        return fullyClosed;
    }

    public boolean obstructed(){
        Message message = softwareBus.get(TOPIC_DOOR_SENSOR, currentElevatorId);
        if (message != null ) {
            if (message.getBody() == OBSTRUCTED_CODE) obstructed = true;
            if (message.getBody() == NOT_OBSTRUCTED_CODE) obstructed = false;
        }
        return obstructed;
    }

    public boolean overCapacity(){
        Message message =  softwareBus.get(TOPIC_CABIN_LOAD, currentElevatorId);
        if (message != null ) {
            if (message.getBody() == OVER_CAPACITY_CODE) overCapacity = true;
            if (message.getBody() == NOT_OVER_CAPACITY_CODE) overCapacity = false;
        }
        return overCapacity;
    }
}
