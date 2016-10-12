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
    static TextArea chatField, activeUsersField;
    TextField userInputField;
    Button sendButton;

    // initialize the controller class
    @FXML
    private void initialize() {}

    @FXML
    public void handleSendButton() {
        if(userInputField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("No message!");
            alert.setContentText("Please enter your message before hit \"Send\"");
            alert.show();
        }

        else {
            TCPClient.sendButton(userInputField.getText());
        }
    }

    @FXML
    public static void handleChatField(String message) {
        chatField.appendText(message + "\n");
    }


}
