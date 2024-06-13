package Controllers.commande_back;


import entities.Commande;
import entities.Produit;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.CommandeService;
import utils.AlertUtils;
import utils.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ManageController implements Initializable {

    @FXML
    public TextField montantTF;
    @FXML
    public TextField lieucmdTF;
    @FXML
    public TextField quantiteTF;
    @FXML
    public ComboBox<Produit> produitCB;

    @FXML
    public Button btnAjout;
    @FXML
    public Text topText;

    Commande currentCommande;


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        for (Produit produit : CommandeService.getInstance().getAllProduits()) {
            produitCB.getItems().add(produit);
        }

        currentCommande = ShowAllController.currentCommande;

        if (currentCommande != null) {
            topText.setText("Modifier commande");
            btnAjout.setText("Modifier");

            try {
                montantTF.setText(String.valueOf(currentCommande.getMontant()));
                lieucmdTF.setText(currentCommande.getLieucmd());
                quantiteTF.setText(String.valueOf(currentCommande.getQuantite()));

                produitCB.setValue(currentCommande.getProduit());

            } catch (NullPointerException ignored) {
                System.out.println("NullPointerException");
            }
        } else {
            topText.setText("Ajouter commande");
            btnAjout.setText("Ajouter");
        }
    }

    @FXML
    private void manage(ActionEvent ignored) {

        if (controleDeSaisie()) {

            Commande commande = new Commande();
            commande.setMontant(Float.parseFloat(montantTF.getText()));
            commande.setDatecmd(LocalDate.now());
            commande.setLieucmd(lieucmdTF.getText());
            commande.setQuantite(Integer.parseInt(quantiteTF.getText()));
            commande.setProduit(produitCB.getValue());

            if (currentCommande == null) {
                if (CommandeService.getInstance().add(commande)) {
                    AlertUtils.makeSuccessNotification("Commande ajouté avec succés");
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_DISPLAY_ALL_COMMANDE));
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
                commande.setId(currentCommande.getId());
                if (CommandeService.getInstance().edit(commande)) {
                    AlertUtils.makeSuccessNotification("Commande modifié avec succés");
                    ShowAllController.currentCommande = null;
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_DISPLAY_ALL_COMMANDE));
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

        }
    }


    private boolean controleDeSaisie() {


        if (montantTF.getText().isEmpty()) {
            AlertUtils.makeInformation("montant ne doit pas etre vide");
            return false;
        }


        try {
            Float.parseFloat(montantTF.getText());
        } catch (NumberFormatException ignored) {
            AlertUtils.makeInformation("montant doit etre un réel");
            return false;
        }

        if (lieucmdTF.getText().isEmpty()) {
            AlertUtils.makeInformation("lieucmd ne doit pas etre vide");
            return false;
        }


        if (quantiteTF.getText().isEmpty()) {
            AlertUtils.makeInformation("quantite ne doit pas etre vide");
            return false;
        }


        try {
            Integer.parseInt(quantiteTF.getText());
        } catch (NumberFormatException ignored) {
            AlertUtils.makeInformation("quantite doit etre un nombre");
            return false;
        }

        if (produitCB.getValue() == null) {
            AlertUtils.makeInformation("Veuillez choisir un produit");
            return false;
        }
        return true;
    }
}