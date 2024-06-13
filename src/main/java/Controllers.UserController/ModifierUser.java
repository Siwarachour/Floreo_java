package Controllers.UserController;

import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import services.UserService;

public class ModifierUser {



    @FXML
    private PasswordField PasswordField;

    @FXML
    private PasswordField ResetField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstnameField;

    @FXML
    private ImageView imageField;

    @FXML
    private TextField lastnameField;

    @FXML
    private TextField numberField;
    @FXML
    private ChoiceBox<String> rolecombo;
private  User user;

    @FXML
    void initialize()
    {




    }
    @FXML
    void ModifierUser(ActionEvent event) {
        if (user != null) {
            String newRole = "[\"" + rolecombo.getValue() + "\"]"; // Déterminez le nouveau rôle selon vos besoins
            user.setRoles(newRole); // Mettez à jour le rôle de l'utilisateur

            // Appelez la méthode updateRole du service utilisateur pour mettre à jour le rôle dans la base de données
            UserService userService = new UserService(); // Initialisez votre service utilisateur
            userService.updateRole(user); // Appelez la méthode updateRole avec l'utilisateur modifié

            // Afficher une alerte pour informer l'utilisateur que le rôle a été mis à jour avec succès
            showAlertAndCloseWindow(Alert.AlertType.INFORMATION, "Success", "Role Updated", "The role has been updated successfully.", event);

            System.out.println("Role updated successfully.");
        } else {
            // Afficher une alerte si l'utilisateur est null
            showAlert(Alert.AlertType.ERROR, "Error", "User is Null", "Cannot update role because the user is null.");
            System.out.println("User is null. Cannot update role.");
        }
    }

    // Méthode pour afficher une alerte et fermer la fenêtre parente
    private void showAlertAndCloseWindow(Alert.AlertType alertType, String title, String header, String content, ActionEvent event) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Fermer la fenêtre parente de l'événement
                Node source = (Node) event.getSource();
                Stage stage = (Stage) source.getScene().getWindow();
                stage.close();
            }
        });
    }


    // Méthode pour afficher une alerte
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }



    @FXML
    void UploadImage(ActionEvent event) {

    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            if (emailField != null) {
                emailField.setText(user.getEmail());
            }
            if (firstnameField != null) {
                firstnameField.setText(user.getName());
            }
            if (lastnameField != null) {
                lastnameField.setText(user.getLastname());
            }
            if (numberField != null) {
                numberField.setText(String.valueOf(user.getNumber()));

            }
            if (numberField != null) {
                numberField.setText(String.valueOf(user.getNumber()));

            }
            // Assuming setImage method is available for your ImageView


        }
    }

    public void setParentController(listUserController listUserController) {
    }

    public void setUserData(String s) {
    }
}
