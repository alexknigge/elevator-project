package ElevatorController.LowerLevel;

import Bus.SoftwareBus;
import ElevatorController.Util.FloorNDirection;

public class Notifier {
    private SoftwareBus softwareBus;
    public  Notifier(SoftwareBus softwareBus){
        //TODO: does notifier need to subscribe? or can it just publish messages?
        //TODO may need to take in int for elevator number for software bus subscription
        //TODO call subscribe on softwareBus w/ relevant topic/subtopic

        this.softwareBus = softwareBus;
    }
    public void arrivedAtFloor(FloorNDirection floorNDirection){}
    public void elevatorStatus(FloorNDirection floorNDirection){}
    public void playCapacityNoise(){}
    public void stopCapacityNoise(){}

}
