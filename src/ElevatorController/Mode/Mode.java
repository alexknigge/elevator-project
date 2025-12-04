package ElevatorController.Mode;

import Bus.SoftwareBus;
import Bus.SoftwareBusCodes;
import ElevatorController.Util.Destination;
import ElevatorController.Util.State;
import Message.Message;

/**
 * The mode serves as a means for the Elevator Controller to be put into and track its current mode.
 * The mode is indirectly being updated by the Control Room, a separate entity outside of the Elevator Controller system.
 * Additionally, the mode is responsible for taking in demands from the Control Room when the elevator is being remotely controlled.
 * The mode object receives messages via the software bus but does not post messages to the software bus.
 */
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
        Message modeMessage = softwareBus.get(SoftwareBusCodes.setMode, currentElevatorId);
        Message fireMessage = softwareBus.get(SoftwareBusCodes.fireAlarmActive, currentElevatorId);
        Message statusMessage = softwareBus.get(SoftwareBusCodes.elevatorOnOff, currentElevatorId);

        int state;
        if(modeMessage!=null){
            state = modeMessage.getBody();
            switch (state){
                case SoftwareBusCodes.centralized -> currentMode = State.CONTROL;
                case SoftwareBusCodes.normal -> currentMode = State.NORMAL;
            }
        }
        if(fireMessage!=null){
            state = fireMessage.getBody();
            if (state == SoftwareBusCodes.pulled){
                softwareBus.publish(new Message(SoftwareBusCodes.fireMode, currentElevatorId, SoftwareBusCodes.emptyBody));
                currentMode = State.FIRE;
            }
        }

        if(statusMessage!=null){
            state = statusMessage.getBody();
            if (state == SoftwareBusCodes.off){
                currentMode = State.OFF;
            }
        }

        if (currentMode == State.FIRE) {
            softwareBus.publish(new Message(SoftwareBusCodes.fireAlarm, currentElevatorId, SoftwareBusCodes.emptyBody));
            softwareBus.publish(new Message(SoftwareBusCodes.fireAlarm, SoftwareBusCodes.buildingMUX, SoftwareBusCodes.emptyBody));
        }
        return currentMode;
    }

    public Destination nextService() {
        Message last = null;
        Message current;

        while ((current = softwareBus.get(SoftwareBusCodes.setDestination, currentElevatorId)) != null) {
            last = current;
        }

        if (last == null) {
            return currentDestination;
        }

        int newFloor = last.getBody();
        Destination newDestination = new Destination(newFloor, null);
        currentDestination = newDestination;
        return newDestination;
    }

}
