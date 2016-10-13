import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Petru on 12-Oct-16.
 */
public class MainClient extends Application {
    Stage stage;
    Pane layout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        initIntroScene();
    }

    // method to initialize the root layout
    public void initChatScene() {
        try {
            // load root layout
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainClient.class.getResource("client.fxml"));
            layout = loader.load();
            ClientController clientController = loader.getController();

            // setScene, show
            Scene scene = new Scene(layout, 652, 394);
            stage.setScene(scene);
            stage.centerOnScreen();

            Thread thread = new Thread() {
                @Override
                public void run() {
                    TCPClient.initialize(clientController);
                }
            };
            thread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initIntroScene() {
        try {
            // load root layout
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainClient.class.getResource("intro.fxml"));
            layout = loader.load();

            // method to give the controller access to the main app
            IntroController introController = loader.getController();
            introController.setMainClient(this);

            // setScene, show
            Scene scene = new Scene(layout, 400, 365);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
