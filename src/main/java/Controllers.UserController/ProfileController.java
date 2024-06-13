package Controllers.UserController;

import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import services.UserService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static Controllers.UserController.LoginController.emailc;

public class ProfileController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView imagefield;

    @FXML
    private Text nameField;

    @FXML
    private Text lastnamefield;

    @FXML
    private Text roleField;

    @FXML
    private Text datenaissancefield;

    @FXML
    private Text numberfield;

    @FXML
    private Text emailfield;

    private String authenticatedEmail;
    private User userData; // Store user data
    private WebEngine webEngine;
    @FXML
    private WebView webView;

     @FXML
     private void initialize()
     {
         setAuthenticatedEmail();


     }
    public void setUserData(User user) {
        this.userData = user;
        // Populate fields with user data
        nameField.setText(userData.getName());
        emailfield.setText(userData.getEmail());

        // Set other fields...
    }
    public void setAuthenticatedEmail() {
        this.authenticatedEmail = HomeController.authenticatedEmail;
        initializeFields();
    }

    @FXML
    void initializeFields() {
        // Remplir les champs avec les informations de l'utilisateur authentifié
        User authenticatedUser = getAuthenticatedUser(emailc);
        if (authenticatedUser != null) {
            nameField.setText(authenticatedUser.getName());
            lastnamefield.setText(authenticatedUser.getLastname());
            roleField.setText(getUserRoleText(authenticatedUser.getRoles()));

            // Afficher la date de naissance si elle n'est pas nulle
            if (authenticatedUser.getDatenaissance() != null) {
                String dateNaissance = authenticatedUser.getDatenaissance().toString();
                datenaissancefield.setText(dateNaissance);
            }

            // Afficher le numéro si il est différent de zéro
            if (authenticatedUser.getNumber() != 0) {
                String numero = String.valueOf(authenticatedUser.getNumber());
                numberfield.setText(numero);
            }

            emailfield.setText(authenticatedUser.getEmail());

            // Récupérer le chemin de l'image de profil de l'utilisateur
            String imagePath =authenticatedUser.getImage();
            System.out.println(imagePath);

            // Vérifier si le chemin de l'image est valide
            if (imagePath != null && !imagePath.isEmpty()) {
                // Créer une instance de File à partir du chemin de l'image
                File file = new File(imagePath);

                // Vérifier si le fichier de l'image existe
                if (file.exists()) {
                    // Créer une instance de Image à partir du fichier de l'image
                    Image image = new Image(file.toURI().toString());

                    // Afficher l'image dans l'imageView
                    imagefield.setImage(image);
                } else {
                    System.out.println("L'image de profil n'existe pas : " + imagePath);
                }
            } else {
                System.out.println("Chemin de l'image de profil non spécifié.");
            }
        }
    }



    private String getUserRoleText(String roles) {
        if (roles.contains("ROLE_CLIENT")) {
            return "Client";
        } else if (roles.contains("ROLE_ADMIN")) {
            return "Admin";
        } else if (roles.contains("ROLE_FOURNISSEUR")) {
            return "Fournisseur";
        } else {
            return "Inconnu";
        }
    }

    // Méthode de récupération de l'utilisateur authentifié (à adapter selon votre logique d'authentification)
    private User getAuthenticatedUser(String email) {
        UserService userService = new UserService();
        return userService.getUserByEmail(email);
    }

    @FXML
    void logoutclicked(MouseEvent mouseEvent) {
        // Vérifier si l'utilisateur est connecté
        if (emailc == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Déconnexion échouée", "Vous n'êtes pas connecté.");
            return; // Arrêter l'exécution de la méthode si l'utilisateur n'est pas connecté
        }

        // Afficher une boîte de dialogue de confirmation avant de se déconnecter
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir vous déconnecter ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // L'utilisateur a confirmé la déconnexion
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/login.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Login");
                stage.show();

                // Fermer la fenêtre actuelle
                Stage currentStage = (Stage) emailfield.getScene().getWindow();
                currentStage.close();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Déconnexion échouée", "Impossible d'ouvrir la fenêtre de connexion.");
            }
        }

}

    private void showAlert(Alert.AlertType alertType, String title, String header, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void retourC(MouseEvent event) {
        // Vérifier si la navigation arrière est possible
        if (webEngine.getHistory().getCurrentIndex() > 0) {
            webEngine.executeScript("history.back()");
        } else {
            // Vous pouvez afficher un message indiquant que vous êtes déjà sur la première page
            System.out.println("Impossible de revenir en arrière : Vous êtes déjà sur la première page.");
        }
    }




}
