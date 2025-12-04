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

    public ElevatorController(int currentElevatorId) {
        this.softwareBus = new SoftwareBus(false);
        initElevatorController(currentElevatorId);
    }

    public State controlledMode() {

        buttons.disableCalls();
        buttons.enableSingleRequests();

        closeDoors();

        while (mode.getMode() == State.CONTROL) {
            System.out.println("in control");

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

        System.out.println("Fire mode is now on.");

        buttons.disableCalls();
        buttons.enableSingleRequests();
        closeDoors();

        Destination req = null;

        while (mode.getMode() == State.FIRE) {

            if (cabin.getCurrentFloor() == 1) {

                arrivalSequence(null);
                System.out.println("Fire mode: elevator parked at floor 1");

                // park the elevator here until fire cleared
                while (mode.getMode() == State.FIRE) {
                    try { Thread.sleep(50); } catch (Exception e) {}
                }

                System.out.println("Fire cleared. Exiting fire mode.");
                return mode.getMode();
            }

            if (req == null)
                req = buttons.nextService(cabin.getDestination());

            if (req != null)
                cabin.gotoFloor(req.floor());
            else
                cabin.gotoFloor(1);

            if (cabin.stopped()) {
                arrivalSequence(req);
                req = null;
            }

            try { Thread.sleep(10); } catch (Exception ignored) {}
        }

        return mode.getMode();
    }


    public State normalMode() {
        if (mode.getMode() != State.NORMAL) {
            System.out.println("not in normal mode anymore");
            return mode.getMode();
        }

        buttons.enableCalls();
        buttons.enableMultipleRequests();
        closeDoors();

        Destination pendingReq;
        Destination activeReq = null;

        while ( mode.getMode() == State.NORMAL) {

            if (activeReq == null) {
                pendingReq = buttons.nextService(cabin.getDestination());

                if (pendingReq != null) {
                    activeReq = pendingReq;
                    cabin.gotoFloor(activeReq.floor());
                }
            } else {
                cabin.gotoFloor(activeReq.floor());
            }

            if (activeReq != null && cabin.stopped()) {
                arrivalSequence(activeReq);
                buttons.clearCall(activeReq);

                activeReq = null;
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

    private State waitingMode() {
        if(mode.getMode()!= State.OFF) {
            return mode.getMode();
        }
        while(mode.getMode() == State.OFF) {
            //do nothing lol
        }
        System.out.println("i keft");

        return null;
    }


    private void beginInitialState() {
        running = true;
        while (running) {
            System.out.println("Elevator " + currentElevatorId + " is running.--------------------------------------------------------");
            State current = mode.getMode();   // DO NOT call normalMode() here first

            switch (current) {
                case NORMAL -> {
                    System.out.println("Normal Mode for " + currentElevatorId + " is running");
                    normalMode();   // one call, and it returns when mode changes
                }
                case FIRE -> {
                    System.out.println("Fire Mode for " + currentElevatorId + " is running");
                    fireMode();
                }
                case CONTROL -> {
                    System.out.println("Control Mode for " + currentElevatorId + " is running");
                    controlledMode();
                }
                case OFF -> {
                    //System.out.println("Off Mode for " + currentElevatorId
                    // + " is running");
                    waitingMode();
                }
                default -> {
                    running = false;
                }
            }
        }
    }

    private void initElevatorController(int elevatorId) {
        currentElevatorId = elevatorId;

        System.out.println("I am elevator " +  currentElevatorId + ", and I now exist.");

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
