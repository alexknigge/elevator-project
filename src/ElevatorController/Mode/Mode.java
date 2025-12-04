package ElevatorController.Mode;

import Bus.SoftwareBus;
import Bus.SoftwareBusCodes;
import ElevatorController.Util.Destination;
import ElevatorController.Util.State;
import Message.Message;

import static Message.Message.drain;

public class Mode {
    private SoftwareBus softwareBus;
    private Destination currentDestination;
    private State currentMode;
    private int currentElevatorId;

    public Mode(SoftwareBus softwareBus, int currentElevatorId) {
        this.softwareBus = softwareBus;
        this.currentElevatorId = currentElevatorId;
        this.currentDestination = null;
        this.currentMode = State.OFF;
        softwareBus.subscribe(SoftwareBusCodes.elevatorOnOff, currentElevatorId);
        softwareBus.subscribe(SoftwareBusCodes.setMode, currentElevatorId);
        softwareBus.subscribe(SoftwareBusCodes.setDestination, currentElevatorId);
        softwareBus.subscribe(SoftwareBusCodes.fireAlarmActive, currentElevatorId);
    }

    public State getMode() {

//        Message statusMessage = drain(SoftwareBusCodes.elevatorOnOff, currentElevatorId, softwareBus);
        Message statusMessage =
                softwareBus.get(SoftwareBusCodes.elevatorOnOff, currentElevatorId);
        //System.out.println("Message: " + statusMessage);

        if (statusMessage != null) {
            System.out.println("i got here");
            System.out.println(statusMessage);
        }
        if (statusMessage != null && statusMessage.getBody() == SoftwareBusCodes.off && currentMode != State.OFF) {
            currentMode = State.OFF;
            System.out.println("i go in here L");
            return currentMode;
        } else if( statusMessage != null && statusMessage.getBody() == SoftwareBusCodes.on && currentMode != State.ON) {
            currentMode = State.NORMAL;
            System.out.println("i go in here R");
            return currentMode;
        }

        Message fireMessage = null;
        if (currentMode != State.FIRE) {
            fireMessage = softwareBus.get(SoftwareBusCodes.fireAlarmActive, currentElevatorId);
        }

        if (fireMessage != null) {
            int body = fireMessage.getBody();
            if (body == SoftwareBusCodes.pulled) {
                currentMode = State.FIRE;
                softwareBus.publish(new Message(SoftwareBusCodes.fireMode,
                        currentElevatorId, SoftwareBusCodes.emptyBody));

                softwareBus.publish(new Message(SoftwareBusCodes.fireAlarm,
                        currentElevatorId, SoftwareBusCodes.emptyBody));

                softwareBus.publish(new Message(SoftwareBusCodes.fireAlarm,
                        SoftwareBusCodes.buildingMUX, SoftwareBusCodes.emptyBody));
            }
        }

        if (currentMode == State.FIRE) {
            return currentMode;
        }

        //Message modeMessage = drain(SoftwareBusCodes.setMode,
        // currentElevatorId, softwareBus);
        Message modeMessage = softwareBus.get(SoftwareBusCodes.setMode, currentElevatorId);

        if(modeMessage != null) {
            System.out.println("I got here");
            System.out.println(modeMessage);
        }

        if (modeMessage != null) {
            int body = modeMessage.getBody();
            if (body == SoftwareBusCodes.centralized)
                currentMode = State.CONTROL;
            else if (body == SoftwareBusCodes.normal)
                currentMode = State.NORMAL;
        }

        return currentMode;
    }


    public Destination nextService() {
        Message nextService = drain(SoftwareBusCodes.setDestination,  currentElevatorId, softwareBus);

        if (nextService == null) {
            return currentDestination;
        }

        int newFloor = nextService.getBody();
        Destination newDestination = new Destination(newFloor, null);
        currentDestination = newDestination;
        return newDestination;
    }

}
