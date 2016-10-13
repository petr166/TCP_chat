import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Created by Petru on 13-Oct-16.
 */
public class IntroController {

    @FXML
    TextField serverAddressField;
    @FXML
    TextField serverPortField;
    @FXML
    TextField userNameField;
    @FXML
    Button connectButton;

    MainClient mainClient;

    public IntroController() {
        serverAddressField = new TextField();
        serverPortField = new TextField();
    }

    @FXML
    public void initialize() {
    }

    public void setMainClient(MainClient mainClient) {
        this.mainClient = mainClient;
    }

    public void handleConnectButton() {
        if (serverPortField.getText().isEmpty() || serverAddressField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Empty address!");
            alert.setContentText("Please enter the server details before hit 'Connect'!");
            alert.show();
        } else {
            TCPClient.connectToServer(serverAddressField.getText(), Integer.parseInt(serverPortField.getText()), userNameField.getText(), this);
            mainClient.initChatScene();

        }
    }

    public void showWarningAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }
}
