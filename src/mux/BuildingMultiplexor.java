package mux;

import bus.Message;
import bus.SoftwareBus;
import bus.Topic;
import pfdAPI.Building;

/**
 * Class that defines the BuildingMultiplexor, which coordinates communication from the Elevator
 * Command Center to the relevant devices. Communication is accomplished via the software bus,
 * and both the PFDs and the motion devices are subject to control.
 * 
 * Note: car and elevator are used interchangeably in this context.
 */
public class BuildingMultiplexor {

    // Constructor
    public BuildingMultiplexor(){ 
        initialize(); 
    }

    // Listener for GUI/API integration
    private final SoftwareBus bus = new SoftwareBus(false);
    private final Building bldg = new Building(10);;

    // Door Commands
    int DOOR_OPEN = 1;
    int DOOR_CLOSE = 2;

    int DIR_IDLE = 0;
    int DIR_UP = 1;
    int DIR_DOWN = 2;

    int FIRE_OFF = 0;
    int FIRE_ON = 1;

    // Initialize the MUX
    public void initialize() {
        System.out.println("ready bldg");
        bus.subscribe(Topic.FIRE_ALARM, 0);
        bus.subscribe(Topic.CALL_RESET, 0);
        bus.subscribe(Topic.HALL_CALL, 0);
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
                msg = bus.get(Topic.FIRE_ALARM, 0);
                if (msg != null) {
                    handleFireAlarm(msg);
                }
                msg = bus.get(Topic.CALL_RESET, 0);
                if (msg != null) {
                    handleCallReset(msg);
                }
                msg = bus.get(Topic.HALL_CALL, 0);
                if (msg != null) {
                    handleHallCall(msg);
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

    // Handle Hall Call Message
    public void handleHallCall(Message msg) {
        int floor = msg.getSubTopic();
        int directionCode = msg.getBody();
        if (directionCode == DIR_UP) { bldg.callButtons[floor].pressUpCall(); } 
        else if (directionCode == DIR_DOWN) { bldg.callButtons[floor].pressDownCall(); }
    }

    // Handle Mode Set Message (Building Only Cares about Fire Safety)
    public void handleFireAlarm(Message msg) {
        int modeCode = msg.getBody();
        if (modeCode == FIRE_ON) {
            // TODO: set fire state in building
        } else if(modeCode == FIRE_OFF){
            // TODO: reset fire state in building
        }
    }

    // Handle Call Reset Message
    public void handleCallReset(Message msg) {
        int floor = msg.getSubTopic();
        int directionCode = msg.getBody();
        if (directionCode == DIR_UP) { bldg.callButtons[floor].resetCallButton("UP"); } 
        else if (directionCode == DIR_DOWN) { bldg.callButtons[floor].resetCallButton("DOWN"); }
    }

    /**
     * Outgoing Emitter Function
     */

    // Pass information through the MUX to the console & publish to bus if needed
    public void emit(String msg, boolean publish) {
        System.out.println("Building-EMIT: " + msg);

        // Publish to bus
        if(publish){ 
            Message message = Message.parseStringToMsg(msg); // Expects TOPIC-SUBTOPIC-BODY format
            bus.publish(message); 
        }
    }
}
