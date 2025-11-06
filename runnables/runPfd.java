import javafx.application.Application;
import java.math.*;
import java.util.Random;

public class runPfd {
    public static void main(String[] args) throws InterruptedException {
        ElevatorFloorDisplay display = new ElevatorFloorDisplay();
        ElevatorDoorsAssembly doors = new ElevatorDoorsAssembly();
        CabinPassengerPanel panel = new CabinPassengerPanel(10);
        FloorCallButtons callButtons = new FloorCallButtons(1, 10);
        Random rand = new Random();

        // Start GUI
        Thread guiThread = new Thread(() -> Application.launch(gui.class));
        guiThread.start();
        Thread.sleep(3000); // Wait for GUI to initialize
    }
}
