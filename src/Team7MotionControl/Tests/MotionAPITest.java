package Team7MotionControl.Tests;

import Team7MotionControl.Elevator_Controler.MotionAPI;
import Team7MotionControl.Util.Direction;

public class MotionAPITest {
    //TODO: This class doesn't work with out a crazy amount of modification bs

    public static void main(String[] args) {
        MotionAPI motionAPI =new MotionAPI();
        motionAPI.set_direction(Direction.UP);
        motionAPI.start();


        while (true){
            System.out.println("( "+ motionAPI.bottom_alignment()+","+motionAPI.top_alignment()+" )");
        }
    }
}
