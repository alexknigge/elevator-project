package pfdAPI;

import pfdGUI.gui;

public class Building {

    // The building's elevator call buttons on each floor
    public final FloorCallButtons[] callButtons;

    /**
     * Constructs a Building.
     * @param totalFloors the number of floors in the building (=10)
     */
    public Building(int totalFloors) {
        gui g = gui.getInstance();
        this.callButtons = new FloorCallButtons[totalFloors];
        for (int i = 0; i < totalFloors; i++) {
            this.callButtons[i] = new FloorCallButtons(i, totalFloors, g.internalState);
        }
    }
}
