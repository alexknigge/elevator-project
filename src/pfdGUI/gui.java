package pfdGUI;

import java.util.ArrayList;
import java.util.List;

import bus.SoftwareBus;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import mux.BuildingMultiplexor;
import mux.ElevatorMultiplexor;
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
import javafx.scene.image.ImageView;
import utils.imageLoader;

/**
 * Simple JavaFX GUI that listens to model classes via a nested listener
 * interface and swaps images in response to events.
 */
public class gui extends Application {
    private int numElevators = 4; // Total number of elevators
    private int numFloors = 10; // Total number of floors

    private imageLoader loader;
    private SoftwareBus bus;
    private BuildingMultiplexor bMUX = new BuildingMultiplexor();
    private Elevator[] elevators = new Elevator[numElevators];
    ElevatorMultiplexor[] eMUX = new ElevatorMultiplexor[numElevators];

    private List<Label> buttonLabels = new ArrayList<>();
    private ArrayList<Panel> cabinPanelList = new ArrayList<>();
    private ArrayList<ElevatorDoor> elevDoorList = new ArrayList<>();
    private ArrayList<CallButton> callButtonList = new ArrayList<>();
    private ArrayList<FloorDisplay> floorDisplayList = new ArrayList<>();
    private ArrayList<OverloadWeightTrigger> overloadTriggerList = new ArrayList<>();
    private FireAlarm fireAlarm;

    @Override
    public void start(Stage primaryStage) {
        /**
         * Event handling from the bldg MUX to update the GUI
         */
        bMUX.setListener(new BuildingMultiplexor.BuildingDeviceListener() {

            @Override
            // Image interaction tracker
            public void onImageInteraction(String imageType, int imageIndex, String interactionType, String additionalData) {
                System.out.println("Image interaction - " + imageType + "[" + imageIndex + "] " + interactionType + ": " + additionalData);
            }

            @Override
            // Display update event handling
            public void onDisplayUpdate(int carId, int floor, String direction) {
                ImageView img = floorDisplayList.get(carId).floorDispImg;

                // Change floor number on display and the direction arrow
                if(floorDisplayList.get(carId) != null) { 
                    floorDisplayList.get(carId).digitalLabel.setText(Integer.toString(floor)); 
                    if (direction.contains("UP")) {
                        img.setImage(loader.imageList.get(10));
                    } else if (direction.contains("DOWN")) {
                        img.setImage(loader.imageList.get(9));
                    } else {
                        img.setImage(loader.imageList.get(8));
                    }
                }
            }
            
            @Override
            // Call button event handling
            public void onCallCar(int floor, String direction) {
                ImageView img = callButtonList.get(floor).elevCallButtonsImg;

                // Change call button image based on direction
                if(callButtonList.get(floor) != null && img.getImage() == loader.imageList.get(13)) { 
                    if (direction.contains("UP")) {
                        img.setImage(loader.imageList.get(15));
                    } else if (direction.contains("DOWN")) {
                        img.setImage(loader.imageList.get(14));
                    }
                }
            }

            @Override
            // Call reset event handling
            public void onCallReset(int floor) {
                ImageView img = callButtonList.get(floor).elevCallButtonsImg;
                img.setImage(loader.imageList.get(13));
            }

            @Override
            // Fire alarm event handling
            public void onFireAlarm(boolean active) {
                ImageView img = fireAlarm.fireAlarmImg;

                if (active) {
                    img.setImage(loader.imageList.get(12));
                } else {
                    img.setImage(loader.imageList.get(11));
                }
            }
        });

        /**
         * Event handling from the elevator MUXs to update the GUI
         */
        ElevatorMultiplexor.ElevatorDeviceListener listener = new ElevatorMultiplexor.ElevatorDeviceListener() {

            @Override
            // Image interaction tracker
            public void onImageInteraction(String imageType, int imageIndex, String interactionType, String additionalData) {
                System.out.println("Image interaction - " + imageType + "[" + imageIndex + "] " + interactionType + ": " + additionalData);
            }

            @Override
            // Display update event handling
            public void onDisplayUpdate(int carId, int floor, String direction) {
                int listIndex = carId - 1;
                ImageView img = floorDisplayList.get(listIndex).floorDispImg;

                // Change floor number on display and the direction arrow
                if(floorDisplayList.get(listIndex) != null) { 
                    floorDisplayList.get(listIndex).digitalLabel.setText(Integer.toString(floor)); 
                    if (direction.contains("UP")) {
                        img.setImage(loader.imageList.get(10));
                    } else if (direction.contains("DOWN")) {
                        img.setImage(loader.imageList.get(9));
                    } else {
                        img.setImage(loader.imageList.get(8));
                    }
                }
            }

            @Override
            // Panel button select event handling
            public void onPanelButtonSelect(int carId, int floor) {
                Label btnLabel = buttonLabels.get((floor-1) + (carId) * 10);
                btnLabel.setStyle("-fx-text-fill: #ffffffff;");
            }

            @Override
            // Door state change event handling
            public void onDoorStateChanged(int carId, String state) {
                ImageView img = elevDoorList.get(carId).elevDoorsImg;

                // Change door image based on state
                if(elevDoorList.get(carId) != null) { 
                    if (state.contains("ING")) {
                        img.setImage(loader.imageList.get(4));
                    } else if (state.contains("CLOSED")) {
                        img.setImage(loader.imageList.get(3));
                    } else if (state.contains("OPEN")) {
                        img.setImage(loader.imageList.get(6));
                    }
                }
            }

            @Override
            // Door obstruction event handling
            public void onDoorObstructed(int carId, boolean blocked) {
                ImageView img = elevDoorList.get(carId).elevDoorsImg;

                // Change door image based on obstruction
                if(elevDoorList.get(carId) != null) { 
                    if (blocked) {
                        img.setImage(loader.imageList.get(7));
                    } else {
                        img.setImage(loader.imageList.get(6));
                    }
                }
            }

            @Override
            // Cabin overload event handling
            public void onCabinOverload(int carId, boolean overloaded) {
                if (overloaded) {
                    overloadTriggerList.get(carId).weightTriggerButton.setStyle("-fx-background-color: #684b4bff;");
                } else {
                    overloadTriggerList.get(carId).weightTriggerButton.setStyle("-fx-background-color: #bdbdbdff;");
                }
            }
        };

        // Create elevators/MUXs and set their listeners
        for(int i = 0; i < numElevators; i++) {
            eMUX[i] = new ElevatorMultiplexor(i);
            eMUX[i].setListener(listener);
            elevators[i] = new Elevator(i + 1, numFloors);
        }

        // load images via utility
        loader = new imageLoader();
        loader.loadImages();

        ScrollPane scrollPane = new ScrollPane();
        VBox vbox = new VBox(10);
        HBox hbox = new HBox();

        // Create floor displays & call buttons
        for (int i = 0; i < numFloors; i++) {
            CallButton callButton = new CallButton(i);
            callButtonList.add(callButton);
            vbox.getChildren().addAll(callButton.callButtonOverlay);
        }
        fireAlarm = new FireAlarm();
        vbox.getChildren().add(fireAlarm.fireAlarmOverlay);
        scrollPane.setContent(vbox);
        hbox.getChildren().add(scrollPane);

        // Create cabin panels & elevator doors & overload buttons
        for (int i = 0; i < numElevators; i++) {
            VBox v = new VBox();
            Panel cabinPanel = new Panel(i);
            cabinPanelList.add(cabinPanel);
            ElevatorDoor elevatorDoor = new ElevatorDoor(i);
            elevDoorList.add(elevatorDoor);
            OverloadWeightTrigger overloadTrigger = new OverloadWeightTrigger(i);
            overloadTriggerList.add(overloadTrigger);
            FloorDisplay floorDisplay = new FloorDisplay(i);
            floorDisplayList.add(floorDisplay);
            v.getChildren().addAll(cabinPanel.panelOverlay, floorDisplay.floorDisplayOverlay, elevatorDoor.doorOverlay, overloadTrigger.weightTriggerButton);
            hbox.getChildren().add(v);
            v.setAlignment(Pos.CENTER);
        }

        Scene scene = new Scene(hbox, 1700, 800, Color.web("#c0bfbbff"));
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
        private int carId;

        private Panel(int index){ 
            this.carId = index;
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
                    Platform.runLater(() -> {
                        eMUX[carId].getListener().onImageInteraction("PanelButtonPressed", leftFloorNumber, "PanelButtonPressed:", " Floor " + leftFloorNumber);
                        eMUX[carId].getListener().onPanelButtonSelect(carId, leftFloorNumber);
                        eMUX[carId].emit(carId + "", false);
                    });
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
                    Platform.runLater(() -> {
                        eMUX[carId].getListener().onImageInteraction("PanelButtonPressed", rightFloorNumber, "PanelButtonPressed:", " Floor " + rightFloorNumber);
                        eMUX[carId].getListener().onPanelButtonSelect(carId, rightFloorNumber);
                        eMUX[carId].emit(carId + "", false);
                    });
                });

                panelOverlay.getChildren().add(right);
                buttonLabels.add(right);
            }
        }
    }

    private class ElevatorDoor{
        public ImageView elevDoorsImg = new ImageView();
        public StackPane doorOverlay = new StackPane(elevDoorsImg);
        private int carId;

        private ElevatorDoor(int index){ 
            this.carId = index;
            makeDoor(); 
        }

        private void makeDoor(){
            elevDoorsImg.setPreserveRatio(true);
            elevDoorsImg.setFitWidth(400);
            elevDoorsImg.setImage(loader.imageList.get(6)); // 3-7 indices are cabin doors

            elevDoorsImg.setOnMouseClicked(event -> {

                // If door is open, allow placing/removing an obstruction by clicking
                if(loader.imageList.get(6).equals(elevDoorsImg.getImage())) {
                    Platform.runLater(() -> {
                        eMUX[carId].getListener().onImageInteraction("Door", carId, "RemoveObstruction:", " Door " + carId);
                        eMUX[carId].getListener().onDoorObstructed(carId, true);
                        eMUX[carId].emit(carId + "", false);
                    });
                    return;
                } else if(loader.imageList.get(7).equals(elevDoorsImg.getImage())) {
                    Platform.runLater(() -> {
                        eMUX[carId].getListener().onImageInteraction("Door", carId, "RemoveObstruction:", " Door " + carId);
                        eMUX[carId].getListener().onDoorObstructed(carId, false);
                        eMUX[carId].emit(carId + "", false);
                    });
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
            this.callButton = new FloorCallButtons(index, 10);
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
                    Platform.runLater(() -> {
                    bMUX.getListener().onImageInteraction("CallButton", buttonIndex, "DirectionPress:", "UP" + "_FLOOR_" + buttonIndex);
                    bMUX.getListener().onCallCar(buttonIndex, "UP");
                    bMUX.emit(buttonIndex + "", false);
                    });
                } 
                else if (distToDown <= radius) {
                    // Lower button clicked
                    Platform.runLater(() -> {
                        bMUX.getListener().onImageInteraction("CallButton", buttonIndex, "DirectionPress:", "DOWN" + "_FLOOR_" + buttonIndex);
                        bMUX.getListener().onCallCar(buttonIndex, "DOWN");
                        bMUX.emit(buttonIndex + "", false);
                    });
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
                    Platform.runLater(() -> {
                        bMUX.getListener().onImageInteraction("FireAlarm", 0, "AlarmActivated", "EMERGENCY");
                        bMUX.getListener().onFireAlarm(true);
                        bMUX.emit("FIRE", false);
                    });
                } else {
                    Platform.runLater(() -> {
                        bMUX.getListener().onImageInteraction("FireAlarm", 0, "AlarmDeactivated", "NO_EMERGENCY");
                        bMUX.getListener().onFireAlarm(false);
                        bMUX.emit("No more fire", false);
                    });
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
                Platform.runLater(() -> {
                    String style = weightTriggerButton.getStyle();
                    boolean isOverloaded = style.contains("#684b4bff");

                    if (isOverloaded) {
                        // Toggle to NORMAL
                        weightTriggerButton.setStyle("-fx-background-color: #bdbdbdff; -fx-text-fill: black;");
                        eMUX[buttonIndex].getListener().onCabinOverload(buttonIndex, false);
                        eMUX[buttonIndex].getListener().onImageInteraction("OverloadWeight", buttonIndex, "Reset", "NORMAL");                        
                    } else {
                        // Toggle to OVERLOAD
                        weightTriggerButton.setStyle("-fx-background-color: #684b4bff; -fx-text-fill: black;");
                        eMUX[buttonIndex].getListener().onCabinOverload(buttonIndex, true);
                        eMUX[buttonIndex].getListener().onImageInteraction("OverloadWeight", buttonIndex, "WeightExceeded", "OVERLOAD");
                    }

                    eMUX[buttonIndex].emit(buttonIndex + "", false);
                });
            });
        }
    }

// **********************************************************************

    public static void main(String[] args) {
        launch(args);
    }


}
