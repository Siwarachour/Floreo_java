package Controllers.reservationDechets_back;


import entities.Dechets;
import entities.ReservationDechets;
import entities.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.DechetsService;
import services.ReservationDechetsService;
import utils.AlertUtils;
import utils.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ManageController implements Initializable {

    @FXML
    public DatePicker dateDP;
    @FXML
    public DatePicker dateRamassageDP;
    @FXML
    public TextField nomFournisseurTF;
    @FXML
    public TextField numeroTellTF;
    @FXML
    public TextField quantiteTF;


    @FXML
    public ComboBox<Dechets> dechetsCB;

    @FXML
    public Button btnAjout;
    @FXML
    public Text topText;

    ReservationDechets currentReservationDechets;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println(url);


        for (Dechets dechets : DechetsService.getInstance().getAll()) {
            dechetsCB.getItems().add(dechets);
        }

        currentReservationDechets = ShowAllController.currentReservationDechets;

        if (currentReservationDechets != null) {
            topText.setText("Modifier reservationDechets");
            btnAjout.setText("Modifier");

            try {
                dateDP.setValue(currentReservationDechets.getDate());
                dateRamassageDP.setValue(currentReservationDechets.getDateRamassage());
                nomFournisseurTF.setText(currentReservationDechets.getNomFournisseur());
                numeroTellTF.setText(currentReservationDechets.getNumeroTell());
                quantiteTF.setText(String.valueOf(currentReservationDechets.getQuantite()));

                dechetsCB.setValue(currentReservationDechets.getDechets());
            } catch (NullPointerException ignored) {
                System.out.println("NullPointerException");
            }
        } else {
            topText.setText("Ajouter reservationDechets");
            btnAjout.setText("Ajouter");
        }
    }

    @FXML
    private void manage(ActionEvent ignored) {

        if (controleDeSaisie()) {

            ReservationDechets reservationDechets = new ReservationDechets();
            currentReservationDechets.setDate(dateDP.getValue());
            currentReservationDechets.setDateRamassage(dateRamassageDP.getValue());
            currentReservationDechets.setNomFournisseur(nomFournisseurTF.getText());
            currentReservationDechets.setNumeroTell(numeroTellTF.getText());
            currentReservationDechets.setQuantite(Integer.parseInt(quantiteTF.getText()));

            currentReservationDechets.setDechets(dechetsCB.getValue());

            if (currentReservationDechets != null) {
                if (ReservationDechetsService.getInstance().add( currentReservationDechets)) {
                    AlertUtils.makeSuccessNotification("ReservationDechets ajouté avec succés");
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_DISPLAY_ALL_RESERVATION_DECHETS));
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
                reservationDechets.setId(currentReservationDechets.getId());
                if (ReservationDechetsService.getInstance().edit(reservationDechets)) {
                    AlertUtils.makeSuccessNotification("ReservationDechets modifié avec succés");
                    ShowAllController.currentReservationDechets = null;
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_DISPLAY_ALL_RESERVATION_DECHETS));
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


        if (dateDP.getValue() == null) {
            AlertUtils.makeInformation("Choisir une date pour date");
            return false;
        }


        if (dateRamassageDP.getValue() == null) {
            AlertUtils.makeInformation("Choisir une date pour dateRamassage");
            return false;
        }


        if (nomFournisseurTF.getText().isEmpty()) {
            AlertUtils.makeInformation("nomFournisseur ne doit pas etre vide");
            return false;
        }


        if (numeroTellTF.getText().isEmpty()) {
            AlertUtils.makeInformation("numeroTell ne doit pas etre vide");
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



        if (dechetsCB.getValue() == null) {
            AlertUtils.makeInformation("Veuillez choisir un dechet");
            return false;
        }

        return true;
    }
}
