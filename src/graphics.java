import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.geometry.Pos;

public class graphics extends Application {
    private static Label floorLabel;
    private static Label directionLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        floorLabel = new Label("Floor: 1");
        directionLabel = new Label("Direction: IDLE");

        VBox root = new VBox(20, floorLabel, directionLabel);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 300, 200);

        primaryStage.setTitle("Elevator Passenger Panel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
