package ElevatorController.Util;

import Bus.SoftwareBusCodes;

public enum Direction {
    UP(SoftwareBusCodes.up),
    DOWN(SoftwareBusCodes.down),
    STOPPED(SoftwareBusCodes.none);

    private final int intValue;

    public int getIntValue() {
        return intValue;
    }

    Direction(int intValue){
        this.intValue = intValue;
    }
}
