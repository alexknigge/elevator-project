package ElevatorController.ElevatorStates;

import ElevatorController.Buttons.Buttons;
import ElevatorController.Cabin.Cabin;
import ElevatorController.DoorAssembly.DoorAssembly;
import ElevatorController.Mode.Mode;
import ElevatorController.Notifier.Notifier;

public class Controlled {
    public Controlled(Cabin cabin, Buttons buttons,  DoorAssembly doors, Notifier notifications, Mode mode) {
        buttons.disableCalls();
        buttons.enableSingleRequests();

    }
}
