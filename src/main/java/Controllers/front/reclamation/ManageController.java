package Controllers.front.reclamation;


import Controllers.UserController.UserSession;
import entities.Reclamation;
import entities.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.ReclamationService;
import services.UserService;
import utils.AlertUtils;
import utils.BadWords;
import utils.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ManageController implements Initializable {

    @FXML
    public TextField typeTF;
    @FXML
    public TextField descriptionTF;
    @FXML
    public Button btnAjout;
    @FXML
    public Text topText;

    Reclamation currentReclamation;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentReclamation = ShowAllController.currentReclamation;

        if (currentReclamation != null) {
            topText.setText("Modifier reclamation");
            btnAjout.setText("Modifier");

            try {
                typeTF.setText(currentReclamation.getType());
                descriptionTF.setText(currentReclamation.getDescription());
            } catch (NullPointerException ignored) {
                System.out.println("NullPointerException");
            }
        } else {
            topText.setText("Ajouter reclamation");
            btnAjout.setText("Ajouter");
        }
    }

    @FXML
    private void manage(ActionEvent ignored) {

        if (controleDeSaisie()) {

            Reclamation reclamation = new Reclamation();
            reclamation.setType(typeTF.getText());
            reclamation.setDescription(descriptionTF.getText());
            reclamation.setDatemodif(LocalDate.now());
            User user= new User();
            String email=UserSession.getInstance().getAuthenticatedEmail();
            UserService userService = new UserService();
            user=userService.getUserByEmail(email);
            reclamation.setUser(user);
            if (currentReclamation == null) {
                reclamation.setDateajout(LocalDate.now());

                if (ReclamationService.getInstance().add(reclamation)) {
                    AlertUtils.successNotification("Reclamation ajouté avec succés");
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_FRONT_DISPLAY_ALL_RECLAMATION));
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
                } else {
                    AlertUtils.errorNotification("Error");
                }
            } else {
                reclamation.setId(currentReclamation.getId());
                if (ReclamationService.getInstance().edit(reclamation)) {
                    AlertUtils.successNotification("Reclamation modifié avec succés");
                    ShowAllController.currentReclamation = null;
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_FRONT_DISPLAY_ALL_RECLAMATION));
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
                } else {
                    AlertUtils.errorNotification("Error");
                }
            }

        }
    }


    private boolean controleDeSaisie() {

        if (typeTF.getText().isEmpty()) {
            AlertUtils.informationNotification("type ne doit pas etre vide");
            return false;
        }

        if (BadWords.filterText(typeTF.getText())) {
            AlertUtils.informationNotification("type ne doit pas contenir des mots interdits");
            return false;
        }

        if (descriptionTF.getText().isEmpty()) {
            AlertUtils.informationNotification("description ne doit pas etre vide");
            return false;
        }

        if (BadWords.filterText(descriptionTF.getText())) {
            AlertUtils.informationNotification("description contient des mots interdits");
            return false;
        }

        return true;
    }
}
