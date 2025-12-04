import Bus.SoftwareBus;
import CommandCenter.ElevatorControlSystem;
import DeviceMultiplexor.BuildingMultiplexor;
import DeviceMultiplexor.ElevatorMultiplexor;
import ElevatorController.ElevatorController;
import PFDGUI.gui;
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {
    private ElevatorMultiplexor elevatorMultiplexors[];
    private ElevatorController elevatorControllers[];
    private gui multiplexorApp;
    ElevatorControlSystem commandCenter;

    private final static int MAX_ELEVATORS = 4;

    @Override
    public void init() {
        //In main, we will create all the required devices to simulate the
        // elevator controller system, specifically, we will instantiate, 4
        // elevator controllers, 4 device multiplexers, 1 command center and
        // 9 software buses.

        System.out.println("Hello world! We are running!");

        SoftwareBus softwareBus = new SoftwareBus(true);

        elevatorMultiplexors = new ElevatorMultiplexor[MAX_ELEVATORS];
        elevatorControllers = new ElevatorController[MAX_ELEVATORS];

        commandCenter = new ElevatorControlSystem(softwareBus);
        multiplexorApp = new gui();

        for (int i = 0; i < MAX_ELEVATORS; i++) {
            ElevatorMultiplexor elevatorMultiplexor = new ElevatorMultiplexor(i + 1, softwareBus);
            elevatorMultiplexors[i] = elevatorMultiplexor;
        }

        for (int i = 0; i < MAX_ELEVATORS; i++) {
            ElevatorController elevatorController = new ElevatorController(i + 1, softwareBus);
            elevatorControllers[i] = elevatorController;
            Thread eThread = new Thread(elevatorControllers[i]);
            eThread.start();
        }

        // UI setup
        multiplexorApp.initilizeMuxs(elevatorMultiplexors);

        BuildingMultiplexor buildingMultiplexor = new BuildingMultiplexor(softwareBus);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage commandCenterStage = commandCenter.getStage();
        Stage multiplexorAppStage = multiplexorApp.getStage();

        commandCenterStage.show();
        Thread.sleep(5000);
        multiplexorAppStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}