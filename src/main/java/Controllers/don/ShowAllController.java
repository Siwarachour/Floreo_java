package Controllers.don;

import entities.Don;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.DonService;
import utils.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.*;



public class ShowAllController implements Initializable {

    public static Don currentDon;

    @FXML
    public Text topText;
    @FXML
    public Button addButton;
    @FXML
    public VBox mainVBox;



    List<Don> listDon;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listDon = DonService.getInstance().getAll();

        displayData();
    }

    void displayData() {
        mainVBox.getChildren().clear();

        Collections.reverse(listDon);

        if (!listDon.isEmpty()) {
            for (Don don : listDon) {

                mainVBox.getChildren().add(makeDonModel(don));

            }
        } else {
            StackPane stackPane = new StackPane();
            stackPane.setAlignment(Pos.CENTER);
            stackPane.setPrefHeight(200);
            stackPane.getChildren().add(new Text("Aucune donnée"));
            mainVBox.getChildren().add(stackPane);
        }
    }

    public Parent makeDonModel(
            Don don
    ) {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(Constants.FXML_FRONT_MODEL_DON)));

            HBox innerContainer = ((HBox) ((AnchorPane) ((AnchorPane) parent).getChildren().get(0)).getChildren().get(0));
            ((Text) innerContainer.lookup("#typeText")).setText("Type : " + don.getType());
            ((Text) innerContainer.lookup("#descriptionText")).setText("Description : " + don.getDescription());
            ((Text) innerContainer.lookup("#dateDonText")).setText("DateDon : " + don.getDateDon());

            ((Text) innerContainer.lookup("#userText")).setText("User : " + don.getUser().getName());

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return parent;
    }

    @FXML
    private void ajouterDon(ActionEvent ignored) {
        currentDon = null;
        try {
            // Charger le fichier FXML du nouvel interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_FRONT_MANAGE_DON));
            Parent root = loader.load();

            // Créer une nouvelle scène avec le nouvel interface
            Scene scene = new Scene(root);

            // Créer un nouveau stage pour afficher la nouvelle scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Titre de la nouvelle fenêtre");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
