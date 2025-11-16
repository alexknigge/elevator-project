package Team7MotionControl.Tests;

import Team7MotionControl.Elevator_Controler.MotionController;
import Team7MotionControl.GUI.ElevatorGUI;
import Team7MotionControl.Hardware.Motor;
import Team7MotionControl.Simulation.MotionSimulation;
import Team7MotionControl.Util.Direction;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Demonstrates the elevator moving up and down several times,
 * stopping at each floor, without freezing the GUI.
 */
public class Tests extends Application {

    @Override
    public void start(Stage primaryStage) {
        MotionController motionController=new MotionController();


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
        gui.getPrimaryStage(primaryStage);

        motionController.set_direction(Direction.UP);
        motionController.start();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
