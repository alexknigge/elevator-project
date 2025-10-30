package TestProcessor;

import Bus.SoftwareBus;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestProcessorMain extends Application {

    @Override
    public void start(Stage primaryStage) {
        SoftwareBus softwareBus = new SoftwareBus(false);
        TestProcessorDisplay display = new TestProcessorDisplay(softwareBus);

        primaryStage.setTitle("Test Command Center");
        Scene scene = new Scene(display.getPane());
        primaryStage.setScene(scene);

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
