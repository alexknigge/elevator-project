package pfdGUI;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Node;
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

/** MUX calls api, api modifies the gui. MUX needs to also poll the internal state.
 * Simple JavaFX GUI that listens to model classes via a nested listener
 * interface and swaps images in response to events.
 */
public class gui extends Application {
    private int numElevators = 4; // Total number of elevators
    private int numFloors = 10; // Total number of floors
    private imageLoader loader; // Utility image loader

    // GUI Control/Query Interface (Contains internal state & control methods)
    public GUIControl internalState = new GUIControl();

    // Internal State Devices
    private Panel[] panels = new Panel[numElevators];
    private Door[] doors = new Door[numElevators];
    private Display[] displays = new Display[numElevators];
    private WeighScale[] weighScales = new WeighScale[numElevators];
    private CallButton[] callButtons = new CallButton[numFloors];
    private FireAlarm fireAlarm;

    // Singleton instance for API access
    private static gui instance;
    public gui() { instance = this; }
    public static gui getInstance() { return instance; }


    /**************************************************
     * GUI Control & State Query Interface
     */

     public class GUIControl {
        public GUIControl() {}
        
        // Internal State Variables
        private ArrayList<Integer>[] pressedFloors = new ArrayList[numElevators];
        private boolean[] doorObstructions = new boolean[numElevators];
        private boolean[] cabinOverloads = new boolean[numElevators];
        private boolean fireAlarmActive;

        // Getters for internal state variables
        public ArrayList<Integer> getPressedFloors(int ID) { return pressedFloors[ID]; }
        public boolean getIsDoorObstructed(int ID) { return doorObstructions[ID]; }
        public boolean getIsCabinOverloaded(int ID) { return cabinOverloads[ID]; }
        public boolean getFireAlarmPressed() { return fireAlarmActive; }
        

        // Press panel button
        public void pressPanelButton(int ID, int floorNumber) {
            Platform.runLater(() -> {
                for (Node n : panels[ID].panelOverlay.getChildren()) {
                    if (n instanceof Label lbl && lbl.getText().equals(String.valueOf(floorNumber))) {
                        lbl.setStyle("-fx-text-fill: white;");
                        pressedFloors[ID].add(floorNumber);
                    }
                }
            });
        }

        // Reset panel button
        public void resetPanelButton(int ID, int floorNumber) {
            Platform.runLater(() -> {
                for (Node n : panels[ID].panelOverlay.getChildren()) {
                    if (n instanceof Label lbl && lbl.getText().equals(String.valueOf(floorNumber))) {
                        lbl.setStyle("-fx-text-fill: black;");
                        pressedFloors[ID].remove(Integer.valueOf(floorNumber));
                    }
                }
            });
        }

        // Reset all panel buttons
        public void resetPanel(int ID) {
            Platform.runLater(() -> {
                for (Node n : panels[ID].panelOverlay.getChildren()) {
                    if (n instanceof Label lbl) {
                        lbl.setStyle("-fx-text-fill: black;");
                    }
                }
                pressedFloors[ID].clear();
            });
        }

        // Set the door obstruction state of a given elevator
        public void setDoorObstruction(int ID, boolean isObstructed) {
            Platform.runLater(() -> {
                doorObstructions[ID] = isObstructed;
                if (isObstructed) {
                    doors[ID].elevDoorsImg.setImage(loader.imageList.get(7)); // Obstructed image
                } else {
                    doors[ID].elevDoorsImg.setImage(loader.imageList.get(6)); // Normal closed image
                }
            });
        }

        // Change the door state of a given elevator
        public void changeDoorState(int ID, boolean isOpen) {
            Platform.runLater(() -> {
                if (isOpen) {
                    doors[ID].elevDoorsImg.setImage(loader.imageList.get(4)); // Transition image
                    try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
                    doors[ID].elevDoorsImg.setImage(loader.imageList.get(5)); // Open image

                } else if (!isOpen && doorObstructions[ID]) {
                    doors[ID].elevDoorsImg.setImage(loader.imageList.get(5)); // Obstruction image
                    try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
                    doors[ID].elevDoorsImg.setImage(loader.imageList.get(7)); // Open image

                } else {
                    doors[ID].elevDoorsImg.setImage(loader.imageList.get(4)); // Transition image
                    try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
                    doors[ID].elevDoorsImg.setImage(loader.imageList.get(6)); // Closed image
                }
            });
        }

        // Set the cabin overload state of a given elevator
        public void setCabinOverload(int ID, boolean isOverloaded) {
            Platform.runLater(() -> {
                cabinOverloads[ID] = isOverloaded;
                if (isOverloaded) {
                    weighScales[ID].weightTriggerButton.setStyle("-fx-background-color: #684b4bff; -fx-text-fill: black;");
                } else {
                    weighScales[ID].weightTriggerButton.setStyle("-fx-background-color: #bdbdbdff; -fx-text-fill: black;");
                }
            });
        }

        // Set the floor display of a given elevator
        public void setDisplay(int carId, int floorNumber, String direction) {
            Platform.runLater(() -> {
                displays[carId].digitalLabel.setText(String.valueOf(floorNumber));
                if (direction.contains("UP")) {
                    displays[carId].floorDispImg.setImage(loader.imageList.get(10));
                } else if (direction.contains("DOWN")) {
                    displays[carId].floorDispImg.setImage(loader.imageList.get(9));
                } else {
                    displays[carId].floorDispImg.setImage(loader.imageList.get(8));
                }
            });
        }

        // Set the floor call button state
        public void setCallButton(int floorNumber, String direction) {
            Platform.runLater(() -> {
                if (direction.equals("UP")) {
                    callButtons[floorNumber].elevCallButtonsImg.setImage(loader.imageList.get(15));
                } else if (direction.equals("DOWN")) {
                    callButtons[floorNumber].elevCallButtonsImg.setImage(loader.imageList.get(14));
                }
            });
        }

        // Reset the floor call button state
        public void resetCallButton(int floorNumber) {
            Platform.runLater(() -> {
                callButtons[floorNumber].elevCallButtonsImg.setImage(loader.imageList.get(13));
            });
        }

        // Set the fire alarm state
        public void setFireAlarm(boolean isActive) {
            Platform.runLater(() -> {
                fireAlarmActive = isActive;
                if (isActive) {
                    fireAlarm.fireAlarmImg.setImage(loader.imageList.get(12)); // Active image
                } else {
                    fireAlarm.fireAlarmImg.setImage(loader.imageList.get(11)); // Inactive image
                }
            });
        } 
    }

    /**************************************************
     * JavaFX Application & UI
     */

    @Override
    public void start(Stage primaryStage) {

        // load images via utility
        loader = new imageLoader();
        loader.loadImages();

        ScrollPane scrollPane = new ScrollPane();
        VBox vbox = new VBox(10);
        HBox hbox = new HBox();

        // Create floor displays & call buttons
        for (int i = 0; i < numFloors; i++) {
            callButtons[i] = new CallButton(i);
            vbox.getChildren().addAll(callButtons[i].callButtonOverlay);
        }
        fireAlarm = new FireAlarm();
        vbox.getChildren().add(fireAlarm.fireAlarmOverlay);
        scrollPane.setContent(vbox);
        hbox.getChildren().add(scrollPane);

        // Create cabin panels & elevator doors & overload buttons
        for (int i = 0; i < numElevators; i++) {
            VBox v = new VBox();
            panels[i] = new Panel(i);
            doors[i] = new Door(i);
            displays[i] = new Display(i);
            weighScales[i] = new WeighScale(i);

            v.getChildren().addAll(panels[i].panelOverlay, displays[i].displayOverlay, 
            doors[i].doorOverlay, weighScales[i].weightTriggerButton);

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
                        internalState.pressedFloors[carId].add(leftFloorNumber);
                        left.setStyle("-fx-text-fill: #ffffffff;");
                    });
                });

                panelOverlay.getChildren().add(left);

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
                        internalState.pressedFloors[carId].add(rightFloorNumber);
                        right.setStyle("-fx-text-fill: #ffffffff;");
                    });
                });

                panelOverlay.getChildren().add(right);
            }
        }
    }

    private class Door{
        public ImageView elevDoorsImg = new ImageView();
        public StackPane doorOverlay = new StackPane(elevDoorsImg);
        private int carId;

        private Door(int index){ 
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
                        internalState.doorObstructions[carId] = true;
                        elevDoorsImg.setImage(loader.imageList.get(7));
                    });
                    return;
                } else if(loader.imageList.get(7).equals(elevDoorsImg.getImage())) {
                    Platform.runLater(() -> {
                        internalState.doorObstructions[carId] = false;
                        elevDoorsImg.setImage(loader.imageList.get(6));
                    });
                    return;
                }
            });
        }
    }

    private class Display{
        public ImageView floorDispImg = new ImageView();
        public StackPane displayOverlay = new StackPane(floorDispImg);
        public Label digitalLabel;
        private int displayIndex;

        private Display(int index){ 
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
            displayOverlay.getChildren().add(digitalLabel);

            // Floor number label
            Label floorLabel = new Label(String.valueOf(displayIndex + 1));
            floorLabel.setStyle("-fx-text-fill: black;");
            floorLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 16));
            floorLabel.setTranslateY(55);
            displayOverlay.getChildren().add(floorLabel);
        }
    }

    private class WeighScale{
        public Button weightTriggerButton = new Button("Overload");
        private int buttonIndex;

        public WeighScale(int index){ 
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
                        internalState.cabinOverloads[buttonIndex] = false;                     
                    } else {
                        // Toggle to OVERLOAD
                        weightTriggerButton.setStyle("-fx-background-color: #684b4bff; -fx-text-fill: black;");
                        internalState.cabinOverloads[buttonIndex] = true;
                    }
                });
            });
        }
    }

    private class CallButton{
        public  ImageView elevCallButtonsImg = new ImageView();
        public StackPane callButtonOverlay = new StackPane(elevCallButtonsImg);
        private int buttonIndex;
        private String direction;

        private CallButton(int index){ 
            this.buttonIndex = index;
            this.direction = "IDLE";
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
                        callButtons[buttonIndex].direction = "UP";
                        elevCallButtonsImg.setImage(loader.imageList.get(15));
                    });
                } 
                else if (distToDown <= radius) {
                    // Lower button clicked
                    Platform.runLater(() -> {
                        callButtons[buttonIndex].direction = "DOWN";
                        elevCallButtonsImg.setImage(loader.imageList.get(14));
                    });
                } 
                else {
                    // Clicked outside both button circles — ignore
                    System.out.println("Clicked outside call buttons");
                }
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
                    Platform.runLater(() -> {
                        internalState.fireAlarmActive = true;
                    });
                } else {
                    Platform.runLater(() -> {
                        internalState.fireAlarmActive = false;
                    });
                }
            });
        }
    }

    /**************************************************
    * Main Application Entry Point
    ****************************************
    */

    public static void main(String[] args) {
        launch(args);
    }


}
