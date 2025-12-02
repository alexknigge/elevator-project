package ElevatorController.Util;

public record Destination(int floor, Direction direction) {

    public int getFloor() {
        return floor;
    }

    public Direction getDirection() {
        return direction;
    }

}
