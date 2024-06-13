package Controllers.produit_back;

import MainJavaFx.MainApp;
import entities.Produit;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.ProduitService;
import utils.AlertUtils;
import utils.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.ResourceBundle;

public class ManageController implements Initializable {

    @FXML
    public TextField nompTF;
    @FXML
    public TextField descpTF;
    @FXML
    public TextField catgTF;
    @FXML
    public TextField prixTF;
    @FXML
    public ImageView imageIV;


    @FXML
    public Button btnAjout;
    @FXML
    public Text topText;

    Produit currentProduit;
    Path selectedImagePath;
    boolean imageEdited;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        currentProduit = ShowAllController.currentProduit;

        if (currentProduit != null) {
            topText.setText("Modifier produit");
            btnAjout.setText("Modifier");

            try {
                nompTF.setText(currentProduit.getNomp());
                descpTF.setText(currentProduit.getDescp());
                catgTF.setText(currentProduit.getCatg());
                prixTF.setText(String.valueOf(currentProduit.getPrix()));
                selectedImagePath = FileSystems.getDefault().getPath(currentProduit.getImage());
                if (selectedImagePath.toFile().exists()) {
                    imageIV.setImage(new Image(selectedImagePath.toUri().toString()));
                }


            } catch (NullPointerException ignored) {
                System.out.println("NullPointerException");
            }
        } else {
            topText.setText("Ajouter produit");
            btnAjout.setText("Ajouter");
        }
    }

    @FXML
    private void manage(ActionEvent ignored) {

        if (controleDeSaisie()) {
            createImageFile();
            String imagePath = selectedImagePath.toString();

            Produit produit = new Produit();
            produit.setNomp(nompTF.getText());
            produit.setDescp(descpTF.getText());
            produit.setCatg(catgTF.getText());
            produit.setPrix(Float.parseFloat(prixTF.getText()));
            produit.setImage(imagePath);


            if (currentProduit == null) {
                if (ProduitService.getInstance().add(produit)) {
                    AlertUtils.makeSuccessNotification("Produit ajouté avec succés");
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_DISPLAY_ALL_PRODUIT));
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
                    AlertUtils.makeError("Error");
                }
            } else {
                produit.setId(currentProduit.getId());
                if (ProduitService.getInstance().edit(produit)) {
                    AlertUtils.makeSuccessNotification("Produit modifié avec succés");
                    ShowAllController.currentProduit = null;
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_DISPLAY_ALL_PRODUIT));
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


        if (nompTF.getText().isEmpty()) {
            AlertUtils.makeInformation("nomp ne doit pas etre vide");
            return false;
        }


        if (descpTF.getText().isEmpty()) {
            AlertUtils.makeInformation("descp ne doit pas etre vide");
            return false;
        }


        if (catgTF.getText().isEmpty()) {
            AlertUtils.makeInformation("catg ne doit pas etre vide");
            return false;
        }


        if (prixTF.getText().isEmpty()) {
            AlertUtils.makeInformation("prix ne doit pas etre vide");
            return false;
        }


        try {
            Float.parseFloat(prixTF.getText());
        } catch (NumberFormatException ignored) {
            AlertUtils.makeInformation("prix doit etre un réel");
            return false;
        }
        if (selectedImagePath == null) {
            AlertUtils.makeInformation("Veuillez choisir une image");
            return false;
        }


        return true;
    }
}