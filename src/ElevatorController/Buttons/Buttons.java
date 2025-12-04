package ElevatorController.Buttons;

import Bus.SoftwareBus;
import Bus.SoftwareBusCodes;
import ElevatorController.Util.Destination;
import ElevatorController.Util.Direction;
import Message.Message;

import java.util.ArrayList;
import java.util.List;

public class Buttons {

    private final SoftwareBus softwareBus;
    private final int elevatorId;

    private boolean callEnabled = true;
    private boolean multipleRequests = true;
    private boolean fireKey = false;

    private Direction currentDirection = null;
    private int currentFloor = 1;

    private final List<Destination> destinations;

    private static final int TOPIC_HALL_CALL      = SoftwareBusCodes.hallCall;
    private static final int TOPIC_CABIN_SELECT   = SoftwareBusCodes.cabinSelect;
    private static final int TOPIC_FIRE_KEY       = SoftwareBusCodes.fireKey;

    private static final int TOPIC_RESET_CALL     = SoftwareBusCodes.resetCall;
    private static final int TOPIC_RESET_FLOOR    = SoftwareBusCodes.resetFloorSelection;

    private static final int TOPIC_CALLS_ENABLED  = SoftwareBusCodes.callsEnable;
    private static final int TOPIC_REQS_ENABLED   = SoftwareBusCodes.selectionsEnable;
    private static final int TOPIC_SELECTION_TYPE = SoftwareBusCodes.selectionsType;

    private static final int SUBTOPIC_BUILD_MUX   = SoftwareBusCodes.buildingMUX;

    public Buttons(SoftwareBus bus, int elevatorId) {
        this.softwareBus = bus;
        this.elevatorId = elevatorId;
        this.destinations = new ArrayList<>();

        softwareBus.subscribe(TOPIC_CABIN_SELECT, elevatorId);
        softwareBus.subscribe(TOPIC_HALL_CALL, elevatorId);
        softwareBus.subscribe(TOPIC_FIRE_KEY, elevatorId);
    }

    private void handleFireKey() {
        Message msg = softwareBus.get(TOPIC_FIRE_KEY, elevatorId);
        while (msg != null) {
            int body = msg.getBody();
            if (body == SoftwareBusCodes.active) fireKey = true;
            else if (body == SoftwareBusCodes.inactive) fireKey = false;
            msg = softwareBus.get(TOPIC_FIRE_KEY, elevatorId);
        }
    }

    private void handleCabinSelect() {
        Message msg = softwareBus.get(TOPIC_CABIN_SELECT, elevatorId);
        while (msg != null) {
            int floor = msg.getBody();
            destinations.add(new Destination(floor, null));
            msg = softwareBus.get(TOPIC_CABIN_SELECT, elevatorId);
        }
    }

    private void handleHallCall() {
        Message msg = softwareBus.get(TOPIC_HALL_CALL, elevatorId);
        Message last = null;
        while (msg != null) {
            last = msg;
            msg = softwareBus.get(TOPIC_HALL_CALL, elevatorId);
        }
        msg = last;

        if (msg == null) {
            return;
        }

        int destCode = msg.getBody();
        int floor;
        Destination dst;

        if (destCode >= 100) {
            floor = destCode - SoftwareBusCodes.upOffset;
            dst = new Destination(floor, Direction.UP);
        } else {
            floor = destCode - SoftwareBusCodes.downOffset;
            dst = new Destination(floor, Direction.DOWN);
        }

        if (floor < 1 || floor > 10) {
            System.out.println("ERROR in Buttons elevator " + elevatorId +
                    ": floor=" + floor + ", destCode=" + destCode);
        } else {
            destinations.add(dst);
        }

        msg = softwareBus.get(TOPIC_HALL_CALL, elevatorId);
    }

    public void clearCall(Destination dest) {
        if (dest == null || !destinations.contains(dest)) return;

        if (dest.direction() == null) {
            softwareBus.publish(new Message(TOPIC_RESET_FLOOR, elevatorId, dest.floor()));
            destinations.remove(dest);
            return;
        }

        int body;
        Direction d = dest.direction();
        int f = dest.floor();

        body = switch (f) {
            case 1  -> (d == Direction.UP ? SoftwareBusCodes.reset1Up  : SoftwareBusCodes.reset1Down);
            case 2  -> (d == Direction.UP ? SoftwareBusCodes.reset2Up  : SoftwareBusCodes.reset2Down);
            case 3  -> (d == Direction.UP ? SoftwareBusCodes.reset3Up  : SoftwareBusCodes.reset3Down);
            case 4  -> (d == Direction.UP ? SoftwareBusCodes.reset4Up  : SoftwareBusCodes.reset4Down);
            case 5  -> (d == Direction.UP ? SoftwareBusCodes.reset5Up  : SoftwareBusCodes.reset5Down);
            case 6  -> (d == Direction.UP ? SoftwareBusCodes.reset6Up  : SoftwareBusCodes.reset6Down);
            case 7  -> (d == Direction.UP ? SoftwareBusCodes.reset7Up  : SoftwareBusCodes.reset7Down);
            case 8  -> (d == Direction.UP ? SoftwareBusCodes.reset8Up  : SoftwareBusCodes.reset8Down);
            case 9  -> (d == Direction.UP ? SoftwareBusCodes.reset9Up  : SoftwareBusCodes.reset9Down);
            case 10 -> (d == Direction.UP ? SoftwareBusCodes.reset10Up : SoftwareBusCodes.reset10Down);
            default -> throw new IllegalStateException("Invalid floor: " + f);
        };

        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, body));
        destinations.remove(dest);
    }

    public void enableCalls() {
        softwareBus.publish(new Message(TOPIC_CALLS_ENABLED, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.on));
        callEnabled = true;
    }

    public void disableCalls() {
        softwareBus.publish(new Message(TOPIC_CALLS_ENABLED, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.off));
        callEnabled = false;
    }

    public void enableSingleRequests() {
        softwareBus.publish(new Message(TOPIC_REQS_ENABLED, elevatorId, SoftwareBusCodes.on));
        softwareBus.publish(new Message(TOPIC_SELECTION_TYPE, elevatorId, SoftwareBusCodes.single));
        multipleRequests = false;
    }

    public void enableMultipleRequests() {
        softwareBus.publish(new Message(TOPIC_REQS_ENABLED, elevatorId, SoftwareBusCodes.on));
        softwareBus.publish(new Message(TOPIC_SELECTION_TYPE, elevatorId, SoftwareBusCodes.multiple));
        multipleRequests = true;
    }

    public Destination nextService(Destination current) {

        handleCabinSelect();
        handleHallCall();
        handleFireKey();

        if (current != null) {
            currentFloor = current.floor();
            currentDirection = current.direction();
        }

        if (!callEnabled && !fireKey) {
            return null;
        }
        if (destinations.isEmpty()) {
            return null;
        }

        // Single-request mode
        if (!multipleRequests) {
            Destination first = destinations.getFirst();
            destinations.clear();
            destinations.add(first);
            return first;
        }

        if (currentDirection == Direction.STOPPED) {
            return findClosest();
        }

        if (currentDirection == Direction.UP) {
            Destination bestUp = null;
            for (Destination d : destinations) {
                if (d.floor() >= currentFloor) {
                    if (bestUp == null || d.floor() < bestUp.floor()) {
                        bestUp = d;
                    }
                }
            }

            if (bestUp != null) return bestUp;
        }

        if (currentDirection == Direction.DOWN) {
            Destination bestDown = null;
            for (Destination d : destinations) {
                if (d.floor() <= currentFloor) {
                    if (bestDown == null || d.floor() > bestDown.floor()) {
                        bestDown = d;
                    }
                }
            }

            if (bestDown != null) return bestDown;
        }

        return findClosest();
    }


    private Destination findClosest() {
        if (destinations.isEmpty()) return null;

        Destination closest = destinations.get(0);
        int bestDist = Math.abs(closest.floor() - currentFloor);

        for (Destination d : destinations) {
            int dist = Math.abs(d.floor() - currentFloor);
            if (dist < bestDist) {
                closest = d;
                bestDist = dist;
            }
        }

        return closest;
    }


}
