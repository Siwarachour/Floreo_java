package Controllers.dechets_back;

import MainJavaFx.MainApp;
import entities.Dechets;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import services.DechetsService;
import utils.AlertUtils;
import utils.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ManageController implements Initializable {

    @FXML
    public TextField typeTF;
    @FXML
    public DatePicker dateEntreDP;
    @FXML
    public TextField descriptionTF;
    @FXML
    public TextField quantiteTF;
    @FXML
    public ImageView imageIV;
    @FXML
    private Pane content_area;
    @FXML
    public Button btnAjout;
    @FXML
    public Text topText;

    Dechets currentDechets;
    Path selectedImagePath;
    boolean imageEdited;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        currentDechets = ShowAllController.currentDechets;

        if (currentDechets != null) {
            topText.setText("Modifier dechets");
            btnAjout.setText("Modifier");

            try {
                typeTF.setText(currentDechets.getType());
                dateEntreDP.setValue(currentDechets.getDateEntre());
                descriptionTF.setText(currentDechets.getDescription());
                quantiteTF.setText(String.valueOf(currentDechets.getQuantite()));
                selectedImagePath = FileSystems.getDefault().getPath(currentDechets.getImage());
                if (selectedImagePath.toFile().exists()) {
                    imageIV.setImage(new Image(selectedImagePath.toUri().toString()));
                }

            } catch (NullPointerException ignored) {
                System.out.println("NullPointerException");
            }
        } else {
            topText.setText("Ajouter dechets");
            btnAjout.setText("Ajouter");
            dateEntreDP.setValue(LocalDate.now());

        }
    }

    @FXML
    private void manage(ActionEvent ignored) throws IOException {

        if (controleDeSaisie()) {
            createImageFile();
            String imagePath = selectedImagePath.toString();

            Dechets dechets = new Dechets();
            dechets.setType(typeTF.getText());
            dechets.setDateEntre(dateEntreDP.getValue());
            dechets.setDescription(descriptionTF.getText());
            dechets.setQuantite(Integer.parseInt(quantiteTF.getText()));
            dechets.setImage(imagePath);

            if (currentDechets == null) {
                if (DechetsService.getInstance().add(dechets)) {
                    AlertUtils.makeSuccessNotification("Dechets ajouté avec succés");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_DISPLAY_ALL_DECHETS));
                    Parent userListParent = loader.load();

                    content_area.getChildren().clear();
                   content_area.getChildren().add(userListParent);
                } else {
                    AlertUtils.makeError("Error");
                }
            } else {
                dechets.setId(currentDechets.getId());
                if (DechetsService.getInstance().edit(dechets)) {
                    AlertUtils.makeSuccessNotification("Dechets modifié avec succés");
                    ShowAllController.currentDechets = null;
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_DISPLAY_ALL_DECHETS));
                    Parent userListParent = loader.load();

                    content_area.getChildren().clear();
                    content_area.getChildren().add(userListParent);
                } else {
                    AlertUtils.makeError("Error");
                }
            }

            if (selectedImagePath != null) {
                createImageFile();
            }
        }
    }

    @FXML
    public void chooseImage(ActionEvent ignored) {

        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(MainApp.mainStage);
        if (file != null) {
            selectedImagePath = Paths.get(file.getPath());
            imageIV.setImage(new Image(file.toURI().toString()));
        }
    }

    public void createImageFile() {
        try {
            Path newPath = FileSystems.getDefault().getPath("src/main/resources/com/example/flo/images/uploads/" + selectedImagePath.getFileName());
            Files.copy(selectedImagePath, newPath, StandardCopyOption.REPLACE_EXISTING);
            selectedImagePath = newPath;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean controleDeSaisie() {
        String type = typeTF.getText().trim();
        String description = descriptionTF.getText().trim();

        // Check if typeTF is at least 4 characters and doesn't contain numbers
        if (type.isEmpty() || type.length() < 4 || type.matches(".*\\d.*")) {
            AlertUtils.makeInformation("Le type doit contenir au moins 4 caractères et ne pas contenir de chiffres.");
            return false;
        }

        // Check if descriptionTF is at least 4 characters and doesn't contain numbers
        if (description.isEmpty() || description.length() < 4 || description.matches(".*\\d.*")) {
            AlertUtils.makeInformation("La description doit contenir au moins 4 caractères et ne pas contenir de chiffres.");
            return false;
        }

        LocalDate selectedDate = dateEntreDP.getValue();
        LocalDate currentDate = LocalDate.now();

        // Check if selectedDate is null or in the past
        if (selectedDate == null || selectedDate.isBefore(currentDate)) {
            AlertUtils.makeInformation("Veuillez choisir une date future pour dateEntre.");
            return false;
        }

        if (quantiteTF.getText().isEmpty()) {
            AlertUtils.makeInformation("La quantité ne doit pas être vide.");
            return false;
        }

        try {
            Integer.parseInt(quantiteTF.getText());
        } catch (NumberFormatException ignored) {
            AlertUtils.makeInformation("La quantité doit être un nombre entier.");
            return false;
        }

        if (selectedImagePath == null) {
            AlertUtils.makeInformation("Veuillez choisir une image.");
            return false;
        }

        return true;
    }

}
