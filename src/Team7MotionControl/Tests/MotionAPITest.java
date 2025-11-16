package Team7MotionControl.Tests;

import Team7MotionControl.Elevator_Controler.MotionAPI;
import Team7MotionControl.Elevator_Controler.MotionController;
import Team7MotionControl.Util.Direction;

public class MotionAPITest {


    public static void main(String[] args) {

        MotionController motionController=new MotionController();
        motionController.set_direction(Direction.UP);
        motionController.start();
        Integer bottom=null;
        Integer top=null;
        int b=-1;
        int t=-1;

        while (true){
            Integer newBottom=motionController.bottom_alignment();
            Integer newTop=motionController.top_alignment();
            if(bottom==null||top==null){
                System.out.println(bottom+", " +top);
                bottom=newBottom;
                top=newTop;
            }else if((!bottom.equals(newBottom)|| !top.equals(newTop))){
                bottom= newBottom;
                top=newTop;
                System.out.println("( "+bottom+" "+top+" )");
                //System.out.println("(" + vals[0] + "," + (vals[1] == -1 ? "null" : vals[1]) + ")");
            }


        }
    }
}
