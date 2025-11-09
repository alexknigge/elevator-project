import javafx.application.Application;

public class runPfd {
    public static void main(String[] args) throws InterruptedException {
        DeviceMultiplexor mux = DeviceMultiplexor.getInstance();

        // Start GUI
        new Thread(() -> Application.launch(gui.class)).start();
        Thread.sleep(1200); // brief warm-up; shorten/remove if your GUI is instant

        int carId = 1;
        int floor = 6;

        // Show we're at floor 6 and idle
        mux.onDisplaySet(carId, floor + " IDLE");
        mux.notifyCarArrived(carId, floor, "IDLE");

        // Open, wait, close
        mux.onDoorCON(carId, "OPEN");
        Thread.sleep(800);
        mux.onDoorCON(carId, "CLOSE");

        // Update upper display: move UP to 7, then idle at 7
        Thread.sleep(500);
        mux.onDisplaySet(carId, (floor + 1) + " UP");   // shows arrow UP and "7"
        Thread.sleep(600);
        mux.notifyCarArrived(carId, floor + 1, "UP");
        mux.onDisplaySet(carId, (floor + 1) + " IDLE"); // now idle on 7
    }
}
