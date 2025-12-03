package ElevatorController.Notifier;

import Bus.SoftwareBus;
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

    public void arrivedAtFloor(Destination destination) {

    }

    public void elevatorStatus(Destination destination) {

    }

    /**
     * sends a bus message to play a noise because of too much weight
     */
    public void overloadOn() {

    }

    /**
     * sends a bus message to stop playing noise
     */
    public void overloadOff() {

    }


}
