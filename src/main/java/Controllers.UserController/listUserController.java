package Controllers.UserController;

import com.sun.javafx.charts.Legend;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import entities.User;
import services.UserService;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class listUserController {
    @FXML
    private ListView<User> userListListView; // ListView pour afficher les détails d'utilisateurs
    @FXML
    private TableView<User> userTableView;

    @FXML
    private Button btnAddUser;
    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> statusInput; // ComboBox pour sélectionner le critère de tri

    private UserService userService;
    private List<User> users;

    public listUserController() {
        userService = new UserService();
    }






    @FXML
    private void initialize() {
        users = userService.show();
        userListListView.getItems().addAll(users);
        statusInput.getItems().addAll("Name", "Email", "Role","Date");

        userListListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
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
                            String role = "";
                            if (user.getRoles().contains("ROLE_CLIENT")) {
                                role = "Client";
                            } else if (user.getRoles().contains("ROLE_ADMIN")) {
                                role = "Admin";
                            } else if (user.getRoles().contains("ROLE_FOURNISSEUR")) {
                                role = "Fournisseur";
                            } else {
                                role = "Inconnu";
                            }


// VBox pour les détails de l'utilisateur
                            HBox hBox = new HBox();
                            //     hBox.setStyle("-fx-background-color: #4CAF50;"); // Définir la couleur de fond
                            hBox.setSpacing(50);
                            hBox.setAlignment(Pos.CENTER);
                            HBox.setMargin(hBox, new Insets(50, 0, 50, 0));

// VBox pour les détails de l'utilisateur
                            VBox vBox = new VBox();
                            // vBox.setStyle("-fx-background-color: #4CAF50;");
                            vBox.setSpacing(15); // Ajouter de l'espace entre les éléments
                            vBox.getChildren().addAll(
                                    // Détails de l'utilisateur
                                    new Label("Nom: " + user.getName()),
                                    new Label("Prénom: " + user.getLastname()),
                                    new Label("Téléphone: " + user.getNumber()),
                                    new Label("Email: " + user.getEmail()),
                                    new Label("Role: " + role),
                                    new Label("Date de naissance: " + (user.getDatenaissance() != null ? new SimpleDateFormat("dd/MM/yyyy").format(user.getDatenaissance()) : "Inconnue"))
                            );

// ImageView pour afficher l'image
                            ImageView imageView = new ImageView(new Image(new File(user.getImage()).toURI().toString()));
                            imageView.setFitWidth(100);
                            imageView.setFitHeight(100);

// Ajouter de l'espace autour de l'ImageView

// Ajouter la VBox et l'ImageView dans le HBox
                            hBox.getChildren().addAll(vBox, imageView);





                            Button editButton = new Button("  Edit   ");
                            Button deleteButton = new Button("Delete");
                            Button unbanUser = new Button("Unban");


                            editButton.setOnAction(event -> {
                                openUpdateUserWindow(user);
                            });

                            deleteButton.setOnAction(event -> {
                                userService.delete(user);
                                userListListView.getItems().remove(user);
                                refreshUserList();
                            });
                            unbanUser.setOnAction(event -> {
                                try {
                                    userService.unbanUser(user);
                                    showAlert(Alert.AlertType.INFORMATION, "User Unbanned", "User has been successfully unbanned.", "");
                                } catch (SQLException ex) {
                                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to unban user.", ex.getMessage());
                                }
                            });


                            // Créer un HBox pour aligner les boutons à droite
                            HBox hbox = new HBox(editButton, deleteButton,unbanUser);
                            hbox.setSpacing(50); // Espace entre les boutons
                            Insets buttonMargin = new Insets(50, 0, 0, 25);
                            Insets buttonMargin2 = new Insets(190, 0, 0, -135);
                            Insets buttonMargin3 = new Insets(120, 0, 0, -130);// Marge de 50 pixels en haut, 0 à droite, 0 en bas, 0 à gauche
                            // Marge de 50 pixels en haut, 0 à droite, 0 en bas, 0 à gauche
                            unbanUser.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 20px;"); // Modifier la couleur, la couleur du texte et la taille du texte
                            editButton.setStyle("-fx-background-color: #3CB371; -fx-text-fill: white; -fx-font-size: 20px;"); // Modifier la couleur, la couleur du texte et la taille du texte

// Ajouter du style au bouton deleteButton
                            deleteButton.setStyle("-fx-background-color: #FF5733; -fx-text-fill: white; -fx-font-size: 20px;"); // Modifier la couleur, la couleur du texte et la taille du texte
                            HBox.setMargin(editButton, buttonMargin);
                            HBox.setMargin(deleteButton, buttonMargin3);
                            HBox.setMargin(unbanUser, buttonMargin2);
                            // Créer un HBox global pour aligner les éléments
                            HBox globalHBox = new HBox(hBox, hbox);
                            globalHBox.setSpacing(20); // Espace entre le texte et les boutons
                            // globalHBox.setAlignment(Pos.CENTER);
                            globalHBox.setStyle("-fx-background-color: #bbcbac;");
                            // Définir le graphique de la cellule comme le HBox global
                            setGraphic(globalHBox);
                        }
                    }

                    private void showAlert(Alert.AlertType alertType, String title, String header, String message) {
                        Alert alert = new Alert(alertType);
                        alert.setTitle(title);
                        alert.setHeaderText(header);
                        alert.setContentText(message);
                        alert.showAndWait();
                    }

                };
            }
        });

        userListListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Utilisateur sélectionné : " + newValue.getName());
                System.out.println("Prénom : " + newValue.getLastname());
                System.out.println("Email : " + newValue.getEmail());
                System.out.println("Téléphone : " + newValue.getNumber());
                System.out.println("Role : " + newValue.getRoles());
                System.out.println("Date de naissance : " + newValue.getDatenaissance());
            }
        });
        userListListView.setOnMouseClicked(event -> {
            User selectedUser = userListListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                userService.show();
            }
        });

        Comparator<User> dateOfBirthComparator = Comparator.comparing(User::getDatenaissance, Comparator.nullsLast(Comparator.naturalOrder()));

        statusInput.setOnAction(event -> {
            String selectedSort = statusInput.getValue();
            if ("Nom".equals(selectedSort)) {
                Collections.sort(users, Comparator.comparing(User::getName));
            } else if ("Email".equals(selectedSort)) {
                Collections.sort(users, Comparator.comparing(User::getEmail));
            } else if ("Role".equals(selectedSort)) {
                Collections.sort(users, Comparator.comparing(User::getRoles));
            }else if ("Date".equals(selectedSort)) {
                Collections.sort(users, dateOfBirthComparator);
            }
            userListListView.getItems().setAll(users);

        });
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {

            List<User> filteredUsers = users.stream()
                    .filter(user ->
                            user.getName().toLowerCase().contains(newValue.toLowerCase()) ||
                                    user.getLastname().toLowerCase().contains(newValue.toLowerCase()) ||
                                    user.getEmail().toLowerCase().contains(newValue.toLowerCase()) ||
                                    user.getRoles().toLowerCase().contains(newValue.toLowerCase())
                    )
                    .toList();
            userListListView.getItems().setAll(filteredUsers);
        });


    }

    private void openUpdateUserWindow(User user) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/ModiferUser.fxml"));
        try {
            Parent root = loader.load();
            ModifierUser updateUserController = loader.getController();
            updateUserController.setUser(user);
            updateUserController.setParentController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            refreshUserList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void navigateToAddUser(MouseEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/AddUser.fxml"));
        Parent root;
        try {
            root = loader.load();
            Stage stage = (Stage) btnAddUser.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void statusChange(javafx.event.ActionEvent actionEvent) {
    }

    public void AddUserClicked(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/AjouterUser.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            refreshUserList();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void refreshUserList() {
        userListListView.getItems().clear();
        users = userService.show();
        userListListView.getItems().addAll(users);
    }

}
