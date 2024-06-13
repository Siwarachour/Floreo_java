package Controllers.UserController;

import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import services.UserService;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

public class AjouterUser {
    @FXML
    private ImageView imageField;

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private TextField firstnameField;

    @FXML
    private TextField lastnameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField numberField;

    @FXML
    private PasswordField PasswordField;

    @FXML
    private PasswordField ResetField;

    @FXML
    private DatePicker datePicker;
    @FXML
    private ToggleButton showPasswordToggle;

    private UserService userService = new UserService();
    private listUserController listUserController;

    @FXML
    private void initialize() {
        // Ajoutez des éléments à la ChoiceBox
        choiceBox.getItems().addAll("ROLE_CLIENT", "ROLE_FOURNISSEUR");
    }

    @FXML
    private void onImageClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            imageField.setImage(image);
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucune image sélectionnée", null, "Aucune image sélectionnée.");
        }
    }

    @FXML
    void UploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            String imagePath = selectedFile.getAbsolutePath();
            imageField.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    @FXML
    void AjoutUser(ActionEvent actionEvent) {
        String role = choiceBox.getValue();
        if (role != null && !role.isEmpty()) {
            try {
                String name = firstnameField.getText().trim();
                String lastname = lastnameField.getText().trim();
                String email = emailField.getText().trim();
                String password = PasswordField.getText().trim();
                String resetPassword = ResetField.getText().trim();
                int number = Integer.parseInt(numberField.getText().trim());



                if (name.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty() || resetPassword.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ajout", null, "Veuillez remplir tous les champs.");
                    return;
                }

                if (!password.equals(resetPassword)) {
                    showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ajout", null, "Les champs de mot de passe ne correspondent pas.");
                    return;
                }

                Date dateNaissance = null;
                if (datePicker.getValue() != null) {
                    dateNaissance = Date.valueOf(datePicker.getValue());
                }

                // Vérification de la longueur du numéro
                String numberString = Integer.toString(number);
                if (numberString.length() != 8) {
                    showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ajout", null, "Le numéro doit contenir exactement 8 chiffres.");
                    return;
                }

                // Vérification du premier chiffre
                char firstDigit = numberString.charAt(0);
                if (firstDigit != '2' && firstDigit != '7' && firstDigit != '4' && firstDigit != '9') {
                    showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ajout", null, "Le numéro doit commencer par 2, 7, 4 ou 9.");
                    return;
                }




                User user = new User(name, lastname,  password, email,role, "", number, false, dateNaissance);
                userService.add(user);

                showAlert(Alert.AlertType.INFORMATION, "Ajout réussi", "Utilisateur ajouté avec succès", null);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ajout", "Une erreur s'est produite lors de l'ajout de l'utilisateur", e.getMessage());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ajout", "Veuillez entrer un numéro valide", null);
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucun rôle sélectionné", "Veuillez sélectionner un rôle pour l'utilisateur", null);
        }
    }
    private void showAlert(Alert.AlertType alertType, String title, String header, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        // Ajouter un gestionnaire d'événements pour le bouton "OK"
        alert.setOnCloseRequest(event -> {
            // Récupérer la scène actuelle à partir de n'importe quel nœud de la fenêtre
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            // Fermer la fenêtre
            stage.close();

            // Réinitialiser les champs du formulaire
            firstnameField.clear();
            lastnameField.clear();
            emailField.clear();
            PasswordField.clear();
            ResetField.clear();
            numberField.clear();
            datePicker.setValue(null);
        });

        alert.show(); // Utiliser show() au lieu de showAndWait()
    }

    public void UpdateUser(javafx.scene.input.MouseEvent mouseEvent) {
    }



    public void Backtolist(MouseEvent mouseEvent) {
        // Récupérer la scène actuelle
        Stage stage = (Stage) imageField.getScene().getWindow();
        // Fermer la fenêtre actuelle
        stage.close();
        // Rafraîchir la liste des utilisateurs dans la fenêtre précédente (s'il y a une méthode pour cela)
        // Par exemple, vous pouvez appeler une méthode de rafraîchissement sur le contrôleur de la fenêtre précédente
    }










        // Méthode pour injecter la référence du contrôleur de la fenêtre précédente
        public void setListUserController(listUserController listUserController) {
            this.listUserController = listUserController;
        }

    @FXML
    public void toggleShowPassword(ActionEvent actionEvent) {  if (showPasswordToggle.isSelected()) {
        // Si le bouton est enfoncé, montrer le texte du champ de mot de passe
        PasswordField.setVisible(true);
    } else {
        // Sinon, masquer le texte du champ de mot de passe
        PasswordField.setVisible(false);
    }}
    }


