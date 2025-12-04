package ElevatorController.Notifier;

import Bus.SoftwareBus;
import Bus.SoftwareBusCodes;
import Message.Message;
import ElevatorController.Util.Destination;
import ElevatorController.Util.State;

/**
 * The notifier object is used to communicate all necessary visual and audio
 * information. The notifier sends messages to the speakers, button lights, and
 * floor display (up/down arrows and LEDs for displaying the floor number). The
 * notifier object does not receive any messages from the Software Bus.
 */
public class Notifier {
    private SoftwareBus softwareBus;
    private int currentElevatorId;

    public Notifier(SoftwareBus softwareBus, int currentElevatorId) {
        this.softwareBus = softwareBus;
        this.currentElevatorId = currentElevatorId;
    }

    /**
     * sends a bus message to change the display to indicate that the
     * elevator has arrived to a floor
     *
     * @param destination Direction and current floor
     */
    public void arrivedAtFloor(Destination destination) {

        Message arrrivedAtFloorMessage = new Message(SoftwareBusCodes.displayFloor, currentElevatorId, destination.getFloor());
        softwareBus.publish(arrrivedAtFloorMessage);
    }

    /**
     * Send out current elevator status, specifically the direction the
     * elevator is moving and its current floor position
     * @param destination Direction and current floor
     */
    public void elevatorStatus(Destination destination) {
        int direction = destination.getDirection().getIntValue();
        int floorLevel = destination.floor();

        Message directionMessage = new Message(SoftwareBusCodes.displayDirection, currentElevatorId, direction);
        Message currentPositionMessage = new Message(SoftwareBusCodes.cabinPosition, currentElevatorId, floorLevel);

        Message commandCenterPosition = new Message(SoftwareBusCodes.elevatorStatus, currentElevatorId, floorLevel);
        Message commandCenterDirection = new Message(SoftwareBusCodes.elevatorStatus, currentElevatorId, direction);

        softwareBus.publish(directionMessage);
        softwareBus.publish(currentPositionMessage);
        softwareBus.publish(commandCenterPosition);
        softwareBus.publish(commandCenterDirection);
    }

    /**
     * sends a bus message to play a noise because of too much weight
     */
    public void overloadOn() {
        Message overLoadMessageOn = new Message(SoftwareBusCodes.playSound, currentElevatorId, 1);
        softwareBus.publish(overLoadMessageOn);
    }

    /**
     * sends a bus message to stop playing noise
     */
    public void overloadOff() {
        Message overLoadMessageOff;
    }


}
