public class testPfdApi {
    public static void main(String[] args) {
        ElevatorFloorDisplay display = new ElevatorFloorDisplay();
        ElevatorDoorsAssembly doors = new ElevatorDoorsAssembly();

        // Display updates
        display.updateFloorIndicator(4, "UP");
        display.playArrivalChime();

        // Door operations
        doors.open();
        doors.setObstruction(true);
        doors.close();  // Should detect obstruction and reopen
        doors.setObstruction(false);
        doors.close();  // Now closes successfully

        display.playOverLoadWarning();
    }
}
