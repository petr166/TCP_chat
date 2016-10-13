import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Created by Petru on 12-Oct-16.
 */
public class ClientController {

    @FXML
    private TextArea chatField;
    @FXML
    private TextArea activeUsersField;
    @FXML
    private TextField userInputField;
    @FXML
    private Button sendButton;

    // initialize the controller class
    @FXML
    private void initialize() {
        sendButton.setDefaultButton(true);
    }

    @FXML
    private void handleSendButton() {
        if (userInputField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("No message!");
            alert.setContentText("Please enter your message before you hit \"Send\"");
            alert.show();

        } else {
            TCPClient.sendButton(userInputField.getText());
            userInputField.clear();
        }
    }


    public void handleChatField(String message) {
        chatField.appendText(message + "\n");
    }

}
