package Controllers.UserController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import entities.User;
import services.UserService;

import java.io.IOException;
import java.util.List;

public class ListArticleController {
    @FXML
    private ListView<User> articleListView; // ListView pour afficher les détails d'articles

    private UserService userService;
    public Button btnAddArticle;
    private List<User> users;

    public ListArticleController() {
        userService = new UserService();
    }

    @FXML
    private void initialize() {
        // Récupérez la liste des utilisateurs à partir de votre service
        users = userService.show();

        // Ajoutez les utilisateurs à la ListView
        articleListView.getItems().addAll(users);

        // Définir la manière dont les utilisateurs sont affichés dans la ListView
        articleListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> listView) {
                return new ListCell<User>() {
                    @Override
                    protected void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);
                        if (empty || user == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText("Nom: " + user.getName() + "\nPrénom: " + user.getLastname() +
                                "\nEmail: " + user.getEmail() + "\nTéléphone: " + user.getNumber() +
                                "\nRole: " + user.getRoles() + "\nDate de naissance: " + user.getDatenaissance());

                            Button editButton = new Button("Editer");
                            Button deleteButton = new Button("Supprimer");

                            editButton.setOnAction(event -> {
                                openUpdateUserWindow(user.getId());
                            });

                            deleteButton.setOnAction(event -> {
                                userService.delete(user);
                                users.remove(user);
                                articleListView.getItems().setAll(users);
                            });

                            HBox buttonsBox = new HBox(editButton, deleteButton);
                            setGraphic(buttonsBox);
                        }
                    }
                };
            }
        });

        // Si vous souhaitez gérer les sélections d'utilisateurs
        articleListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Gérer la sélection de l'utilisateur ici
            if (newValue != null) {
                System.out.println("Utilisateur sélectionné : " + newValue.getName());
                System.out.println("Prénom : " + newValue.getLastname());
                System.out.println("Email : " + newValue.getEmail());
                System.out.println("Téléphone : " + newValue.getNumber());
                System.out.println("Role : " + newValue.getRoles());
                System.out.println("Date de naissance : " + newValue.getDatenaissance());
            }
        });
        articleListView.setOnMouseClicked(event -> {
            // Obtenez l'utilisateur sélectionné
            User selectedUser = articleListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                // Affichez l'interface "ShowUser.fxml" avec les détails de l'utilisateur sélectionné
                userService.show();
            }
        });
    }

    private void openUpdateUserWindow(int userId) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateUser.fxml"));
        try {
            Parent root = loader.load();
            ModifierUser updateUserController = loader.getController();
            updateUserController.setUserData(String.valueOf(userId));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void navigateToAddArticle(MouseEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddUser.fxml"));
        Parent root;
        try {
            root = loader.load();
            Stage stage = (Stage) btnAddArticle.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
