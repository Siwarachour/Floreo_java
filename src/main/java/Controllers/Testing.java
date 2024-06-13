package Controllers;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.ServiceEvenement;
import javafx.fxml.FXMLLoader;
public class Testing extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/eventfront/market.fxml"));
        primaryStage.setTitle("Event Market");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();



    }

    public static void main(String[] args) {
        launch(args);
    }
}


