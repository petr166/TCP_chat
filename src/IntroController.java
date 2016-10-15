import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.InetAddress;

/**
 * Created by Petru on 13-Oct-16.
 */
public class IntroController {
    //fields
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

    private MainClient mainClient; //reference to the main client


    //initialize the scene
    @FXML
    private void initialize() {
        //set up scene objects
        logo.setImage(new Image("Le_Chat_logo.png"));
        serverAddressField.setPromptText("server address");
        serverAddressField.setText("localhost");
        serverPortField.setPromptText("server port");
        serverPortField.setText("7777");
        userNameField.setPromptText("user name");

        connectButton.setDefaultButton(true);

        //console print
        try {
            System.out.printf("\nclient running(%s)...\n\n", InetAddress.getLocalHost());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("-->unable to get the local host.");
            System.out.println("------------------------------------------------->");
        }
    }


    //method to initialize the reference to the main client
    public void setMainClient(MainClient mainClient) {
        this.mainClient = mainClient;
    }


    //'Login' button action handler
    @FXML
    private void handleConnectButton() {
        //check if the fields are filled
        if (serverPortField.getText().isEmpty() || serverAddressField.getText().isEmpty() || userNameField.getText().isEmpty()) {
            //if not show the alert
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Empty input field!");
            alert.setContentText("Please enter the server details and user name before hit 'Login'!");
            alert.showAndWait();

        } else { //fields completed, proceed to server connection attempt
            //get the conncetion details from the fields
            String serverAddress = serverAddressField.getText();
            int serverPort = Integer.parseInt(serverPortField.getText());
            String userName = userNameField.getText();

            //try connect
            boolean connectedSuccessful = TCPClient.connectToServer(serverAddress, serverPort, userName, this);

            //if connection is accepted show the chat scene
            if (connectedSuccessful) mainClient.initChatScene();
        }
    }


    //method to show alert in case of wrong inputs
    public void showWarningAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
