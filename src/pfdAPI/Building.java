package pfdAPI;
import mux.BuildingMultiplexor;

public class Building {

    // The building's elevator call buttons on each floor
    public final FloorCallButtons[] callButtons;
    public final BuildingMultiplexor mux;

    /**
     * Constructs a Building.
     * @param totalFloors the number of floors in the building (=10)
     * @param mux the BuildingMultiplexor instance
     */
    public Building(int totalFloors, BuildingMultiplexor mux) {
        this.mux = mux;

        this.callButtons = new FloorCallButtons[totalFloors];
        for (int i = 1; i <= totalFloors; i++) {
            this.callButtons[i - 1] = new FloorCallButtons(i, totalFloors, mux);
        }
    }
}
