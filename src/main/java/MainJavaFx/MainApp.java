package MainJavaFx;

import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import utils.BadWords;
import utils.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApp extends Application {

    public static Stage mainStage;
    static AnchorPane staticContent;

    private static MainApp instance;

    public static void main(String[] args) {
        launch(args);
    }

    public static MainApp getInstance() {
        if (instance == null) {
            instance = new MainApp();
        }
        return instance;
    }


    @Override
    public void start(Stage primaryStage) {
        System.out.println("Loading bad words ..");
        BadWords.loadConfigs();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/UserInterface/Login.fxml"));
            Scene scene = new Scene(root);
            // Stage.setFullScreen(false);
            // Stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/logo/favicon.png")));
            // Stage.setTitle("RadioHub");
            // Stage.setResizable(false);

            primaryStage.setScene(scene);

            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void loadLogin() {
        loadScene(
                Constants.FXML_LOGIN,
                "Connexion",
                550,
                250
        );
    }


    public void loadInterface(String location) {
        staticContent.getChildren().clear();
        if (getClass().getResource(location) == null) {
            System.out.println("Could not load FXML check the path");
        } else {
            try {
                Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(location)));
                AnchorPane.setTopAnchor(parent, 0.0);
                AnchorPane.setBottomAnchor(parent, 0.0);
                AnchorPane.setRightAnchor(parent, 0.0);
                AnchorPane.setLeftAnchor(parent, 0.0);
                staticContent.getChildren().add(parent);
            } catch (IOException e) {
                System.out.println("Could not load FXML : " + e.getMessage() + " check your controller");
                e.printStackTrace();
            }
        }
    }



    public void logout() {

        System.out.println("Deconnexion ..");
        loadLogin();
    }

    private void loadScene(String fxmlLink, String title, int width, int height) {
        try {
            Stage primaryStage = mainStage;
            primaryStage.close();

            Scene scene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlLink))));
            scene.setFill(Color.TRANSPARENT);

            //primaryStage.getIcons().add(new Image("app/images/app-icon.png"));
            primaryStage.setTitle(title);
            primaryStage.setWidth(width);
            primaryStage.setHeight(height);
            primaryStage.setMinWidth(width);
            primaryStage.setMinHeight(height);
            primaryStage.setScene(scene);
            primaryStage.setX((Screen.getPrimary().getBounds().getWidth() / 2) - (width / 2.0));
            primaryStage.setY((Screen.getPrimary().getBounds().getHeight() / 2) - (height / 2.0));

            primaryStage.show();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
