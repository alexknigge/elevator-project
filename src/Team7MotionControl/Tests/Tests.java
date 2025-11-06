package Team7MotionControl.Tests;

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
        MotionSimulation sim = new MotionSimulation(.5);
        Thread simThread = new Thread(sim);
        simThread.setDaemon(true);
        simThread.start();

        ElevatorGUI gui = new ElevatorGUI(
                sim.getSensors(),
                sim.get_sensor_pos_HashMap(),
                sim.getElevator(),
                sim.getMotor()
        );
        gui.getPrimaryStage(primaryStage);

        Motor motor = new Motor();
        motor.subscribe(sim);
        motor.set_direction(Direction.UP);
        motor.start();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
