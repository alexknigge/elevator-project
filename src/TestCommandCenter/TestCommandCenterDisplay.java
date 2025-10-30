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

        checkForIncomingMessage();
    }

    private void checkForIncomingMessage() {
        Thread thread = new Thread(() -> {
            while (true) {
                Message message = softwareBus.get(2, 1);
                if(message != null) {
                    handleNewMessage(message);
                }
            }
        });
        thread.start();
    }

    private void handleSubmit() {
        String messageString = messageToBeSent.getText();

        if (messageString.isEmpty()) {
            return;
        }

        if (!messageString.matches("\\d+-\\d+-.+")) {
            System.out.println("Invalid format. Expected: <topic>-<subtopic>-<body>");
            return;
        }

        Message messageToBeSent = Message.parseStringToMsg(messageString);
        handleSendMessage(messageToBeSent);

        softwareBus.publish(messageToBeSent);
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




}
