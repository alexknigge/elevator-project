package pfdGUI;

import java.util.ArrayList;
import java.util.List;

import bus.SoftwareBus;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import mux.DeviceMultiplexor;
import pfdAPI.*;
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
    private SoftwareBus bus;

    private Label floorLabel1;
    private Label floorLabel2;
    private DeviceMultiplexor multiplexor;
    List<Label> buttonLabels = new ArrayList<>();
    ArrayList<Panel> cabinPanelList = new ArrayList<>();
    ArrayList<ElevatorDoor> elevDoorList = new ArrayList<>();
    ArrayList<CallButton> callButtonList = new ArrayList<>();
    ArrayList<FloorDisplay> floorDisplayList = new ArrayList<>();
    ArrayList<Elevator> eList = new ArrayList<>();
    FireAlarm fireAlarm;

    @Override
    public void start(Stage primaryStage) {
        // Initialize the multiplexor
        multiplexor = new DeviceMultiplexor();

        // Create 4 elevators, store them for later use, and register them with the MUX
        Elevator e1 = new Elevator(1, 10, multiplexor);
        Elevator e2 = new Elevator(2, 10, multiplexor);
        Elevator e3 = new Elevator(3, 10, multiplexor);
        Elevator e4 = new Elevator(4, 10, multiplexor);

        eList.add(e1);
        eList.add(e2);
        eList.add(e3);
        eList.add(e4);

        multiplexor.registerCar(e1);
        multiplexor.registerCar(e2);
        multiplexor.registerCar(e3);
        multiplexor.registerCar(e4);
        
        /**
         * Event handling from the MUX to update the GUI
         */
        multiplexor.setListener(new DeviceMultiplexor.DeviceListener() {

            @Override
            // Display update event handling
            public void onDisplayUpdate(int carId, int floor, String text) {
                System.out.println("Multiplexor: Display update for car " + carId + ": " + text);

            int listIndex = carId - 1;
                if (text == null) return;

                // Change direction arrow on display
                if (text.contains("UP")) {
                    setImg(1, listIndex, 2);
                } else if (text.contains("DOWN")) {
                    setImg(1, listIndex, 1);
                } else {
                    setImg(1, listIndex, 0);
                }

                // Change floor number on display
                if(floorDisplayList.get(listIndex) != null) { floorDisplayList.get(listIndex).digitalLabel.setText(Integer.toString(floor)); }
            }
            @Override
            // Door state change event handling
            public void onDoorStateChanged(int carId, String state) {
                System.out.println("Multiplexor: Door state changed for car " + carId + ": " + state);

                int listIndex = carId - 1;
                if (listIndex < 0) listIndex = 0;

                if (state == null) return;
                String s = state.toUpperCase();

                // Change door image based on state
                if (s.contains("OPEN")) {
                    setImg(2, listIndex, 6);
                } else if (s.contains("CLOSE")) {
                    setImg(2, listIndex, 3);
                } else {
                    setImg(2, listIndex, 4);
                }
            }
            @Override
            // Car arrival event handling
            public void onCarArrived(int carId, int floor, String direction) {
                System.out.println("Multiplexor: Car " + carId + " arrived at floor " + floor + " going " + direction);
            }
            
            @Override
            // Call reset event handling
            public void onCallReset(int floor) {
                System.out.println("Multiplexor: Call reset for floor " + floor);
            }
            
            @Override
            // Cabin load event handling
            public void onCabinLoad(int carId, int weight) {
                System.out.println("Multiplexor: Cabin load changed for car " + carId + ": " + weight);
            }
            
            @Override
            // Mode change event handling
            public void onModeChanged(int carId, String mode) {
                System.out.println("Multiplexor: Mode changed for car " + carId + ": " + mode);
            }
            
            @Override
            // Image interaction event handling
            public void onImageInteraction(String imageType, int imageIndex, String interactionType, String additionalData) {
                System.out.println("Multiplexor: Image interaction - " + imageType + "[" + imageIndex + "] " + interactionType + ": " + additionalData);
            }
            @Override
            // Call elevator event handling
            public void onHallCall(int floor, String direction) {}
            @Override
            // Cabin select event handling
            public void onCabinSelect(int carId, int floor) {}
            @Override
            // Door sensor event handling
            public void onDoorSensor(int carId, boolean blocked) {}
            @Override
            // Car position event handling
            public void onCarPosition(int carId, int floor, String direction) {}

            
        });



        // load images via utility
        loader = new imageLoader();
        loader.loadImages();

        ScrollPane scrollPane = new ScrollPane();
        VBox vbox = new VBox(10);
        HBox hbox = new HBox();

        // Create floor displays & call buttons
        for (int i = 1; i <= 10; i++) {
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
                final int leftFloorNumber = i;
                left.setOnMouseClicked(event -> {
                    left.setStyle("-fx-text-fill: white;");
                    eList.get(panelIndex).panel.pressFloorButton(leftFloorNumber);
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
                    eList.get(panelIndex).panel.pressFloorButton(rightFloorNumber);
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
            makeDoor(); 
        }

        private void makeDoor(){
            elevDoorsImg.setPreserveRatio(true);
            elevDoorsImg.setFitWidth(400);
            elevDoorsImg.setImage(loader.imageList.get(6)); // 3-7 indices are cabin doors

            elevDoorsImg.setOnMouseClicked(event -> {

                // If door is open, allow placing/removing an obstruction by clicking
                if(loader.imageList.get(6).equals(elevDoorsImg.getImage())) {
                    // Door is fully closed, open doors
                    elevDoorsImg.setImage(loader.imageList.get(7)); // Place obstruction
                    eList.get(doorIndex).doors.setObstruction(true);
                    return;
                } else if(loader.imageList.get(7).equals(elevDoorsImg.getImage())) {
                    // Door has obstruction, remove obstruction
                    elevDoorsImg.setImage(loader.imageList.get(6)); // Remove obstruction
                    eList.get(doorIndex).doors.setObstruction(false);
                    return;
                }
            });
        }
    }

    private class CallButton{
        public  ImageView elevCallButtonsImg = new ImageView();
        public StackPane callButtonOverlay = new StackPane(elevCallButtonsImg);
        private FloorCallButtons callButton;
        private int buttonIndex;

        private CallButton(int index){ 
            this.buttonIndex = index;
            this.callButton = new FloorCallButtons(index, 10, multiplexor);
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
                    callButton.pressUpCall();
                } 
                else if (distToDown <= radius) {
                    // Lower button clicked
                    elevCallButtonsImg.setImage(loader.imageList.get(14)); // down pressed
                    callButton.pressDownCall();
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
                    multiplexor.imgInteracted("FireAlarm", 0, "AlarmActivated", "EMERGENCY");
                    multiplexor.setMode(0, "NORMAL");
                    multiplexor.emit("No more fire", false);
                } else {
                    // currently inactive → turn ON
                    fireAlarmImg.setImage(loader.imageList.get(12)); // Activated state
                    multiplexor.imgInteracted("FireAlarm", 0, "AlarmActivated", "EMERGENCY");
                    multiplexor.setMode(0, "EMERGENCY");
                    multiplexor.emit("FIRE", false);
                }
            });
        }
    }

    private class OverloadWeightTrigger{
        public Button weightTriggerButton = new Button("Overload");
        private int buttonIndex;

        public OverloadWeightTrigger(int index){ 
            this.buttonIndex = index; 
            makeTrigger();
        }

        private void makeTrigger(){
            weightTriggerButton.setPrefWidth(150);
            weightTriggerButton.setPrefHeight(50);
            weightTriggerButton.setStyle("-fx-background-color: #bdbdbdff; -fx-text-fill: black;");
            weightTriggerButton.setFont(Font.font("Times New Roman", FontWeight.BOLD, 22));

            weightTriggerButton.setOnMouseClicked(event -> {
                
                // Notify the multiplexor of the overload weight click
                multiplexor.imgInteracted("OverloadWeight", buttonIndex, "WeightExceeded", "OVERLOAD");
                multiplexor.setMode(buttonIndex+1, "OVERLOAD");
                multiplexor.emit(buttonIndex+1 + "", false);
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
}
