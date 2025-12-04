package ElevatorController;

import Bus.SoftwareBus;
import ElevatorController.Buttons.Buttons;
import ElevatorController.Cabin.Cabin;
import ElevatorController.DoorAssembly.DoorAssembly;
import ElevatorController.Mode.Mode;
import ElevatorController.Notifier.Notifier;

public class ElevatorController {
    private SoftwareBus softwareBus;
    private Cabin cabin;
    private Buttons buttons;
    private DoorAssembly doors;
    private Notifier notifications;
    private Mode mode;
    private int currentElevatorId;

    public ElevatorController(int currentElevatorId) {
        initElevatorController(currentElevatorId);

    }

    private void beginInitialState() {

    }

    private void initElevatorController(int elevatorId) {
        currentElevatorId = elevatorId;
        softwareBus = new SoftwareBus(false);

        cabin = new Cabin(softwareBus, currentElevatorId);
        buttons = new Buttons(softwareBus, currentElevatorId);
        doors = new DoorAssembly(softwareBus, currentElevatorId);
        notifications = new Notifier(softwareBus, currentElevatorId);
        mode = new Mode(softwareBus, currentElevatorId);
    }
}
