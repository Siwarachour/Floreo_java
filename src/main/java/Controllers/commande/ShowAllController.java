package Controllers.commande;

import entities.Commande;
import entities.User;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.CommandeService;
import services.UserService;
import utils.AlertUtils;
import utils.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ShowAllController implements Initializable {

    public static Commande currentCommande;

    @FXML
    public Text topText;
    @FXML
    public Button addButton;
    @FXML
    public VBox mainVBox;

    public  static User user;
    List<Commande> listCommande;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listCommande = CommandeService.getInstance().getAll();
        System.out.println(listCommande);

        displayData();
    }

    void displayData() {
        mainVBox.getChildren().clear();

        Collections.reverse(listCommande);

        if (!listCommande.isEmpty()) {
            for (Commande commande : listCommande) {
                // System.out.println(commande.getUser().toString());
                mainVBox.getChildren().add(makeCommandeModel(commande));

            }
        } else {
            StackPane stackPane = new StackPane();
            stackPane.setAlignment(Pos.CENTER);
            stackPane.setPrefHeight(200);
            stackPane.getChildren().add(new Text("Aucune donnée"));
            mainVBox.getChildren().add(stackPane);
        }
    }

    public Parent makeCommandeModel(
            Commande commande
    ) {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(Constants.FXML_FRONT_MODEL_COMMANDE)));

            HBox innerContainer = ((HBox) ((AnchorPane) ((AnchorPane) parent).getChildren().get(0)).getChildren().get(0));
            ((Text) innerContainer.lookup("#montantText")).setText("Montant : " + commande.getMontant());
            ((Text) innerContainer.lookup("#datecmdText")).setText("Datecmd : " + commande.getDatecmd());
            ((Text) innerContainer.lookup("#lieucmdText")).setText("Lieucmd : " + commande.getLieucmd());
            ((Text) innerContainer.lookup("#quantiteText")).setText("Quantite : " + commande.getQuantite());
            ((Text) innerContainer.lookup("#produitText")).setText("Produit : " + commande.getProduit().toString());
            ((Text) innerContainer.lookup("#user")).setText("User : " + commande.getUser().toString());


            ((Button) innerContainer.lookup("#editButton")).setOnAction((event) -> modifierCommande(commande));
            ((Button) innerContainer.lookup("#deleteButton")).setOnAction((event) -> supprimerCommande(commande));


        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return parent;
    }

    @FXML
    private void ajouterCommande(ActionEvent ignored) {
        currentCommande = null;
        ManageController.selectedProduit = null;
        try {
            // Charger le fichier FXML du nouvel interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_FRONT_MANAGE_COMMANDE));
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
    }

    private void modifierCommande(Commande commande) {
        currentCommande = commande;
        ManageController.selectedProduit = null;
        try {
            // Charger le fichier FXML du nouvel interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_FRONT_MANAGE_COMMANDE));
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
    }

    private void supprimerCommande(Commande commande) {
        currentCommande = null;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText(null);
        alert.setContentText("Etes vous sûr de vouloir supprimer commande ?");
        Optional<ButtonType> action = alert.showAndWait();

        if (action.isPresent()) {
            if (action.get() == ButtonType.OK) {
                if (CommandeService.getInstance().delete(commande.getId())) {
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_FRONT_DISPLAY_ALL_COMMANDE));
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
                    AlertUtils.makeError("Could not delete commande");
                }
            }
        }
    }
}
