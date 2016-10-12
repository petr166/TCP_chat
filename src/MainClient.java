import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Petru on 12-Oct-16.
 */
public class MainClient extends Application{
    Stage stage;
    AnchorPane layout;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        initRootLayout();
        System.out.println(Thread.currentThread().getName());
        Thread thread = new Thread(){
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
                TCPClient.initialize();
            }
        };
        thread.start();
    }

    // method to initialize the root layout
    public void initRootLayout() {
        try {
            // load root layout
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainClient.class.getResource("GUI.fxml"));
            layout = loader.load();

            // setScene, show
            Scene scene = new Scene(layout, 652, 394);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
