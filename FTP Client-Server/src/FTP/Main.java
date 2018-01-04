package FTP;

import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.*;

public class Main extends Application {
    Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        showMainWindow();
    }

    public static void main(String[] args) {
        launch(args);
    }
    public void showMainWindow() throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("MainWindow.fxml"));
        Parent root = loader.load();

        //Loading controller
        MainWindowController controller = loader.getController();
        controller.init();
        controller.setMain(this);

        //Set stage
        stage.setTitle("FTP Client Software");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
