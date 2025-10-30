package TestCommandCenter;


import Bus.SoftwareBus;
import Message.Message;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class TestCommandCenterDisplay {
    private SoftwareBus softwareBus;

    private final BorderPane display;

    private Label messageStatus;
    private TextField messageToBeSent;
    private Button submitMessage;


    public TestCommandCenterDisplay(SoftwareBus softwareBus) {
        this.softwareBus = softwareBus;

        display = new BorderPane();
        messageStatus = new Label("");
        StackPane messagePane = new StackPane();
        messagePane.setMinSize(100, 100);
        messagePane.getChildren().add(messageStatus);
        display.setTop(messagePane);


        messageToBeSent = new TextField();
        messageToBeSent.setMaxSize(250, 20);
        StackPane textPane = new StackPane();
        textPane.setMinSize(350, 150);
        textPane.getChildren().add(messageToBeSent);
        textPane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE,
                CornerRadii.EMPTY, Insets.EMPTY
        )));
        display.setCenter(textPane);

        submitMessage = new Button("Submit Message");
        submitMessage.setOnAction(event -> handleSubmit());
        StackPane buttonPane = new StackPane();
        buttonPane.getChildren().add(submitMessage);
        buttonPane.setAlignment(Pos.CENTER);
        display.setRight(buttonPane);

    }

    public void handleNewMessage(Message message) {
        messageStatus.setText("Message Received!\n" + message.toString());
    }

    public void handleSendMessage(Message message) {
        messageStatus.setText("Message Sent!\n" + message.toString());
    }

    public BorderPane getPane() {
        return display;
    }

    public void handleSubmit() {
        String message = messageToBeSent.getText();
        if (!message.isEmpty()) {
            int topic = 1;
            int subtopic = 0;
            Message messageToBeSent = new Message(topic, subtopic, message);
            handleSendMessage(messageToBeSent);

            softwareBus.publish(messageToBeSent);
        }
    }
}
