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
    // *** Topic Constants ***
    // From Command Center to Mode
    private static final int TOPIC_ON_OFF = SoftwareBusCodes.elevatorOnOff;
    private static final int TOPIC_MODE = SoftwareBusCodes.setMode;
    private static final int TOPIC_DESTINATION =
            SoftwareBusCodes.setDestination;
    // From Mode to Command Center
    private static final int TOPIC_FIRE_MODE = SoftwareBusCodes.fireMode;

    // From MUX to Mode
    private static final int TOPIC_FIRE_ALARM =
            SoftwareBusCodes.fireAlarmActive;
    // From Mode to MUX
    private static final int TOPIC_SET_FIRE = SoftwareBusCodes.fireAlarm;

    // Body for mode changes
    private static final int BODY_CENTRALIZED_MODE  = SoftwareBusCodes.centralized;
    private static final int BODY_NORMAL_MODE = SoftwareBusCodes.normal;
    private static final int BODY_FIRE_MODE = SoftwareBusCodes.fire;

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
        setCurrentMode();
        return currentMode;
    }

    private void setCurrentMode(){
        Message modeMessage = softwareBus.get(currentElevatorId,TOPIC_MODE);
        Message fireMessage = softwareBus.get(currentElevatorId,TOPIC_FIRE_ALARM);
        Message statusMessage = softwareBus.get(currentElevatorId,TOPIC_ON_OFF);

        int state;
        if(modeMessage!=null){
            state = modeMessage.getBody();
            switch (state){
                case BODY_CENTRALIZED_MODE -> currentMode = State.CONTROL;
                case BODY_NORMAL_MODE -> currentMode = State.NORMAL;
            }
        }
        if(fireMessage!=null){
            state = fireMessage.getBody();
            if (state == SoftwareBusCodes.pulled){
                softwareBus.publish(new Message(TOPIC_FIRE_MODE, currentElevatorId,
                        SoftwareBusCodes.emptyBody));
                currentMode = State.FIRE;
            }
        }

        if(statusMessage!=null){
            state = statusMessage.getBody();
            if (state == SoftwareBusCodes.off){
                currentMode = State.OFF;
            }
        }


        // Notify the MUX that the fire is active
        if (currentMode == State.FIRE) {
            softwareBus.publish(new Message(TOPIC_SET_FIRE, currentElevatorId,
                    SoftwareBusCodes.emptyBody));
            softwareBus.publish(new Message(TOPIC_SET_FIRE, SoftwareBusCodes.buildingMUX, SoftwareBusCodes.emptyBody));
        }

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
