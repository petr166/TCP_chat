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
    TextArea chatField;
    @FXML
    TextArea activeUsersField;
    @FXML
    TextField userInputField;
    @FXML
    Button sendButton;

    // initialize the controller class
    @FXML
    private void initialize() {}

    public ClientController() {
        chatField = new TextArea();
    }

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


    public void handleChatField(String message) {
        chatField.appendText(message + "\n");
    }


}
