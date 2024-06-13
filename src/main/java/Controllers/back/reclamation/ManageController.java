package Controllers.back.reclamation;


import entities.Reclamation;
import entities.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import services.ReclamationService;
import utils.AlertUtils;
import utils.BadWords;
import utils.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ManageController implements Initializable {
    @FXML
    private Pane content_area;
    @FXML
    public TextField typeTF;
    @FXML
    public TextField descriptionTF;
    @FXML
    public Button btnAjout;
    @FXML
    public Text topText;
    public static User user;

    Reclamation currentReclamation;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println(user);

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
    private void manage(ActionEvent ignored) throws IOException {

        if (controleDeSaisie()) {

            Reclamation reclamation = new Reclamation();
            reclamation.setType(typeTF.getText());
            reclamation.setDescription(descriptionTF.getText());
            reclamation.setDatemodif(LocalDate.now());


            if (currentReclamation == null) {
                reclamation.setDateajout(LocalDate.now());

                if (ReclamationService.getInstance().add(reclamation)) {
                    AlertUtils.successNotification("Reclamation ajouté avec succés");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_DISPLAY_ALL_RECLAMATION));
                    Parent displayresDParent = loader.load();

                    // Remplacer le contenu actuel par le contenu du fichier FXML chargé
                    content_area.getChildren().clear();
                    content_area.getChildren().add(displayresDParent);
                } else {
                    AlertUtils.errorNotification("Error");
                }
            } else {
                reclamation.setId(currentReclamation.getId());
                if (ReclamationService.getInstance().edit(reclamation)) {
                    AlertUtils.successNotification("Reclamation modifié avec succés");
                    ShowAllController.currentReclamation = null;
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_DISPLAY_ALL_RECLAMATION));
                    Parent displayresDParent = loader.load();

                    // Remplacer le contenu actuel par le contenu du fichier FXML chargé
                    content_area.getChildren().clear();
                    content_area.getChildren().add(displayresDParent);                } else {
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
            AlertUtils.informationNotification("description ne doit pas contenir des mots interdits");
            return false;
        }

        return true;
    }
}
