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
        //Uncommit this if you want to see the GUI, also make the motion sim
        // in motion controller public
//        ElevatorGUI gui = new ElevatorGUI(
//                motionController.motionSimulation.getSensors(),
//                motionController.motionSimulation.get_sensor_pos_HashMap(),
//                motionController.motionSimulation.getElevator(),
//                motionController.motionSimulation.getMotor()
//        );
//        //gui.getPrimaryStage(primaryStage);
        boolean dummy=true;
        boolean dummy2=true;
        while (true){
            Integer newBottom=motionController.bottom_alignment();
            Integer newTop=motionController.top_alignment();
            System.out.println("( "+newBottom+" "+newTop+" )");

            if(newBottom!=null&&newBottom==2&&dummy){
                System.out.println("Trying to stop");
                motionController.stop();
                dummy=false;
                try {
                    System.out.println("Sleeping");
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }


            }
            if(!dummy&&dummy2){
                motionController.set_direction(Direction.DOWN);
                motionController.start();
                dummy2=false;
            }



        }
    }
}
