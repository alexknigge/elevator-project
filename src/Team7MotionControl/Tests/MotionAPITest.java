package Team7MotionControl.Tests;

import Team7MotionControl.Elevator_Controler.MotionAPI;
import Team7MotionControl.Elevator_Controler.MotionController;
import Team7MotionControl.GUI.ElevatorGUI;
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

        ElevatorGUI gui = new ElevatorGUI(
                motionController.motionSimulation.getSensors(),
                motionController.motionSimulation.get_sensor_pos_HashMap(),
                motionController.motionSimulation.getElevator(),
                motionController.motionSimulation.getMotor()
        );
        //gui.getPrimaryStage(primaryStage);

        while (true){
            Integer newBottom=motionController.bottom_alignment();
            Integer newTop=motionController.top_alignment();
            System.out.println("( "+newBottom+" "+newTop+" )");


        }
    }
}
