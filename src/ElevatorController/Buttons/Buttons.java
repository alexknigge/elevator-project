package ElevatorController.Buttons;

import Bus.SoftwareBus;
import Bus.SoftwareBusCodes;
import ElevatorController.Util.Destination;
import ElevatorController.Util.Direction;
import Message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * The buttons object enables the Elevator Controller to track and schedule its destinations. The buttons object
 * indirectly receives floor requests via the physical buttons on the panel inside the cabin, as well as the call
 * buttons on each level. These button events are being received via the software bus.
 * The buttons object does not post any messages to the Software Bus.
 */
public class Buttons {
    private boolean callEnabled;
    private SoftwareBus softwareBus;
    private int currentElevatorId;
    private Direction currentDirection;
    private int currentFloor;
    private boolean multipleRequests;
    private List<Destination> destinations;


    //  *** Software Bus Topics ***
    // Receiving from MUX
    private final static int TOPIC_HALL_CALL = SoftwareBusCodes.hallCall; // buttons in the halls
    private final static int TOPIC_CABIN_SELECT = SoftwareBusCodes.cabinSelect; // button events in the cabin
    private final static int TOPIC_FIRE_KEY = SoftwareBusCodes.fireKey; //TODO: handle fire key messages

    //Sending to MUX
    private final static int TOPIC_RESET_CALL = SoftwareBusCodes.resetCall;
    private final static int RESET_FLOOR_SELECTION = SoftwareBusCodes.resetFloorSelection;
    private final static int TOPIC_CALLS_ENABLED = SoftwareBusCodes.callsEnable;
    private final static int TOPIC_REQS_ENABLED =
            SoftwareBusCodes.selectionsEnable;
    private final static int TOPIC_SELECTION_TYPE =
            SoftwareBusCodes.selectionsType;

    //Subtopic for sending to Building MUX
    private final static int SUBTOPIC_BUILD_MUX = SoftwareBusCodes.buildingMUX;

    // Bodies for the fire key
//    private final static int BODY_F_KEY_ACTIVE = SoftwareBusCodes.active;
//    private final static int BODY_F_KEY_INACTIVE = SoftwareBusCodes.inactive;


    //TODO will likely need to deal with fire/firekey within the code
    public Buttons(SoftwareBus softwareBus, int currentElevatorId) {
        this.softwareBus = softwareBus;
        this.currentElevatorId = currentElevatorId;

        //TODO may need to add id for elevators

        // Assuming normal mode settings initially
        this.callEnabled = true;
        this.multipleRequests = true;

        this.destinations = new ArrayList<>();
        this.softwareBus = softwareBus;
        this.currentElevatorId = currentElevatorId;

        // Subscribing
        softwareBus.subscribe(TOPIC_CABIN_SELECT, currentElevatorId);
        softwareBus.subscribe(TOPIC_HALL_CALL, currentElevatorId);
        softwareBus.subscribe(TOPIC_FIRE_KEY, currentElevatorId);

    }

    /**
     * Call publish on the softwareBus with a message that the call button of the given floor, and given direction can be
     * turned off
     * Remove that floor from destinations
     *
     * @param destination The call button and direction which is no longer relevant
     */
    public void clearCall(Destination destination) {
        if (destination == null) return;
        if (!destinations.contains(destination)) return;

        //Cabin Button Reset
        if (destination.direction() == null) {
            softwareBus.publish(new Message(RESET_FLOOR_SELECTION, currentElevatorId, destination.floor()));
            destinations.remove(destination);
            return;
        }

        //Floor Button Reset

        int floor = destination.floor();
        if (floor == 1) {
            switch (destination.direction()) {
                case UP ->
                        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset1Up));
                case DOWN ->
                        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset1Down));
                // if direction is not up or down handle with grace!
                default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
            }
        } else if (floor == 2) {
            switch (destination.direction()) {
                case UP ->
                        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset2Up));
                case DOWN ->
                        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset2Down));
                // if direction is not up or down handle with grace!
                default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
            }
        } else if (floor == 3) {
            switch (destination.direction()) {
                case UP ->
                        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset3Up));
                case DOWN ->
                        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset3Down));
                // if direction is not up or down handle with grace!
                default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
            }
        } else if (floor == 4) {
            switch (destination.direction()) {
                case UP ->
                        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset4Up));
                case DOWN ->
                        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset4Down));
                // if direction is not up or down handle with grace!
                default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
            }
        } else if (floor == 5) {
            switch (destination.direction()) {
                case UP ->
                        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset5Up));
                case DOWN ->
                        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset5Down));
                // if direction is not up or down handle with grace!
                default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
            }
        } else if (floor == 6) {
            switch (destination.direction()) {
                case UP ->
                        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset6Up));
                case DOWN ->
                        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset6Down));
                // if direction is not up or down handle with grace!
                default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
            }
        } else if (floor == 7) {
            switch (destination.direction()) {
                case UP ->
                        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset7Up));
                case DOWN ->
                        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset7Down));
                // if direction is not up or down handle with grace!
                default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
            }
        } else if (floor == 8) {
            switch (destination.direction()) {
                case UP ->
                        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset8Up));
                case DOWN ->
                        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset8Down));
                // if direction is not up or down handle with grace!
                default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
            }
        } else if (floor == 9) {
            switch (destination.direction()) {
                case UP ->
                        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset9Up));
                case DOWN ->
                        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset9Down));
                // if direction is not up or down handle with grace!
                default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
            }
        } else if (floor == 10) {
            switch (destination.direction()) {
                case UP ->
                        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset10Up));
                case DOWN ->
                        softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset10Down));
                // if direction is not up or down handle with grace!
                default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
            }
        } else {
            throw new IllegalStateException("Unexpected value: " + destination.direction());
        }


//        switch(destination.floor()){
////            case UP -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, 0));
////            case DOWN -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, 1));
////            // if direction is not up or down handle with grace!
////            default -> throw new IllegalStateException("Unexpected value: " + floorNDirection.direction());
//            case 1 -> {
//                switch(destination.direction()) {
//                    case UP -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset1Up));
//                    case DOWN -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset1Down));
//                    // if direction is not up or down handle with grace!
//                    default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
//                }
//            }
//            case 2 -> {
//                switch(destination.direction()) {
//                    case UP -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset2Up));
//                    case DOWN -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset2Down));
//                    // if direction is not up or down handle with grace!
//                    default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
//                }
//            }
//            case 3 -> {
//                switch(destination.direction()) {
//                    case UP -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset3Up));
//                    case DOWN -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset3Down));
//                    // if direction is not up or down handle with grace!
//                    default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
//                }
//            }
//            case 4 -> {
//                switch(destination.direction()) {
//                    case UP -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset4Up));
//                    case DOWN -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset4Down));
//                    // if direction is not up or down handle with grace!
//                    default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
//                }
//            }
//            case 5 -> {
//                switch(destination.direction()) {
//                    case UP -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset5Up));
//                    case DOWN -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset5Down));
//                    // if direction is not up or down handle with grace!
//                    default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
//                }
//            }
//            case 6 -> {
//                switch(destination.direction()) {
//                    case UP -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset6Up));
//                    case DOWN -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset6Down));
//                    // if direction is not up or down handle with grace!
//                    default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
//                }
//            }
//            case 7 -> {
//                switch(destination.direction()) {
//                    case UP -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset7Up));
//                    case DOWN -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset7Down));
//                    // if direction is not up or down handle with grace!
//                    default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
//                }
//            }
//            case 8 -> {
//                switch(destination.direction()) {
//                    case UP -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset8Up));
//                    case DOWN -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset8Down));
//                    // if direction is not up or down handle with grace!
//                    default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
//                }
//            }
//            case 9 -> {
//                switch(destination.direction()) {
//                    case UP -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset9Up));
//                    case DOWN -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset9Down));
//                    // if direction is not up or down handle with grace!
//                    default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
//                }
//            }
//            case 10 -> {
//                switch(destination.direction()) {
//                    case UP -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset10Up));
//                    case DOWN -> softwareBus.publish(new Message(TOPIC_RESET_CALL, SUBTOPIC_BUILD_MUX, SoftwareBusCodes.reset10Down));
//                    // if direction is not up or down handle with grace!
//                    default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
//                }
//            }
//            default -> throw new IllegalStateException("Unexpected value: " + destination.direction());
//        }
//        destinations.remove(destination);
    }
    /// ////////////////////////////////////


    /**
     * In normal mode, level call buttons are enabled
     */
    public void enableCalls() {
        // Publish to MUX
        softwareBus.publish(new Message(TOPIC_CALLS_ENABLED,
                SoftwareBusCodes.buildingMUX,
                SoftwareBusCodes.on));

        // set local variable
        this.callEnabled = true;
    }

    /**
     * In fire mode, and controlled mode call buttons are disabled
     */
    public void disableCalls() {
        // Notify MUX
        softwareBus.publish(new Message(TOPIC_CALLS_ENABLED, SoftwareBusCodes.buildingMUX,
                SoftwareBusCodes.off));

        // Update local
        this.callEnabled = false;
    }

    /**
     * Call publish on softwareBus with a message that the call button on the given floor, and given direction can be
     * turned off
     * Remove that floor from destinations
     *
     * @param floor the floor request button that is no longer relevant
     */
    public void clearRequest(int floor) {
        // I am going to assume these ar the buttons inside the cabin
        // we may want to consider keeping track of what buttons are on with an array of booleans
        // this could reduce clutter so we only call publish if the array contains true at the index of the floor
        softwareBus.publish(new Message(TOPIC_RESET_CALL, currentElevatorId, floor));
    }

    /**
     * In Fire mode, the request buttons in the cabin are mutually exclusive
     */
    public void enableSingleRequests() {
        // Notify MUX
        softwareBus.publish(new Message(TOPIC_REQS_ENABLED, currentElevatorId,
                SoftwareBusCodes.on));
        softwareBus.publish(new Message(TOPIC_SELECTION_TYPE, currentElevatorId,
                SoftwareBusCodes.single));

        // Set local variable
        this.multipleRequests = false;
    }

    /**
     * In Normal mode, all request buttons are enabled
     */
    public void enableMultipleRequests() {
        // Notify MUX
        softwareBus.publish(new Message(TOPIC_REQS_ENABLED, currentElevatorId,
                SoftwareBusCodes.on));
        softwareBus.publish(new Message(TOPIC_SELECTION_TYPE, currentElevatorId,
                SoftwareBusCodes.multiple));

        // Set local variable
        this.multipleRequests = true;

    }


    public Destination nextService(Destination destination) {
        //TODO deal with method call
//        handleCabinSelect();


        currentDirection = destination.direction();
        currentFloor = destination.floor();

        // TODO deal fire key somehow
        // Calls disabled case
        if (!callEnabled /* && !fireKey */) {
            return null;
        }

        if (!multipleRequests) {
            Destination nextService = destinations.getFirst();
            destinations.clear();
            destinations.add(nextService); //TODO: this seems incorrect?
            return nextService;
        }

        //Determine floors not on the way
        List<Destination> unreachable = new ArrayList<>();
        if (destinations.isEmpty()) {
            return null;
        }
        int currServiceFloor = destinations.getFirst().getFloor();

        for (Destination fd : destinations) {
            //Service incompatible
            boolean belowDown =
                    fd.floor() < currServiceFloor &&
                            fd.direction() != null &&
                            fd.direction() == Direction.DOWN;
            boolean aboveUp =
                    fd.floor() > currServiceFloor &&
                            fd.direction() != null &&
                            fd.direction() == Direction.UP;
            //Floor incompatible
            boolean belowUp =
                    fd.floor() < currentFloor &&
                            fd.direction() == Direction.UP;
            boolean aboveDown =
                    fd.floor() > currentFloor &&
                            fd.direction() == Direction.DOWN;
            if (belowDown || aboveUp || belowUp || aboveDown) {
                unreachable.add(fd);
            }
        }
        //Remove unreachable floors from queue temporarily
        for (Destination fd : unreachable) {
            destinations.remove(fd);
        }
        // Determines if list is sorted increasing or decreasing
        int inticator = 0;
        // sort increasing
        if (currentDirection == Direction.UP) {
            inticator = 1;
        }// sort decreasing
        else if (currentDirection == Direction.DOWN) {
            inticator = -1;
        }

        //If not moving, go to the most recently called floor
        if (inticator == 0) {
            return destinations.getFirst();
        }

        //The humble bubble sort glorious! <- so hot! wowowowow!!
        for (int i = 0; i < destinations.size(); i++) {
            for (int j = 0; j < destinations.size(); j++) {
                if (i == j) continue;
                if (destinations.get(i).floor() * inticator > destinations.get(j).floor() * inticator) {
                    Destination temp = destinations.get(i);
                    destinations.set(i, destinations.get(j));
                    destinations.set(j, temp);
                }
            }
        }

        //re-add unreachable destinations
        destinations.addAll(unreachable);

        return destinations.getFirst();

    }
}