package DeviceTwo;

import javafx.application.Application;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DeviceTwoMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        DeviceTwo device = new DeviceTwo(1);
        primaryStage.setTitle("Device Two");
        Scene scene = new Scene(device.getDisplay().getPane());
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(500);
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
