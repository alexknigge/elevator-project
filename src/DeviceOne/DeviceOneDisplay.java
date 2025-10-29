package DeviceOne;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class DeviceOneDisplay {
    private DeviceOne device;
    private DeviceOneInput userInput;

    private BorderPane display;

    private Label messagesReceived;
    private TextField messageToBeSent;
    private Button submitMessage;


    public DeviceOneDisplay(DeviceOne device) {
        userInput = new DeviceOneInput(device, this);

        display = new BorderPane();
        messagesReceived = new Label("Test");
        StackPane messagePane = new StackPane();
        messagePane.setMinSize(100,100);
        messagePane.getChildren().add(messagesReceived);
        display.setTop(messagePane);


        messageToBeSent = new TextField();
        messageToBeSent.setMaxSize(250,20);
        StackPane textPane = new StackPane();
        textPane.setMinSize(350,150);
        textPane.getChildren().add(messageToBeSent);
        textPane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE,
                CornerRadii.EMPTY, Insets.EMPTY
                )));
        display.setCenter(textPane);

        submitMessage = new Button("Submit Message");
        submitMessage.setOnAction(event -> userInput.handleSubmit());
        StackPane buttonPane = new StackPane();
        buttonPane.getChildren().add(submitMessage);
        buttonPane.setAlignment(Pos.CENTER);
        display.setRight(buttonPane);

    }

    public BorderPane getPane() {
        return display;
    }

    public TextField getMessageToBeSent() {
        return messageToBeSent;
    }
}
