import Bus.SoftwareBus;
import CommandCenter.ElevatorControlSystem;
import DeviceMultiplexor.BuildingMultiplexor;
import DeviceMultiplexor.ElevatorMultiplexor;
import ElevatorController.ElevatorController;
import PFDGUI.gui;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {
    private record Elevator(ElevatorController elevatorController, ElevatorMultiplexor elevatorMultiplexor) {}

    private gui multiplexorApp;
    private Stage muxStage;
    private Stage commandCenterStage;

    private final static int MAX_ELEVATORS = 4;

    public Main() {
        System.out.println("Hello world!");

        SoftwareBus softwareBus = new SoftwareBus(true);

        //In main, we will create all the required devices to simulate the
        // elevator controller system, specifically, we will instantiate, 4
        // elevator controllers, 4 device multiplexers, 1 command center and
        // 9 software buses.

        ArrayList<ElevatorController> elevatorControllers = new ArrayList<>();
        ArrayList<ElevatorMultiplexor> elevatorMultiplexors = new ArrayList<>();

        ElevatorControlSystem commandCenter = new ElevatorControlSystem(softwareBus);

        for (int i = 0; i < MAX_ELEVATORS; i++) {
            ElevatorController elevatorController = new ElevatorController(i, softwareBus);
            ElevatorMultiplexor elevatorMultiplexor = new ElevatorMultiplexor(i, softwareBus);

            elevatorControllers.add(elevatorController);
            elevatorMultiplexors.add(elevatorMultiplexor);

        }

        // UI setup
        multiplexorApp = new gui();
        muxStage = multiplexorApp.getStage();

        commandCenterStage = commandCenter.getStage();

        BuildingMultiplexor buildingMultiplexor = new BuildingMultiplexor(softwareBus);




    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new Main();
        muxStage.show();
        commandCenterStage.show();
    }
    
}