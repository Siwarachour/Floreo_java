package Controllers.UserController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.UserService;

import java.io.IOException;

public class HomeController {


    @FXML
    private Button EmailField;

    // Méthode pour définir l'e-mail authentifié
    public void setAuthenticatedEmail(String email) {
        if (LoginController.emailc != null) {
            EmailField.setText(email);
        }
    }

    public static  String authenticatedEmail; // Store authenticated email


    @FXML
    public void Profileclicked(MouseEvent mouseEvent) {
        openNewStage("/UserInterface/profile.fxml", "Profil");
    }

    @FXML
    public void Donclicked(MouseEvent mouseEvent) {
        openNewStage("/don/Manage.fxml", "Liste des dons");
    }
    @FXML
    public void Reponseclicked(MouseEvent event) {openNewStage("/back/reponse/ShowAll.fxml", "Liste des dons");
    }
    @FXML
    public void Reclamationclicked(MouseEvent mouseEvent) {
        openNewStage("/front/reclamation/Manage.fxml", "Liste des réclamations");
    }

    @FXML
    public void Produitclicked(MouseEvent mouseEvent) {
        openNewStage("/produit/ShowAll.fxml", "Liste des produits");
    }

    @FXML
    public void Eventlicked(MouseEvent mouseEvent) {
        openNewStage("/eventfront/market.fxml", "Liste des événements");
    }

    @FXML
    public void Dechetclicked(MouseEvent mouseEvent) {
        openNewStage("/dechets/ShowAll.fxml", "Liste des déchets");
    }

    @FXML
    public void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) EmailField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Déconnexion échouée", "Impossible d'ouvrir la fenêtre de connexion.");
        }
    }

    private void openNewStage(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Chargement du contenu échoué", "Impossible de charger le contenu dans une nouvelle fenêtre.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
