package ElevatorController;

import Bus.SoftwareBus;
import ElevatorController.Buttons.Buttons;
import ElevatorController.Cabin.Cabin;
import ElevatorController.DoorAssembly.DoorAssembly;
import ElevatorController.Mode.Mode;
import ElevatorController.Notifier.Notifier;
import ElevatorController.Util.Destination;
import ElevatorController.Util.State;
import ElevatorController.Util.Timer;

public class ElevatorController implements Runnable{
    private SoftwareBus softwareBus;
    private Cabin cabin;
    private Buttons buttons;
    private DoorAssembly doors;
    private Notifier notifications;
    private Mode mode;
    private int currentElevatorId;

    boolean running = false;

    public ElevatorController(int currentElevatorId, SoftwareBus softwareBus) {
        this.softwareBus = softwareBus;
        initElevatorController(currentElevatorId);
    }

    public State controlledMode() {

        buttons.disableCalls();
        buttons.enableSingleRequests();

        closeDoors();

        while (mode.getMode() == State.CONTROL) {

            Destination next = mode.nextService();
            if (next == null) continue;

            if (cabin.getTargetFloor() != next.floor()) {
                cabin.gotoFloor(next.floor());
            }

            if (cabin.stopped()) {
                arrivalSequence(next);
            }
        }

        return mode.getMode();
    }

    public State fireMode() {

        buttons.disableCalls();
        buttons.enableSingleRequests();

        closeDoors();

        Destination req = null;

        while (mode.getMode() == State.FIRE && cabin.getTargetFloor() != 1 && !cabin.stopped())
        {

            if (req == null)
                req = buttons.nextService(cabin.getDestination());

            if (req != null)
                cabin.gotoFloor(req.floor());
            else if (cabin.getTargetFloor() != 1)
                cabin.gotoFloor(1);

            if (cabin.stopped()) {
                arrivalSequence(req);
                req = null;
            }
        }

        return mode.getMode();
    }


    public State normalMode() {

        if (mode.getMode() != State.NORMAL) {
            return mode.getMode();
        }

        buttons.enableCalls();
        buttons.enableMultipleRequests();
        closeDoors();

        Destination req = null;

        while (mode.getMode() == State.NORMAL) {

            if (req == null) {
                req = buttons.nextService(cabin.getDestination());
            } else {
                cabin.gotoFloor(req.floor());
            }

            if (cabin.stopped() && req != null) {
                arrivalSequence(req);
                req = null;
            }
        }

        return mode.getMode();
    }

    private void arrivalSequence(Destination request) {
        if (request != null)
            buttons.clearCall(request);

        openDoors();
        waitDoorsOpen();
        closeDoors();
    }

    private void openDoors() {
        Timer t = new Timer(10000);

        doors.open();

        while (!doors.fullyOpen()) {
            if (t.timeout()) {
                doors.open();      // try once more
                if (t.timeout()) break;
            }
        }
    }

    private void waitDoorsOpen() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {}
    }

    private void closeDoors() {

        Timer timer = new Timer(10000);
        boolean playingNoise = false;

        doors.close();

        while (!doors.fullyClosed()) {

            if (doors.obstructed()) {
                doors.open();
                doors.close();
                continue;
            }

            if (doors.overCapacity()) {
                if (!playingNoise) {
                    notifications.overloadOn();
                    playingNoise = true;
                }
                doors.open();
                doors.close();
                continue;
            }

            if (timer.timeout()) break;
        }

        if (playingNoise) {
            notifications.overloadOff();
        }
    }


    private void beginInitialState() {
        running = true;
        while (running) {
            System.out.println("Elevator " + currentElevatorId + " is running.--------------------------------------------------------");
            State nowMode = normalMode();
            switch (nowMode) {
                case NORMAL -> {
                    System.out.println("Normal Mode for " + currentElevatorId + " is running");
                    normalMode();
                }
                case FIRE -> {
                    System.out.println("Fire Mode for " + currentElevatorId + " is running");
                    fireMode();
                }
                case CONTROL -> {
                    System.out.println("Control Mode for " + currentElevatorId + " is running");
                    controlledMode();
                }
                default -> {
                    running = false;
                }
            }
        }
    }

    private void initElevatorController(int elevatorId) {
        currentElevatorId = elevatorId;

        cabin = new Cabin(softwareBus, currentElevatorId);
        buttons = new Buttons(softwareBus, currentElevatorId);
        doors = new DoorAssembly(softwareBus, currentElevatorId);
        notifications = new Notifier(softwareBus, currentElevatorId);
        mode = new Mode(softwareBus, currentElevatorId);
    }

    @Override
    public void run() {
        beginInitialState();
    }
}
