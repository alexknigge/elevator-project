import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.imageLoader;

/**
 * Simple JavaFX GUI that listens to model classes via a nested listener
 * interface and swaps images in response to events.
 */
public class gui extends Application {
    private imageLoader loader;

    /**
     * Public nested listener type so other classes can refer to it as `gui.listener`.
     */
    public static interface listener {
        void notify(String event, String payload);
    }

    private Label floorLabel1;
    private Label floorLabel2;
    private listener listenerImpl;
    private DeviceMultiplexor multiplexor;
    List<Label> buttonLabels = new ArrayList<>();
    ArrayList<Panel> cabinPanelList = new ArrayList<>();
    ArrayList<ElevatorDoor> elevDoorList = new ArrayList<>();
    ArrayList<CallButton> callButtonList = new ArrayList<>();
    ArrayList<FloorDisplay> floorDisplayList = new ArrayList<>();
    FireAlarm fireAlarm;

    @Override
    public void start(Stage primaryStage) {
        // Initialize the multiplexor
        multiplexor = DeviceMultiplexor.getInstance();

        // create 4 elevators and register them with the mux
        Elevator e1 = new Elevator(1, 10);
        Elevator e2 = new Elevator(2, 10);
        Elevator e3 = new Elevator(3, 10);
        Elevator e4 = new Elevator(4, 10);

        multiplexor.registerCar(e1);
        multiplexor.registerCar(e2);
        multiplexor.registerCar(e3);
        multiplexor.registerCar(e4);

        multiplexor.initialize();
        
        // Set up a listener to handle multiplexor events
        multiplexor.setListener(new DeviceMultiplexor.DeviceListener() {

            @Override
            public void onDisplayUpdate(int carId, String text) {
                System.out.println("Multiplexor: Display update for car " + carId + ": " + text);

            int listIndex = carId - 1;
                if (text == null) return;

                if (text.contains("UP")) {
                    setImg(1, listIndex, 2);
                } else if (text.contains("DOWN")) {
                    setImg(1, listIndex, 1);
                } else {
                    setImg(1, listIndex, 0);
                }
            }
        @Override
            public void onDoorStateChanged(int carId, String state) {
                System.out.println("Multiplexor: Door state changed for car " + carId + ": " + state);

                int listIndex = carId - 1;
                if (listIndex < 0) listIndex = 0;

                if (state == null) return;
                String s = state.toUpperCase();

                if (s.startsWith("OPEN")) {
                    setImg(2, listIndex, 6);
                } else if (s.startsWith("CLOSE")) {
                    setImg(2, listIndex, 3);
                } else {
                    setImg(2, listIndex, 4);
                }
            }
            @Override
            public void onCarArrived(int carId, int floor, String direction) {
                System.out.println("Multiplexor: Car " + carId + " arrived at floor " + floor + " going " + direction);
            }
            
            @Override
            public void onCallReset(int floor) {
                System.out.println("Multiplexor: Call reset for floor " + floor);
            }
            
            @Override
            public void onCabinLoad(int carId, int weight) {
                System.out.println("Multiplexor: Cabin load changed for car " + carId + ": " + weight);
            }
            
            @Override
            public void onModeChanged(int carId, String mode) {
                System.out.println("Multiplexor: Mode changed for car " + carId + ": " + mode);
            }
            
            @Override
            public void onImageInteraction(String imageType, int imageIndex, String interactionType, String additionalData) {
                System.out.println("Multiplexor: Image interaction - " + imageType + "[" + imageIndex + "] " + interactionType + ": " + additionalData);
            }

            @Override
            public void emitOverloadWeightClick(int buttonIndex) {
                System.out.println("Multiplexor: Button interaction - OverloadWeight[" + buttonIndex + "] WeightExceeded: OVERLOAD");
            }
            @Override
            public void onHallCall(int floor, String direction) {
            }
            @Override
            public void onCabinSelect(int carId, int floor) {
            }
            @Override
            public void onDoorSensor(int carId, boolean blocked) {
 
            }
            @Override
            public void onCarPosition(int carId, int floor, String direction) {

            }

            
        });



        // load images via utility
        loader = new imageLoader();
        loader.loadImages();

        ScrollPane scrollPane = new ScrollPane();
        VBox vbox = new VBox(10);
        HBox hbox = new HBox();

        // Create floor displays & call buttons
        for (int i = 0; i < 10; i++) {
            HBox h = new HBox();
            FloorDisplay floorDisplay = new FloorDisplay(i);
            floorDisplayList.add(floorDisplay);
            CallButton callButton = new CallButton(i);
            callButtonList.add(callButton);
            h.getChildren().addAll(floorDisplay.floorDisplayOverlay, callButton.callButtonOverlay);
            vbox.getChildren().add(h);
        }
        fireAlarm = new FireAlarm();
        vbox.getChildren().add(fireAlarm.fireAlarmOverlay);
        scrollPane.setContent(vbox);
        hbox.getChildren().add(scrollPane);

        // Create cabin panels & elevator doors & overload buttons
        for (int i = 0; i < 4; i++) {
            VBox v = new VBox();
            Panel cabinPanel = new Panel(i);
            cabinPanelList.add(cabinPanel);
            ElevatorDoor elevatorDoor = new ElevatorDoor(i);
            elevDoorList.add(elevatorDoor);
            OverloadWeightTrigger overloadTrigger = new OverloadWeightTrigger(i);
            v.getChildren().addAll(cabinPanel.panelOverlay, elevatorDoor.doorOverlay, overloadTrigger.weightTriggerButton);
            hbox.getChildren().add(v);
            v.setAlignment(Pos.CENTER);
        }

        Scene scene = new Scene(hbox, 1850, 750, Color.web("#c0bfbbff"));
        primaryStage.setTitle("Elevator Passenger Devices");
        primaryStage.setScene(scene);
        primaryStage.show();

        // create listener implementation and register it with model objects
        listenerImpl = new listener() {
            @Override
            public void notify(String event, String payload) {
                Platform.runLater(() -> handleEvent(event, payload));
            }
        };

        try {
            CabinPassengerPanel.setGuiListener(listenerImpl);
        } catch (Throwable t) {}
        try {
            FloorCallButtons.setGuiListener(listenerImpl);
        } catch (Throwable t) {}
        try {
            ElevatorDoorsAssembly.setGuiListener(listenerImpl);
        } catch (Throwable t) {}
        try {
            ElevatorFloorDisplay.setGuiListener(listenerImpl);
        } catch (Throwable t) {}
    }

    // NOT SURE WE NEED THIS STILL
    private void handleEvent(String event, String payload) {
        // int payloadNum;

        // try {
        //     if (event == null) return;
        //     switch (event) {
        //         case "Cabin.pressFloorButton":
        //             payloadNum = Integer.parseInt(payload);
        //             buttonLabels.get(payloadNum-1).setStyle("-fx-text-fill: white;");
        //             break;
        //         case "Cabin.setDisplay":
        //         if (payload != null) {
        //                 String[] parts = payload.split(":", 2);
        //                 floorLabel1.setText(parts[0]);
        //                 if ("UP".equals(parts[1])) setImg(1, 0, 2);
        //                 else if ("DOWN".equals(parts[1])) setImg(1, 0, 1);
        //                 else setImg(1, 0, 0);
        //             }
        //             break;
        //         case "Cabin.playCabinArrivalChime": // 0-2 indices are cabin panels
        //             break;
        //         case "Cabin.playCabinOverloadWarning":
        //             break;
        //         case "Cabin.clearPressedFloors":
        //             setImg(1, 0, 0);
        //             break;
        //         case "Cabin.resetFloorButton":
        //             payloadNum = Integer.parseInt(payload);
        //             buttonLabels.get(payloadNum-1).setStyle("-fx-text-fill: black;");
        //             break;
        //         case "FloorCall.pressUp": // 11-13 indices are call buttons
        //             setImg(3, 0, 13);
        //             break;
        //         case "FloorCall.pressDown":
        //             setImg(3, 0, 12);
        //             break;
        //         case "FloorCall.resetCallButton":
        //             setImg(3, 0, 11);
        //             break;
        //         case "Doors.opening": // 3-7 indices are cabin doors
        //             setImg(2, 0, 4);
        //             break;
        //         case "Doors.opened":
        //             setImg(2, 0, 6);
        //             break;
        //         case "Doors.closing":
        //             setImg(2, 0, 4);
        //             break;
        //         case "Doors.closed":
        //             setImg(2, 0, 3);
        //             break;
        //         case "Doors.obstructionSet":
        //             if ("true".equals(payload)) setImg(2, 0, 5);
        //             else setImg(2, 0, 7);
        //             break;
        //         case "FloorDisplay.update": // 8-10 indices are floor displays
        //             if (payload != null) {
        //                 String[] parts = payload.split(":", 2);
        //                 floorLabel2.setText(parts[0]);
        //                 if (parts[1].equals("UP")) setImg(4, 0, 10);
        //                 else if (parts[1].equals("DOWN")) setImg(4, 0, 9);
        //                 else setAllListImgs(4, 8);
        //             }
        //             break;
        //         case "FloorDisplay.arrivalChime":
        //             break;
        //         case "FloorDisplay.overloadWarning":
        //             break;
        //         default:
        //             // unknown event
        //             break;
        //     }
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
    }

    private class Panel{
        public  ImageView elevPanelImg = new ImageView();
        public StackPane panelOverlay = new StackPane(elevPanelImg);
        public Label digitalLabel;
        private double scaleFactor = 1;
        private int yTranslation = -30; // adjust as needed for non 1:1 scales
        private int panelIndex; // Track which panel this is
        private int carId;

        private Panel(int index){ 
            this.panelIndex = index;
            this.carId = index + 1;
            makePanel(); 
        }

        private void makePanel(){
            elevPanelImg.setPreserveRatio(true);
            elevPanelImg.setFitWidth(250 * scaleFactor);
            elevPanelImg.setImage(loader.imageList.get(0)); // 0-2 indices are cabin panels

            // Digital Display label
            digitalLabel = new Label("1");
            digitalLabel.setStyle("-fx-text-fill: white;");
            digitalLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 32));
            digitalLabel.setTranslateY(-125);
            panelOverlay.getChildren().add(digitalLabel);

            // Button overlays
            for (int i = 1; i < 10; i += 2) {
                Label left = new Label(String.valueOf(i));
                left.setStyle("-fx-text-fill: black;");
                left.setFont(Font.font("Verdana", 16  * scaleFactor));
                left.setTranslateX(-20 * scaleFactor);
                if(scaleFactor != 1) {
                    left.setTranslateY(yTranslation + (17 * i) * scaleFactor); // adjust for scaling
                } else {
                    left.setTranslateY(-52 + (17 * i) * scaleFactor);
                }

                // Capture final reference for lambda
                final int floorNumber = i;
                left.setOnMouseClicked(event -> {
                    left.setStyle("-fx-text-fill: white;");
                    multiplexor.emitCabinPanelClick(carId, panelIndex, floorNumber);
                    multiplexor.onDoorCON(carId, "OPEN");  // open the doors for THIS car

                });

                panelOverlay.getChildren().add(left);
                buttonLabels.add(left);

                Label right = new Label(String.valueOf(i + 1));
                right.setStyle("-fx-text-fill: black;");
                right.setFont(Font.font("Verdana", 16  * scaleFactor));
                right.setTranslateX(20  * scaleFactor);
                if(scaleFactor != 1) {
                    right.setTranslateY(yTranslation + (17 * i) * scaleFactor); // adjust for scaling
                } else {
                    right.setTranslateY(-52 + (17 * i) * scaleFactor);
                }

                final int rightFloorNumber = i + 1;
                right.setOnMouseClicked(event -> {
                    right.setStyle("-fx-text-fill: white;");
                    multiplexor.emitCabinPanelClick(carId, panelIndex, rightFloorNumber);
                });

                panelOverlay.getChildren().add(right);
                buttonLabels.add(right);
            }
        }
    }

    private class ElevatorDoor{
        public ImageView elevDoorsImg = new ImageView();
        public StackPane doorOverlay = new StackPane(elevDoorsImg);
        private int doorIndex;
            private int carId;

        private ElevatorDoor(int index){ 
            this.doorIndex = index;
            this.carId = index + 1;
            makeDoor(); 
        }

        private void makeDoor(){
            elevDoorsImg.setPreserveRatio(true);
            elevDoorsImg.setFitWidth(400);
            elevDoorsImg.setImage(loader.imageList.get(3)); // 3-7 indices are cabin doors

            elevDoorsImg.setOnMouseClicked(event -> {
                boolean isActive = elevDoorsImg.getImage() == loader.imageList.get(6); // Allow place box if fully open

                multiplexor.emitDoorClick(carId, doorIndex, "USER_CLICK");

                if (isActive) {
                    // currently active → turn OFF
                    elevDoorsImg.setImage(loader.imageList.get(7)); // Place box
                } else {
                    // currently inactive → turn ON
                    elevDoorsImg.setImage(loader.imageList.get(6)); // Remove box (or open door fully)
                }
            });
        }
    }

    private class CallButton{
        public  ImageView elevCallButtonsImg = new ImageView();
        public StackPane callButtonOverlay = new StackPane(elevCallButtonsImg);
        private int buttonIndex;

        private CallButton(int index){ 
            this.buttonIndex = index;
            makeCallButton(); 
        }

        private void makeCallButton(){
            elevCallButtonsImg.setPreserveRatio(true);
            elevCallButtonsImg.setFitWidth(100);
            elevCallButtonsImg.setFitHeight(100);
            elevCallButtonsImg.setImage(loader.imageList.get(13)); // 13-15 indices are call buttons

            // Bound the click region with quick maths
            elevCallButtonsImg.setOnMouseClicked(event -> {
                double clickX = event.getX();
                double clickY = event.getY();
                double width = elevCallButtonsImg.getBoundsInLocal().getWidth();
                double height = elevCallButtonsImg.getBoundsInLocal().getHeight();

                // Approximate centers of the upper and lower buttons
                double centerX = width / 2;
                double centerY = height / 2;
                double offsetY = 20;  // how far each button center is from middle
                double radius = 15;   // clickable radius

                // Calculate distances from click point to each button center
                double distToUp = Math.hypot(clickX - centerX, clickY - (centerY - offsetY));
                double distToDown = Math.hypot(clickX - centerX, clickY - (centerY + offsetY));

                if (distToUp <= radius) {
                    // Upper button clicked
                    elevCallButtonsImg.setImage(loader.imageList.get(15)); // up pressed
                    multiplexor.emitCallButtonClick(1, buttonIndex, "UP", buttonIndex);
                } 
                else if (distToDown <= radius) {
                    // Lower button clicked
                    elevCallButtonsImg.setImage(loader.imageList.get(14)); // down pressed
                    multiplexor.emitCallButtonClick(1, buttonIndex, "DOWN", buttonIndex);
                } 
                else {
                    // Clicked outside both button circles — ignore
                    System.out.println("Clicked outside call buttons");
                }
            });
        }
    }

    private class FloorDisplay{
        public ImageView floorDispImg = new ImageView();
        public StackPane floorDisplayOverlay = new StackPane(floorDispImg);
        public Label digitalLabel;
        private int displayIndex;

        private FloorDisplay(int index){ 
            this.displayIndex = index;
            makeDisplay(); 
        }

        private void makeDisplay(){
            floorDispImg.setPreserveRatio(true);
            floorDispImg.setFitWidth(120);
            floorDispImg.setImage(loader.imageList.get(8)); // 8-10 indices are floor displays

            // Digital Display label
            digitalLabel = new Label("1");
            digitalLabel.setStyle("-fx-text-fill: white;");
            digitalLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
            digitalLabel.setTranslateY(-5);
            floorDisplayOverlay.getChildren().add(digitalLabel);

            // Floor number label
            Label floorLabel = new Label(String.valueOf(displayIndex + 1));
            floorLabel.setStyle("-fx-text-fill: black;");
            floorLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 16));
            floorLabel.setTranslateY(55);
            floorDisplayOverlay.getChildren().add(floorLabel);
            
            // Add click handler for floor display
            floorDispImg.setOnMouseClicked(event -> {
                multiplexor.emitFloorDisplayClick(1, displayIndex);
            });
        }
    }

    private class FireAlarm{
        public ImageView fireAlarmImg = new ImageView();
        public StackPane fireAlarmOverlay = new StackPane(fireAlarmImg);

        private FireAlarm(){ makeFireAlarm(); }

        private void makeFireAlarm(){
            fireAlarmImg.setPreserveRatio(true);
            fireAlarmImg.setFitWidth(70);
            fireAlarmImg.setImage(loader.imageList.get(11)); // 11-12 indices are fire alarms

            fireAlarmImg.setOnMouseClicked(event -> {
                boolean isActive = fireAlarmImg.getImage() == loader.imageList.get(12);

                if (isActive) {
                    // currently active → turn OFF
                    fireAlarmImg.setImage(loader.imageList.get(11)); // Normal state
                    multiplexor.emitFireAlarmClick(1);
                } else {
                    // currently inactive → turn ON
                    fireAlarmImg.setImage(loader.imageList.get(12)); // Activated state
                    multiplexor.emitFireAlarmClick(1);
                }
            });
        }
    }

    private class OverloadWeightTrigger{
        public Button weightTriggerButton = new Button("Overload");
        private int buttonIndex;
        private int carId;

        public OverloadWeightTrigger(int index){ 
            this.buttonIndex = index; 
            this.carId = index + 1;
            makeTrigger();
        }

        private void makeTrigger(){
            weightTriggerButton.setPrefWidth(150);
            weightTriggerButton.setPrefHeight(50);
            weightTriggerButton.setStyle("-fx-background-color: #bdbdbdff; -fx-text-fill: black;");
            weightTriggerButton.setFont(Font.font("Times New Roman", FontWeight.BOLD, 22));

            weightTriggerButton.setOnMouseClicked(event -> {
                multiplexor.emitOverloadWeightClick(carId, buttonIndex);
            });
        }
    }

    // Change image
    private void setImg(int ListNumber, int ListIdx, int newImageIdx) {
        if(ListNumber < 1 || ListNumber > 4) ListNumber = 1; // default to cabin panel list
        if (ListNumber == 1) {
            for (int i = 0; i < cabinPanelList.size(); i++) {
                if(i != ListIdx) continue;
                try {
                    if (newImageIdx < 0 || newImageIdx >= loader.imageList.size()) return;
                    Image newImg = loader.imageList.get(newImageIdx);
                    cabinPanelList.get(i).elevPanelImg.setImage(newImg);
                } catch (Exception e) { e.printStackTrace(); }
            }
        } else if (ListNumber == 2) {
            for (int i = 0; i < elevDoorList.size(); i++) {
                if(i != ListIdx) continue;
                try {
                    if (newImageIdx < 0 || newImageIdx >= loader.imageList.size()) return;
                    Image newImg = loader.imageList.get(newImageIdx);
                    elevDoorList.get(i).elevDoorsImg.setImage(newImg);
                } catch (Exception e) { e.printStackTrace(); }
            }
        } else if (ListNumber == 3) {
            for (int i = 0; i < callButtonList.size(); i++) {
                if(i != ListIdx) continue;
                try {
                    if (newImageIdx < 0 || newImageIdx >= loader.imageList.size()) return;
                    Image newImg = loader.imageList.get(newImageIdx);
                    callButtonList.get(i).elevCallButtonsImg.setImage(newImg);
                } catch (Exception e) { e.printStackTrace(); }
            }
        } else if (ListNumber == 4) {
            for (int i = 0; i < floorDisplayList.size(); i++) {
                if(i != ListIdx) continue;
                try {
                    if (newImageIdx < 0 || newImageIdx >= loader.imageList.size()) return;
                    Image newImg = loader.imageList.get(newImageIdx);
                    floorDisplayList.get(i).floorDispImg.setImage(newImg);
                } catch (Exception e) { e.printStackTrace(); }
            }
        } else if (ListNumber == 5) {
            try {
                if (newImageIdx < 0 || newImageIdx >= loader.imageList.size()) return;
                Image newImg = loader.imageList.get(newImageIdx);
                fireAlarm.fireAlarmImg.setImage(newImg);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    // Change all images in a list
    private void setAllListImgs(int ListNumber, int newImageIdx) {
        if(ListNumber < 1 || ListNumber > 4) ListNumber = 1; // default to cabin panel list
        if (ListNumber == 1) {
            for (int i = 0; i < cabinPanelList.size(); i++) {
                try {
                    if (newImageIdx < 0 || newImageIdx >= loader.imageList.size()) return;
                    Image newImg = loader.imageList.get(newImageIdx);
                    cabinPanelList.get(i).elevPanelImg.setImage(newImg);
                } catch (Exception e) { e.printStackTrace(); }
            }
        } else if (ListNumber == 2) {
            for (int i = 0; i < elevDoorList.size(); i++) {
                try {
                    if (newImageIdx < 0 || newImageIdx >= loader.imageList.size()) return;
                    Image newImg = loader.imageList.get(newImageIdx);
                    elevDoorList.get(i).elevDoorsImg.setImage(newImg);
                } catch (Exception e) { e.printStackTrace(); }
            }
        } else if (ListNumber == 3) {
            for (int i = 0; i < callButtonList.size(); i++) {
                try {
                    if (newImageIdx < 0 || newImageIdx >= loader.imageList.size()) return;
                    Image newImg = loader.imageList.get(newImageIdx);
                    callButtonList.get(i).elevCallButtonsImg.setImage(newImg);
                } catch (Exception e) { e.printStackTrace(); }
            }
        } else if (ListNumber == 4) {
            for (int i = 0; i < floorDisplayList.size(); i++) {
                try {
                    if (newImageIdx < 0 || newImageIdx >= loader.imageList.size()) return;
                    Image newImg = loader.imageList.get(newImageIdx);
                    floorDisplayList.get(i).floorDispImg.setImage(newImg);
                } catch (Exception e) { e.printStackTrace(); }
            }
        } else if (ListNumber == 5) {
            try {
                if (newImageIdx < 0 || newImageIdx >= loader.imageList.size()) return;
                Image newImg = loader.imageList.get(newImageIdx);
                fireAlarm.fireAlarmImg.setImage(newImg);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}
