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
        this.currentMode = State.NORMAL;

        softwareBus.subscribe(SoftwareBusCodes.elevatorOnOff, currentElevatorId);
        softwareBus.subscribe(SoftwareBusCodes.setMode, currentElevatorId);
        softwareBus.subscribe(SoftwareBusCodes.setDestination, currentElevatorId);
        softwareBus.subscribe(SoftwareBusCodes.fireAlarmActive, currentElevatorId);


    }

    public State getMode(){
        Message modeMessage = drain(SoftwareBusCodes.setMode, currentElevatorId, softwareBus);
        Message fireMessage = drain(SoftwareBusCodes.fireAlarmActive, currentElevatorId, softwareBus);
        Message statusMessage = drain(SoftwareBusCodes.elevatorOnOff, currentElevatorId, softwareBus);

        boolean fireJustActivated = false;
        int state;

        if (statusMessage != null) {
            state = statusMessage.getBody();
            if (state == SoftwareBusCodes.off) {
                currentMode = State.OFF;
                return currentMode;
            }
        }

        if (fireMessage != null) {
            state = fireMessage.getBody();
            if (state == SoftwareBusCodes.pulled) {
                if (currentMode != State.FIRE) {
                    fireJustActivated = true;
                }
                currentMode = State.FIRE;
            }
        }

        if (fireJustActivated) {
            System.out.println("from Mode, fire has just been activated");
            softwareBus.publish(new Message(SoftwareBusCodes.fireMode,
                    currentElevatorId, SoftwareBusCodes.emptyBody));

            softwareBus.publish(new Message(SoftwareBusCodes.fireAlarm,
                    currentElevatorId, SoftwareBusCodes.emptyBody));

            softwareBus.publish(new Message(SoftwareBusCodes.fireAlarm,
                    SoftwareBusCodes.buildingMUX, SoftwareBusCodes.emptyBody));
        }

        if (currentMode == State.FIRE) {
            return currentMode;
        }

        if (modeMessage != null) {
            state = modeMessage.getBody();
            switch (state) {
                case SoftwareBusCodes.centralized -> currentMode = State.CONTROL;
                case SoftwareBusCodes.normal      -> currentMode = State.NORMAL;
            }
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
