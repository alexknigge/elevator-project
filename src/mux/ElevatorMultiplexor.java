package mux;

import bus.Message;
import bus.SoftwareBus;
import bus.Topic;
import pfdAPI.*;

/**
 * Class that defines the ElevatorMultiplexor, which coordinates communication from the Elevator
 * Command Center to the relevant devices. Communication is accomplished via the software bus,
 * and both the PFDs and the motion devices are subject to control.
 * 
 * Note: car and elevator are used interchangeably in this context.
 */
public class ElevatorMultiplexor {

    // Constructor
    public ElevatorMultiplexor(int ID){
        this.ID = ID;
        this.elev = new Elevator(ID, 10);
        initialize(); 
    }

    // Globals
    private int currentFloor = 1;
    private String currentDirection = "IDLE";
    private final int ID;
    private final Elevator elev;
    private final SoftwareBus bus = new SoftwareBus(false);

    // Initialize the MUX  (placeholder example subscriptions)
    public void initialize() {
        bus.subscribe(Topic.DOOR_CONTROL, ID);
        bus.subscribe(Topic.DISPLAY_FLOOR, ID);
        bus.subscribe(Topic.SELECTION_RESET, ID); //TODO: Jackie added
        bus.subscribe(Topic.DISPLAY_DIRECTION, ID);
        bus.subscribe(Topic.CAR_DISPATCH, ID);
        bus.subscribe(Topic.MODE_SET, 0);  // Global mode changes
        bus.subscribe(Topic.CABIN_SELECT, ID);
        bus.subscribe(Topic.CAR_POSITION, ID);
        bus.subscribe(Topic.DOOR_SENSOR, ID);
        bus.subscribe(Topic.DOOR_STATUS, ID);
        bus.subscribe(Topic.CABIN_LOAD, ID);
        bus.subscribe(Topic.FIRE_KEY, ID);
        System.out.println("ElevatorMUX " + ID + " initialized and subscribed");
        startBusPoller();
    }


    /**
     * Incoming Event Handling Functions
     */

    // Polls the software bus for messages and handles them accordingly
    public void startBusPoller() {
        Thread t = new Thread(() -> {
            // keep polling
            while (true) {
                Message msg;
                msg = bus.get(Topic.DOOR_CONTROL, ID);
                if (msg != null) {
                    handleDoorControl(msg);
                }
                msg = bus.get(Topic.DISPLAY_FLOOR, ID);
                if (msg != null) {
                    handleDisplayFloor(msg);
                }
                msg = bus.get(Topic.SELECTION_RESET, ID);
                if (msg != null) {
                    handleSelectionReset(msg);
                }
                msg = bus.get(Topic.DISPLAY_DIRECTION, ID);
                if (msg != null) {
                    handleDisplayDirection(msg);
                }
                msg = bus.get(Topic.CAR_DISPATCH, ID);
                if (msg != null) {
                    handleCarDispatch(msg);
                }
                msg = bus.get(Topic.MODE_SET, 0);
                if (msg != null) {
                    handleModeSet(msg);
                }
                msg = bus.get(Topic.CABIN_SELECT, ID);
                if (msg != null) {
                    handleCabinSelect(msg);
                }
                msg = bus.get(Topic.CAR_POSITION, ID);
                if (msg != null) {
                    handleCarPosition(msg);
                }
                msg = bus.get(Topic.DOOR_SENSOR, ID);
                if (msg != null) {
                    handleDoorSensor(msg);
                }
                msg = bus.get(Topic.DOOR_STATUS, ID);
                if (msg != null) {
                    handleDoorStatus(msg);
                }
                msg = bus.get(Topic.CABIN_LOAD, ID);
                if (msg != null) {
                    handleCabinLoad(msg);
                }
                //TODO: This is something the MUX sends out, not receives. Only the building needs to
                //TODO: worry about getting the FIRE_ALARM message.
                msg = bus.get(Topic.FIRE_KEY, ID);
                if (msg != null) {
                    handleFireKey(msg);
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private void handleDoorControl(Message msg) {
        currentFloor = msg.getBody();
        if (currentFloor == 1)
            elev.door.open();
        else
            elev.door.close();
    }

    private void handleDisplayFloor(Message msg) {
        int floor = msg.getBody();
        elev.display.updateFloorIndicator(floor, currentDirection);
        elev.panel.setDisplay(floor, currentDirection);
    }

    private void handleDisplayDirection(Message msg) {
        int dir = msg.getBody();
        if (dir == 0){
            elev.display.updateFloorIndicator(currentFloor, "UP");
            elev.panel.setDisplay(currentFloor, "UP");
        } else if (dir == 1) {
            elev.display.updateFloorIndicator(currentFloor, "DOWN");
            elev.panel.setDisplay(currentFloor, "DOWN");
        } else {
            elev.display.updateFloorIndicator(currentFloor, "IDLE");
            elev.panel.setDisplay(currentFloor, "IDLE");
        }
    }

    private void handleCarDispatch(Message msg) {
        // TODO: implement car dispatch logic
    }

    private void handleModeSet(Message msg) {
        // TODO: implement mode set logic
    }

    private void handleCabinSelect(Message msg) {
        int floor = msg.getBody();
        elev.panel.pressFloorButton(floor);
    }

    private void handleCarPosition(Message msg) {
        // TODO: implement car position logic
    }

    private void handleSelectionReset(Message msg){
        // TODO: implement selection button on passenger panel reset logic
    }

    private void handleDoorSensor(Message msg) {
        int status = msg.getBody(); 
        elev.door.setObstruction(status == 0);
    }

    private void handleDoorStatus(Message msg) {    
        int status = msg.getBody(); 
        if (status == 1)
            elev.door.open();
        else
            elev.door.close();
    }

    private void handleCabinLoad(Message msg) {
        int status = msg.getBody();
        elev.panel.setOverloadWarning(status == 1);
    }

    private void handleFireKey(Message msg) {
        elev.panel.toggleFireKey();
    }

    /**
     * Outgoing Emitter Function
     */

    // Pass information through the MUX to the console & publish to bus if needed
    public void emit(String msg, boolean publish) {
        System.out.println("Elevator-EMIT: " + msg);

        // Publish to bus
        if(publish){ 
            Message message = Message.parseStringToMsg(msg); // Expects TOPIC-SUBTOPIC-BODY format
            bus.publish(message); 
        }
    }
}
