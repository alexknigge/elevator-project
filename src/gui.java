import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import utils.imageLoader;

/**
 * Simple JavaFX GUI that listens to model classes via a nested listener
 * interface and swaps images in response to events.
 */
public class gui extends Application {

    /**
     * Public nested listener type so other classes can refer to it as `gui.listener`.
     */
    public static interface listener {
        void notify(String event, String payload);
    }

    private Label floorLabel1;
    private Label floorLabel2;
    private imageLoader loader;
    private listener listenerImpl;
    List<Label> buttonLabels = new ArrayList<>();
    ImageView elevDoorsImg = new ImageView();
    ImageView elevCallButtonsImg = new ImageView();

    @Override
    public void start(Stage primaryStage) {
        // load images via utility
        loader = new imageLoader();
        loader.loadImages();

        elevDoorsImg.setPreserveRatio(true);
        elevCallButtonsImg.setPreserveRatio(true);
        elevDoorsImg.setImage(loader.imageList.get(3)); // 3-7 indices are cabin doors
        elevCallButtonsImg.setImage(loader.imageList.get(11)); // 11-13 indices are call buttons

        // image scaling
        elevDoorsImg.setFitWidth(400);
        elevCallButtonsImg.setFitWidth(120);

        ScrollPane scrollPane = new ScrollPane();
        VBox vbox = new VBox();
        HBox hbox = new HBox();

        // Create floor displays & call buttons
        for (int j = 0; j < 10; j++) {
            HBox h = new HBox();
            FloorDisplay floorDisplay = new FloorDisplay();
            CallButton callButton = new CallButton();
            h.getChildren().addAll(floorDisplay.floorDisplayOverlay, callButton.callButtonOverlay);
            vbox.getChildren().add(h);
        }
        scrollPane.setContent(vbox);
        hbox.getChildren().add(scrollPane);

        // Create cabin panels & elevator doors
        for (int i = 0; i < 4; i++) {
            VBox v = new VBox();
            Panel cabinPanel = new Panel();
            ElevatorDoor elevatorDoor = new ElevatorDoor();
            v.getChildren().addAll(cabinPanel.panelOverlay, elevatorDoor.doorOverlay);
            hbox.getChildren().add(v);
        }

        Scene scene = new Scene(hbox, 1800, 800, Color.web("#c0bfbbff"));
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

    private void handleEvent(String event, String payload) {
        int payloadNum;

        try {
            if (event == null) return;
            switch (event) {
                case "Cabin.pressFloorButton":
                    payloadNum = Integer.parseInt(payload);
                    buttonLabels.get(payloadNum-1).setStyle("-fx-text-fill: white;");
                    break;
                case "Cabin.setDisplay":
                if (payload != null) {
                        String[] parts = payload.split(":", 2);
                        floorLabel1.setText(parts[0]);
                        if ("UP".equals(parts[1])) setImg(elevPanelImg, 2);
                        else if ("DOWN".equals(parts[1])) setImg(elevPanelImg, 1);
                        else setImg(elevPanelImg, 0);
                    }
                    break;
                case "Cabin.playCabinArrivalChime": // 0-2 indices are cabin panels
                    break;
                case "Cabin.playCabinOverloadWarning":
                    break;
                case "Cabin.clearPressedFloors":
                    setImg(elevPanelImg, 0);
                    break;
                case "Cabin.resetFloorButton":
                    payloadNum = Integer.parseInt(payload);
                    buttonLabels.get(payloadNum-1).setStyle("-fx-text-fill: black;");
                    break;
                case "FloorCall.pressUp": // 11-13 indices are call buttons
                    setImg(elevCallButtonsImg, 13);
                    break;
                case "FloorCall.pressDown":
                    setImg(elevCallButtonsImg, 12);
                    break;
                case "FloorCall.resetCallButton":
                    setImg(elevCallButtonsImg, 11);
                    break;
                case "Doors.opening": // 3-7 indices are cabin doors
                    setImg(elevDoorsImg, 4);
                    break;
                case "Doors.opened":
                    setImg(elevDoorsImg, 6);
                    break;
                case "Doors.closing":
                    setImg(elevDoorsImg, 4);
                    break;
                case "Doors.closed":
                    setImg(elevDoorsImg, 3);
                    break;
                case "Doors.obstructionSet":
                    if ("true".equals(payload)) setImg(elevDoorsImg, 5);
                    else setImg(elevDoorsImg, 7);
                    break;
                case "FloorDisplay.update": // 8-10 indices are floor displays
                    if (payload != null) {
                        String[] parts = payload.split(":", 2);
                        floorLabel2.setText(parts[0]);
                        if (parts[1].equals("UP")) setImg(floorDispImg, 10);
                        else if (parts[1].equals("DOWN")) setImg(floorDispImg, 9);
                        else setImg(floorDispImg, 8);
                    }
                    break;
                case "FloorDisplay.arrivalChime":
                    break;
                case "FloorDisplay.overloadWarning":
                    break;
                default:
                    // unknown event
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Panel{
        private ImageView elevPanelImg = new ImageView();
        public StackPane panelOverlay = new StackPane(elevPanelImg);
        private double scaleFactor = 1;
        private int yTranslation = -30; // adjust as needed for non 1:1 scales

        private Panel(){ makePanel(); }

        private void makePanel(){
            elevPanelImg.setPreserveRatio(true);
            elevPanelImg.setFitWidth(250 * scaleFactor);
            elevPanelImg.setImage(loader.imageList.get(0)); // 0-2 indices are cabin panels

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
                left.setOnMouseClicked(event -> left.setStyle("-fx-text-fill: white;"));

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

                right.setOnMouseClicked(event -> right.setStyle("-fx-text-fill: white;"));

                panelOverlay.getChildren().add(right);
                buttonLabels.add(right);
            }
        }
    }

    private class FloorDisplay{
        ImageView floorDispImg = new ImageView();
        public StackPane floorDisplayOverlay = new StackPane(floorDispImg);

        private FloorDisplay(){ makeDisplay(); }

        private void makeDisplay(){
            floorDispImg.setPreserveRatio(true);
            floorDispImg.setFitWidth(120);
            floorDispImg.setImage(loader.imageList.get(8)); // 8-10 indices are floor displays

            Label floorLabel2 = new Label("1");
            floorLabel2.setStyle("-fx-text-fill: white;");
            floorLabel2.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
            floorLabel2.setTranslateY(-185);
        }
    }

    private class ElevatorDoor{
        ImageView elevDoorsImg = new ImageView();
        public StackPane doorOverlay = new StackPane(elevDoorsImg);

        private ElevatorDoor(){ makeDoor(); }

        private void makeDoor(){
            elevDoorsImg.setPreserveRatio(true);
            elevDoorsImg.setFitWidth(400);
            elevDoorsImg.setImage(loader.imageList.get(3)); // 3-7 indices are cabin doors
        }
    }

    private class CallButton{
        private ImageView elevCallButtonsImg = new ImageView();
        public StackPane callButtonOverlay = new StackPane(elevCallButtonsImg);

        private CallButton(){ makeCallButton(); }

        private void makeCallButton(){
            elevCallButtonsImg.setPreserveRatio(true);
            elevCallButtonsImg.setFitWidth(100);
            elevCallButtonsImg.setFitHeight(100);
            elevCallButtonsImg.setImage(loader.imageList.get(11)); // 11-13 indices are call buttons
            elevCallButtonsImg.setOnMouseClicked(event -> {

            double clickY = event.getY();                    // where user clicked
            double imgHeight = elevCallButtonsImg.getBoundsInLocal().getHeight();

            if (clickY < imgHeight / 2) {
                // Upper half clicked
                elevCallButtonsImg.setImage(loader.imageList.get(13)); // top image
            } else {
                // Lower half clicked
                elevCallButtonsImg.setImage(loader.imageList.get(12)); // bottom image
            }
        });
        }
    }

    private class FireAlarm{
        ImageView fireAlarmImg = new ImageView();

        private FireAlarm(){ makeFireAlarm(); }

        private void makeFireAlarm(){
            fireAlarmImg.setPreserveRatio(true);
            fireAlarmImg.setFitWidth(100);
            fireAlarmImg.setImage(loader.imageList.get(14)); // 14-15 indices are fire alarms
        }
    }
    
    // Change image in one of the 4 slots by index in the image list
    private void setImg(ImageView currImg, int newIndex) {
        try {
            if (newIndex < 0 || newIndex >= loader.imageList.size()) return;
            Image newImg = loader.imageList.get(newIndex);
            currImg.setImage(newImg);
        } catch (Exception e) {
            // guard against any GUI update errors
            e.printStackTrace();
        }
    }
}
