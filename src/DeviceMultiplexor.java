import java.util.HashSet;
import java.util.Set;

public class DeviceMultiplexor {
    public interface DeviceListener {
    void onDisplayUpdate(int carId, String text);
    void onDoorStateChanged(int carId, String state);
    void onCarArrived(int carId, int floor, String direction);
    void onCallReset(int floor);
    void onCabinLoadChanged(int carId, int weight);
    void onModeChanged(int carId, String mode);
    void onImageInteraction(String imageType, int imageIndex, String interactionType, String additionalData);
    void emitOverloadWeightClick(int buttonIndex);
    }

    private DeviceListener listener;

    public void setListener(DeviceListener listener) {
        this.listener = listener;
    }

    // simple message tags used
    String CarDispatch = "CAR.DISPATCH";   // move car to a floor
    String DoorCmd     = "DOOR.CON";       // door control ( open or close)
    String DisplaySet  = "DISPLAY.SET";    // update car display 
    String HallCall    = "HALL.CALL";      // hall button pressed with direction 
    String CabinSelect = "CABIN.SELECT";   // cabin floor selected
    String CarPosition  = "CAR.POSITION";   // car at floor with direction 
    String DoorSensor   = "DOOR.SENSOR";    // obstruction 
    String CabinLoad   = "CABIN.LOAD";     // load (weight)
    String ModeSet     = "MODE.SET";       // mode change
    String ImageClick  = "IMAGE.CLICK";    // image clicked/interacted with
    String FloorSelect = "FLOOR.SELECT";   // floor button selected
    
    // cars known to the mux (ids only for now)
    Set<Integer> cars = new HashSet<>();


    public DeviceMultiplexor() {}

    // register a car so the mux can target it by id
    public void registerCar(int carId) {
        cars.add(carId);
    }

    // initilize the Multiplexor  (placeholder initialize)
    public void initialize() {
        System.out.println("ready " + cars);
    }

    // send car to targetFloor
    public void onCarDispatch(int carId, int targetFloor) {
        System.out.println("dispatch " + carId + " " + targetFloor);
    }

    // operate doors on a car
    public void onDoorCON(int carId, String action) {
        System.out.println("door " + carId + " " + action);
    }

    // set text/arrow on car display
    public void onDisplaySet(int carId, String text) {
        System.out.println("display " + carId + " " + text);
    }

    // hall panel pressed at floor with direction
    public void emitHallCall(int floor, String direction) {
        System.out.println("hall " + floor + " " + direction);
    }

    //cabin panel chose a floor on a given car
    public void emitCabinSelect(int carId, int floor) {
        System.out.println("select " + carId + " " + floor);
    }

        // elevator position with direction 
    public void emitCarPosition(int carId, int floor, String direction) {
        System.out.println("position " + carId + " " + floor + " " + direction);
    }


    // door sensor feedback
    public void emitDoorSensor(int carId, boolean blocked) {
        System.out.println("doorSensor " + carId + " " + blocked);
    }

    // cabin load in weight
    public void emitCabinLoad(int carId, int weight) {
        System.out.println("load " + carId + " " + weight);
    }


    // mode set
    public void onModeSet(int carId, String mode) {
        System.out.println("mode " + carId + " " + mode);
    }

    // image interaction tracking
    public void emitImageInteraction(String imageType, int imageIndex, String interactionType, String additionalData) {
        System.out.println("Image interaction: " + imageType + "[" + imageIndex + "] - " + interactionType + " : " + additionalData);
        if (listener != null) {
            listener.onImageInteraction(imageType, imageIndex, interactionType, additionalData);
        }
    }

    // Specific methods for different image types ***** Clean up later maybe *****
    public void emitCabinPanelClick(int panelIndex, int floorNumber) {
        emitImageInteraction("CabinPanel", panelIndex, "FloorButtonPress", String.valueOf(floorNumber));
        emitCabinSelect(panelIndex + 1, floorNumber); // Assuming car IDs start from 1
    }

    public void emitCallButtonClick(int buttonIndex, String direction, int floor) {
        emitImageInteraction("CallButton", buttonIndex, "DirectionPress", direction + "_FLOOR_" + (floor + 1));
        emitHallCall(floor + 1, direction); // floor + 1 because arrays are 0-indexed but floors start at 1
    }

    public void emitDoorClick(int doorIndex, String clickType) {
        emitImageInteraction("ElevatorDoor", doorIndex, "DoorClick", clickType);
    }

    public void emitFloorDisplayClick(int displayIndex) {
        emitImageInteraction("FloorDisplay", displayIndex, "DisplayClick", "FLOOR_" + (displayIndex + 1));
    }

    public void emitFireAlarmClick() {
        emitImageInteraction("FireAlarm", 0, "AlarmActivated", "EMERGENCY");
        onModeSet(1, "EMERGENCY"); // Set all cars to emergency mode
    }

    public void emitOverloadWeightClick(int buttonIndex) {
        emitImageInteraction("OverloadWeight", buttonIndex, "WeightExceeded", "OVERLOAD");
        onModeSet(1, "OVERLOAD"); // Set all cars to overload mode
    }

    // notifications to listener
    public void notifyDisplaySet(int carId, String text) {
        if (listener != null) listener.onDisplayUpdate(carId, text);
    }

    public void notifyDoorChanged(int carId, String state) {
        if (listener != null) listener.onDoorStateChanged(carId, state);
    }

    public void notifyCarArrived(int carId, int floor, String direction) {
        if (listener != null) listener.onCarArrived(carId, floor, direction);
    }
    public void notifyCallReset(int floor) {
        if (listener != null) listener.onCallReset(floor);
    }

    // getter for accessing the multiplexor instance from GUI
    private static DeviceMultiplexor instance = null;
    
    public static DeviceMultiplexor getInstance() {
        if (instance == null) {
            instance = new DeviceMultiplexor();
        }
        return instance;
    }

    public static void setInstance(DeviceMultiplexor mux) {
        instance = mux;
    }

    public static void main(String[] args) {

        // demo 
        DeviceMultiplexor mux = new DeviceMultiplexor();

        mux.registerCar(1);  // add car 1
        mux.registerCar(35);  // add car 35

        mux.initialize(); //initialize the Multiplexor

        // hallway pressed UP at floor 3
        mux.emitHallCall(3, "UP");
        // inside car 1, passenger chose floor 7
        mux.emitCabinSelect(1, 7);  
        mux.onCarDispatch(1, 7); // tell car 1 to go to 7
        mux.onDoorCON(1, "OPEN");   // open doors on car 1

        mux.onDisplaySet(1, "7 UP"); // set car 1 display text

        mux.emitCarPosition(1, 4, "UP");// elevator position with direction
        mux.emitDoorSensor(1, true);// door sensor feedback
        // cabin load in weight
        mux.emitCabinLoad(1, 980);

        mux.onModeSet(1, "EMERGENCY"); // mode set
    }
}
