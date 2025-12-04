package ElevatorController.DoorAssembly;

import Bus.SoftwareBus;
import Bus.SoftwareBusCodes;
import Message.Message;

import static Message.Message.drain;

public class DoorAssembly {

    private final SoftwareBus softwareBus;
    private final int currentElevatorId;

    private boolean obstructed;
    private boolean fullyClosed;
    private boolean fullyOpened;
    private boolean overCapacity;

    public DoorAssembly(SoftwareBus softwareBus, int currentElevatorId) {
        this.softwareBus = softwareBus;
        this.currentElevatorId = currentElevatorId;

        obstructed = false;
        fullyClosed = false;
        fullyOpened = true;
        overCapacity = false;

        softwareBus.subscribe(SoftwareBusCodes.doorSensor, currentElevatorId);
        softwareBus.subscribe(SoftwareBusCodes.cabinLoad, currentElevatorId);
        softwareBus.subscribe(SoftwareBusCodes.doorStatus, currentElevatorId);
    }

    public void open() {
        softwareBus.publish(new Message(
                SoftwareBusCodes.doorControl,
                currentElevatorId,
                SoftwareBusCodes.doorOpen));
    }

    public void close() {
        softwareBus.publish(new Message(
                SoftwareBusCodes.doorControl,
                currentElevatorId,
                SoftwareBusCodes.doorClose));
    }

    public boolean fullyOpen() {
        return doorClosedOrOpened();
    }

    public boolean fullyClosed() {
        return doorClosedOrOpened();
    }

    private boolean doorClosedOrOpened() {
        Message msg = drain(SoftwareBusCodes.doorStatus, currentElevatorId, softwareBus);
        if (msg != null) {
            int body = msg.getBody();
            if (body == SoftwareBusCodes.doorClose) {
                fullyClosed = true;
                fullyOpened = false;
            } else if (body == SoftwareBusCodes.doorOpen) {
                fullyClosed = false;
                fullyOpened = true;
            }
        }
        return fullyClosed;
    }

    public boolean obstructed() {
        Message msg = drain(SoftwareBusCodes.doorSensor, currentElevatorId, softwareBus);

        if (msg != null) {
            int body = msg.getBody();
            if (body == SoftwareBusCodes.obstructed) {
                obstructed = true;
            } else if (body == SoftwareBusCodes.clear) {
                obstructed = false;
            }
        }
        return obstructed;
    }

    public boolean overCapacity() {
        Message msg = drain(SoftwareBusCodes.cabinLoad, currentElevatorId, softwareBus);
        if (msg != null) {
            int body = msg.getBody();
            if (body == SoftwareBusCodes.overloaded) {
                overCapacity = true;
            } else if (body == SoftwareBusCodes.normal) {
                overCapacity = false;
            }
        }
        return overCapacity;
    }
}
