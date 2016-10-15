import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;
import java.util.Scanner;

/**
 * Created by Petru on 12-Oct-16.
 */
public class ClientController {
    //fields
    @FXML
    private TextField userInputField;
    @FXML
    private TextArea chatField;
    @FXML
    private TextArea activeUsersField;
    @FXML
    private Button sendButton;

    private MainClient mainClient; //reference to the main client


    // initialize the scene
    @FXML
    private void initialize() {
        //set up scene objects
        chatField.setText("Welcome to Le Chat!\nPlease type in your message and press 'Send'\n\n");
        userInputField.setPromptText("Please type in your message and press 'Send'");
        sendButton.setDefaultButton(true);
    }


    //method to initialize the reference to the main client
    public void setMainClient(MainClient mainClient) {
        this.mainClient = mainClient;
    }


    //'Send' button action handler
    @FXML
    private void handleSendButton() {
        if (userInputField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("No message!");
            alert.setContentText("Please enter your message before you hit \"Send\"");
            alert.showAndWait();

        } else {
            TCPClient.sendButton(userInputField.getText());
            userInputField.clear();
        }
    }


    //method to display incoming messages
    public void handleChatField(String message) {
        chatField.appendText(message + "\n");
    }


    //method to display the active user list
    public void handleActiveUsersField(String message) {
        activeUsersField.setText("Active users:\n");

        Scanner scanner = new Scanner(message);
        while (scanner.hasNext()) {
            activeUsersField.appendText(scanner.next() + "\n");
        }
    }


    //method to handle the close request
    public void setExit() {
        mainClient.getStage().setOnCloseRequest(e -> {
            //show confirm alert
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure?");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) { //user confirmed
                TCPClient.exit();

            } else { //'cancel' pressed
                e.consume();
                alert.close();
            }
        });
    }

}
