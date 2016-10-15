import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;
import java.util.Scanner;

/**
 * Created by Petru on 12-Oct-16.
 */
public class ClientController {

    @FXML
    private TextField userInputField;
    @FXML
    private TextArea chatField;
    @FXML
    private TextArea activeUsersField;
    @FXML
    private Button sendButton;

    private MainClient mainClient;

    // initialize the controller class
    @FXML
    private void initialize() {
        chatField.setText("Welcome to Le Chat!\nPlease type in your message and press 'Send'\n\n");
        userInputField.setPromptText("Please type in your message and press 'Send'");
        sendButton.setDefaultButton(true);
    }

    public void setMainClient(MainClient mainClient) {
        this.mainClient = mainClient;
    }

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


    public void handleChatField(String message) {
        chatField.appendText(message + "\n");
    }

    public void handleActiveUsersField(String message) {
        activeUsersField.setText("Active users:\n");

        Scanner scanner = new Scanner(message);
        while (scanner.hasNext()) {
            activeUsersField.appendText(scanner.next() + "\n");
        }
    }


    public void setExit() {
        mainClient.getStage().setOnCloseRequest(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure?");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
                TCPClient.exit();
            } else {
                e.consume();
                alert.close();
            }

        });
    }


}
