import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Created by Petru on 13-Oct-16.
 */
public class IntroController {

    @FXML
    private TextField serverAddressField;
    @FXML
    private TextField serverPortField;
    @FXML
    private TextField userNameField;
    @FXML
    private Button connectButton;
    @FXML
    private ImageView logo;

    private MainClient mainClient;

    @FXML
    private void initialize() {
        logo.setImage(new Image("Le_Chat_logo.png"));
        serverAddressField.setText("localhost");
        serverPortField.setText("7777");
        userNameField.setPromptText("user name");
        connectButton.setDefaultButton(true);
    }

    public void setMainClient(MainClient mainClient) {
        this.mainClient = mainClient;
    }

    @FXML
    private void handleConnectButton() {
        if (serverPortField.getText().isEmpty() || serverAddressField.getText().isEmpty() || userNameField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Empty address!");
            alert.setContentText("Please enter the server details and user name before hit 'Login'!");
            alert.show();

        } else {
            String serverAddress = serverAddressField.getText();
            int serverPort = Integer.parseInt(serverPortField.getText());
            String userName = userNameField.getText();

            TCPClient.connectToServer(serverAddress, serverPort, userName, this);
            mainClient.initChatScene();
        }
    }

    public void showWarningAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(mainClient.getStage());
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }
}
