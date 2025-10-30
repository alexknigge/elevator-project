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

        int startingFloor = 6;

        // API Calls
        display.updateFloorIndicator(startingFloor, "IDLE");
        panel.setDisplay(startingFloor, "IDLE");
        display.playArrivalChime();
        panel.resetFloorButton(startingFloor);
        Thread.sleep(2000);
        callButtons.pressUpCall();
        Thread.sleep(2000);
        callButtons.resetCallButton("UP");
        Thread.sleep(2000);
        for(int n = 0; n < 5; n++) {
            int i = rand.nextInt(10);
            display.playArrivalChime();
            Thread.sleep(2000);
            doors.open();
            Thread.sleep(2000);
            doors.setObstruction(true);
            Thread.sleep(2000);
            if(rand.nextInt(10) > 5) display.playOverLoadWarning(); // 50% chance to simulate weight overload call
            Thread.sleep(2000);
            doors.close();  // Should detect obstruction and reopen
            Thread.sleep(2000);
            doors.setObstruction(false);
            Thread.sleep(2000);
            doors.close();  // Now closes successfully
            Thread.sleep(2000);

            // Simulate passing floors
            for(int j = 0; j < 3; j++) {
                if(i > display.getCurrentFloor()) {
                    display.updateFloorIndicator(i, "UP");
                    panel.setDisplay(i, "UP");
                } else if(i < display.getCurrentFloor()) {
                    display.updateFloorIndicator(i, "DOWN");
                    panel.setDisplay(i, "DOWN");
                } else {
                    continue;
                }
                Thread.sleep(1000);
            }

            panel.pressFloorButton(i);
            Thread.sleep(2000);
            display.updateFloorIndicator(i, "IDLE");
            panel.setDisplay(i, "IDLE");
            panel.resetFloorButton(i);
        }
    }
}
