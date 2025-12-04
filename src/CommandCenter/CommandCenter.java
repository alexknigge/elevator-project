package CommandCenter;

import Bus.SoftwareBus;
import Bus.SoftwareBusCodes;
import ElevatorController.Util.Direction;
import ElevatorController.Util.Destination;
import ElevatorController.Util.State;
import Message.Message;
import javafx.application.Platform;

import java.util.Arrays;

public class CommandCenter implements Runnable{

    private boolean[] elevatorEnabled={true,true,true,true};

    private Destination[] Destinations = {null,null,null,null};

    //Given to us by a startup
    public SoftwareBus bus;

    /**
     * Sent by us to elevator control
     */

    private static final int SET_MODE= SoftwareBusCodes.setMode;


    private static final int TURN_ELEVATOR_ON_OFF=SoftwareBusCodes.elevatorOnOff;


    private static final int SERVICE_MESSAGE = SoftwareBusCodes.setDestination;


    /**
     * Sent to us by elevator controller
     */

    private static final int GET_ELEVATOR_STATUS = SoftwareBusCodes.elevatorStatus;

    private static final int GET_MODE = SoftwareBusCodes.fireMode;

    private static final int GET_DOOR_STATUS = SoftwareBusCodes.doorStatusCC;


    private State currMode = State.NORMAL;

    private ElevatorPanel[] elevators;

    public CommandCenter(SoftwareBus bus)  {
        this.bus=bus;
        bus.subscribe(GET_MODE,0);
        bus.subscribe(GET_ELEVATOR_STATUS, 0);
        bus.subscribe(GET_DOOR_STATUS,0);

        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while(true) {
            checkElevatorStatus();
        }
    }

    private void checkElevatorStatus() {
        while (true) {
            Message message = bus.get(GET_ELEVATOR_STATUS,0);
            if(message != null) {
                int elevatorID = message.getSubTopic();
                int elevatorFloor = message.getBody();
                ElevatorPanel elevatorPanel = elevators[elevatorID];
                Platform.runLater(() -> {
                    elevatorPanel.updateCarPosition(elevatorFloor,true);
                });
            }
        }
    }

    /**
     * Publishes starts single elevator
     */
    public void enableSingleElevator(int elevatorId){
        System.out.println("In command center; Turning on elevator: "+elevatorId);
        elevatorEnabled[elevatorId-1]=true;
        bus.publish(new Message(TURN_ELEVATOR_ON_OFF,elevatorId,1)); //turn elevator on
    }

    /**
     * Returns if this elevator is enabled
     * @param elevatorId the elevator in questions
     * @return true or false
     */

    public boolean elevatorOn(int elevatorId){
        return elevatorEnabled[elevatorId-1];
    }

    /**
     * Publishes start all elevators
     */
    public void enableElevator(){
        Arrays.fill(elevatorEnabled,true);
        System.out.println("In command center; Turning on elevators");
        bus.publish(new Message(TURN_ELEVATOR_ON_OFF,0,1)); //Turn all elevators on
    }

    /**
     * Due to the way the command center GUI is set up, we added the functionality
     * to stop/start single elevators
     * @param elevatorId the elevator to be stoped
     */
    public void disableSingleElevator(int elevatorId){
        System.out.println("In command center; Turning off elevator: "+elevatorId);
        elevatorEnabled[elevatorId-1]=false;
        bus.publish(new Message(TURN_ELEVATOR_ON_OFF,elevatorId,0)); //turns elevator off
    }

    /**
     * Stops all elevators
     */

    public void disableElevator(){
        System.out.println("In command center; Turning off elevator: ");
        Arrays.fill(elevatorEnabled,false);
        bus.publish(new Message(TURN_ELEVATOR_ON_OFF,0,0)); //Turns all elevators off
    }

    /**
     * Turns off fire message, sent to all elevators, with a dummy body
     */
    public void clearFireMessage() {
        System.out.println("In command center: clear fire message sent");
        bus.publish(new Message(SoftwareBusCodes.clearFire,0,0));
    }



    /**
     * Send mode message
     */
    public void sendModeMessage(int modeMessage){
        System.out.println("In command center; mode message sent");
        bus.publish(new Message(SET_MODE,0,modeMessage));
    }

    /**
     * Send Service Message
     */
    public void sendServiceMessage(int elevatorID, int floor) {
        System.out.println("Sent service message for elevator: "+ elevatorID+ " to floor "+ floor);
        //TODO HAVE THIS BE SENT OUT ONLY WHEN IN COMMAND MODE
        bus.publish(new Message(SERVICE_MESSAGE,elevatorID,floor));
    }
    /**
     * Gets a new mode from software bus if there is one, otherwise returns current mode
     */
    public State getMode() {
        //hard coded for fire alarm, would need to handle other cases if we are told to switch to other modes
        if (bus.get(GET_MODE,0) != null) return State.FIRE;
        return currMode;
    }

    /**
     * Returns the current elevator status
     * @param id of the elevator
     * @return current floor and motion of the elevator
     */
    public Destination getElevatorStatus(int id) {
        int message = bus.get(GET_ELEVATOR_STATUS,id).getBody();
        Destination Destination;
        if (message > 200){
            Destination = new Destination(message-200,Direction.UP);
        }
        if (message > 100)
            Destination= new Destination(message-100,Direction.STOPPED);
        else
            Destination= new Destination(message,Direction.DOWN);

        Destinations[id-1]=Destination;
        return Destination;
    }

    /**
     * Gets the door status of specified elevator
     * @param id of the elevator
     * @return 0 for open, 1 for closed, -1 error
     */
    private int getDoorStatus(int id) {
        int message = bus.get(GET_DOOR_STATUS,id).getBody();
        if (message == 0 || message == 1) return message;
        return -1;
    }

    public Destination getDestination(int id){
        return Destinations[id-1];
    }

    public void setElevators(ElevatorPanel[] elevators) {
        this.elevators = elevators;
    }
}