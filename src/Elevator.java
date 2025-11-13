public class Elevator {
    public final int carId;
    public final ElevatorDoorsAssembly doors;
    public final CabinPassengerPanel panel;
    public final ElevatorFloorDisplay display;
    public final FloorCallButtons hall;

    public Elevator(int carId, int totalFloors) {
        this.carId = carId;
        this.doors  = new ElevatorDoorsAssembly(carId);
        this.panel  = new CabinPassengerPanel(carId, totalFloors);
        this.display = new ElevatorFloorDisplay(carId);
        this.hall   = new FloorCallButtons(carId, 1, totalFloors);
    }
}
