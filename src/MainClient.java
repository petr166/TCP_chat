import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Petru on 12-Oct-16.
 */
public class MainClient extends Application {
    //fields
    private Stage stage;
    private Pane layout;

    //MAIN
    public static void main(String[] args) {
        launch(args);
    }

    //getter
    public Stage getStage() {
        return stage;
    }


    //START
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        initIntroScene();
    }

    // method to show the chat scene
    public void initChatScene() {
        try {
            // load scene layout
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainClient.class.getResource("client.fxml"));
            layout = loader.load();

            //set up the client controller
            ClientController clientController = loader.getController();
            clientController.setMainClient(this);
            clientController.setExit();

            // setScene, show
            Scene scene = new Scene(layout, 655, 375);
            stage.setScene(scene);

            stage.setTitle("Le Chat - chattin");

            stage.centerOnScreen();

            //start the client backend thread
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


    // method to show the chat scene
    public void initIntroScene() {
        try {
            // load scene layout
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainClient.class.getResource("intro.fxml"));
            layout = loader.load();

            //set up the intro controller
            IntroController introController = loader.getController();
            introController.setMainClient(this);

            // setScene, show
            Scene scene = new Scene(layout, 400, 350);
            stage.setScene(scene);

            stage.getIcons().add(new Image("icon.png"));
            stage.setTitle("Le Chat - Login");

            stage.show();
            stage.centerOnScreen();
            stage.setResizable(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
