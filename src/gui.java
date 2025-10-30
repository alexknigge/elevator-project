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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
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
    ImageView floorDispImg = new ImageView();
    ImageView elevPanelImg = new ImageView();
    ImageView elevCallButtonsImg = new ImageView();

    @Override
    public void start(Stage primaryStage) {
        // load images via utility
        loader = new imageLoader();
        loader.loadImages();

        elevDoorsImg.setPreserveRatio(true);
        floorDispImg.setPreserveRatio(true);
        elevPanelImg.setPreserveRatio(true);
        elevCallButtonsImg.setPreserveRatio(true);
        elevDoorsImg.setImage(loader.imageList.get(3)); // 3-7 indices are cabin doors
        floorDispImg.setImage(loader.imageList.get(8)); // 8-10 indices are floor displays
        elevPanelImg.setImage(loader.imageList.get(0)); // 0-2 indices are cabin panels
        elevCallButtonsImg.setImage(loader.imageList.get(11)); // 11-13 indices are call buttons

        // image scaling
        elevPanelImg.setFitWidth(250);
        floorDispImg.setFitWidth(120);
        elevDoorsImg.setFitWidth(400);
        elevCallButtonsImg.setFitWidth(120);

        StackPane overlay1 = new StackPane(elevPanelImg);
        StackPane overlay2 = new StackPane(floorDispImg);
        StackPane.setAlignment(floorDispImg, Pos.TOP_CENTER);
        HBox hbox = new HBox(0);
        hbox.setAlignment(Pos.CENTER);
        
        Line divider = new Line(0, 0, 0, 800);
        divider.setStrokeWidth(2);
        divider.setStroke(Color.GRAY);

        // Button overlays
        for (int i = 1; i < 10; i += 2) {
            Label left = new Label(String.valueOf(i));
            left.setStyle("-fx-text-fill: black;");
            left.setFont(Font.font("Verdana", 16));
            left.setTranslateX(-20);
            left.setTranslateY(-52 + (17 * i));

            // Capture final reference for lambda
            left.setOnMouseClicked(event -> left.setStyle("-fx-text-fill: white;"));

            overlay1.getChildren().add(left);
            buttonLabels.add(left);

            Label right = new Label(String.valueOf(i + 1));
            right.setStyle("-fx-text-fill: black;");
            right.setFont(Font.font("Verdana", 16));
            right.setTranslateX(20);
            right.setTranslateY(-52 + (17 * i));

            right.setOnMouseClicked(event -> right.setStyle("-fx-text-fill: white;"));

            overlay1.getChildren().add(right);
            buttonLabels.add(right);
        }
        floorLabel1 = new Label("-");
        floorLabel1.setStyle("-fx-text-fill: white;");
        floorLabel1.setFont(Font.font("Verdana", FontWeight.BOLD, 32));
        floorLabel1.setTranslateY(-125);
        overlay1.getChildren().add(floorLabel1);
        floorLabel2 = new Label("-");
        floorLabel2.setStyle("-fx-text-fill: white;");
        floorLabel2.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        floorLabel2.setTranslateY(-185);
        overlay2.getChildren().add(floorLabel2);

        StackPane stacked = new StackPane(elevDoorsImg, floorDispImg, overlay2);
        hbox.getChildren().addAll(stacked, elevCallButtonsImg, divider, overlay1);

        Scene scene = new Scene(hbox, 770, 450, Color.web("#c0bfbbff"));
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
