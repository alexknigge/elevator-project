package DeviceOne;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DeviceOneMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        DeviceOne device = new DeviceOne(1);
        primaryStage.setTitle("Device One");
        Scene scene = new Scene(device.getDisplay().getPane());
        primaryStage.setScene(scene);

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

