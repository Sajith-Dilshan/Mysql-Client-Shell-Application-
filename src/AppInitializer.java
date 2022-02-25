import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        AnchorPane load = FXMLLoader.load(getClass().getResource("/view/MysqlClientLogin.fxml"));
        Scene scene = new Scene(load);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Mysql Client Shell Application");
        primaryStage.show();

    }
}
