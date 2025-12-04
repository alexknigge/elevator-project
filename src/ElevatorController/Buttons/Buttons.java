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

    public Buttons(SoftwareBus bus, int elevatorId) {
        this.softwareBus = bus;
        this.elevatorId = elevatorId;
        this.destinations = new ArrayList<>();

        softwareBus.subscribe(SoftwareBusCodes.cabinSelect, elevatorId);
        softwareBus.subscribe(SoftwareBusCodes.hallCall, elevatorId);
        softwareBus.subscribe(SoftwareBusCodes.fireKey, elevatorId);
    }

    private void handleFireKey() {
        Message msg = softwareBus.get(SoftwareBusCodes.fireKey, elevatorId);
        while (msg != null) {
            int body = msg.getBody();
            if (body == SoftwareBusCodes.active) fireKey = true;
            else if (body == SoftwareBusCodes.inactive) fireKey = false;
            msg = softwareBus.get(SoftwareBusCodes.fireKey, elevatorId);
        }
//        System.out.println("fire key handle went through");
    }

    private void handleCabinSelect() {
        Message msg = softwareBus.get(SoftwareBusCodes.cabinSelect, elevatorId);
        while (msg != null) {
            int floor = msg.getBody();
            destinations.add(new Destination(floor, null));
            msg = softwareBus.get(SoftwareBusCodes.cabinSelect, elevatorId);
        }
    }

    private void handleHallCall() {
        Message msg = softwareBus.get(SoftwareBusCodes.hallCall, elevatorId);
        Message last = null;
        while (msg != null) {
            last = msg;
            msg = softwareBus.get(SoftwareBusCodes.hallCall, elevatorId);
        }
        msg = last;

        if (msg == null) return;

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
    }

    public void clearCall(Destination dest) {
        if (dest == null || !destinations.contains(dest)) return;

        if (dest.direction() == null) {
            softwareBus.publish(new Message(
                    SoftwareBusCodes.resetFloorSelection,
                    elevatorId,
                    dest.floor()));
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

        softwareBus.publish(new Message(
                SoftwareBusCodes.resetCall,
                SoftwareBusCodes.buildingMUX,
                body));

        destinations.remove(dest);
    }

    public void enableCalls() {
        softwareBus.publish(new Message(
                SoftwareBusCodes.callsEnable,
                SoftwareBusCodes.buildingMUX,
                SoftwareBusCodes.on));
        callEnabled = true;
    }

    public void disableCalls() {
        softwareBus.publish(new Message(
                SoftwareBusCodes.callsEnable,
                SoftwareBusCodes.buildingMUX,
                SoftwareBusCodes.off));
        callEnabled = false;
    }

    public void enableSingleRequests() {
        softwareBus.publish(new Message(
                SoftwareBusCodes.selectionsEnable,
                elevatorId,
                SoftwareBusCodes.on));

        softwareBus.publish(new Message(
                SoftwareBusCodes.selectionsType,
                elevatorId,
                SoftwareBusCodes.single));

        multipleRequests = false;
    }

    public void enableMultipleRequests() {
        softwareBus.publish(new Message(
                SoftwareBusCodes.selectionsEnable,
                elevatorId,
                SoftwareBusCodes.on));

        softwareBus.publish(new Message(
                SoftwareBusCodes.selectionsType,
                elevatorId,
                SoftwareBusCodes.multiple));

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

        if (!callEnabled && !fireKey) return null;

        if (destinations.isEmpty()) return null;

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
