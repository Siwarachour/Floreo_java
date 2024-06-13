package Controllers.reservationDechets;

import entities.ReservationDechets;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.ReservationDechetsService;
import utils.AlertUtils;
import utils.Constants;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ShowAllController implements Initializable {

    public static ReservationDechets currentReservationDechets;

    @FXML
    private Button Calendar;

    @FXML
    public Text topText;
    @FXML
    public VBox mainVBox;
    @FXML
    private TextField searchField3;





    List<ReservationDechets> listReservationDechets;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listReservationDechets = ReservationDechetsService.getInstance().getAll();

        displayData();

        searchField3.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.isEmpty()) {
                // Use Java streams to filter the listDechets based on the search text
                List<ReservationDechets> searchResults = listReservationDechets.stream()
                        .filter(reservationDechets ->
                                reservationDechets.getNomFournisseur().toLowerCase().contains(newValue.toLowerCase()) ||
                                        String.valueOf(reservationDechets.getQuantite()).toLowerCase().contains(newValue.toLowerCase()) ||
                                        String.valueOf(reservationDechets.getDate()).toLowerCase().contains(newValue.toLowerCase()) ||
                                        String.valueOf(reservationDechets.getDateRamassage()).toLowerCase().contains(newValue.toLowerCase()) ||
                                        String.valueOf(reservationDechets.getNumeroTell()).toLowerCase().contains(newValue.toLowerCase()) ||

                                        reservationDechets.getDechets().getType().toLowerCase().contains(newValue.toLowerCase())
                        )
                        .collect(Collectors.toList());

                if (!searchResults.isEmpty()) {
                    // Update the listDechets with search results and display data
                    listReservationDechets.clear();
                    listReservationDechets.addAll(searchResults);
                    displayData();
                } else {
                    AlertUtils.makeInformation("No matching results found.");
                }
            } else {
                // If the search field is empty, display all data
                listReservationDechets.clear();
                listReservationDechets.addAll(ReservationDechetsService.getInstance().getAll());
                displayData();
            }
        });
    }

    void displayData() {
        mainVBox.getChildren().clear();

        Collections.reverse(listReservationDechets);

        if (!listReservationDechets.isEmpty()) {
            for (ReservationDechets reservationDechets : listReservationDechets) {

                mainVBox.getChildren().add(makeReservationDechetsModel(reservationDechets));

            }
        } else {
            StackPane stackPane = new StackPane();
            stackPane.setAlignment(Pos.CENTER);
            stackPane.setPrefHeight(200);
            stackPane.getChildren().add(new Text("Aucune Donnée"));
            mainVBox.getChildren().add(stackPane);
        }
    }

    public Parent makeReservationDechetsModel(
            ReservationDechets reservationDechets
    ) {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(Constants.FXML_FRONT_MODEL_RESERVATION_DECHETS)));

            HBox innerContainer = ((HBox) ((AnchorPane) ((AnchorPane) parent).getChildren().get(0)).getChildren().get(0));
            ((Text) innerContainer.lookup("#dateText")).setText("Date : " + reservationDechets.getDate());
            ((Text) innerContainer.lookup("#dateRamassageText")).setText("DateRamassage : " + reservationDechets.getDateRamassage());
            ((Text) innerContainer.lookup("#nomFournisseurText")).setText("NomFournisseur : " + reservationDechets.getNomFournisseur());
            ((Text) innerContainer.lookup("#numeroTellText")).setText("NumeroTell : " + reservationDechets.getNumeroTell());
            ((Text) innerContainer.lookup("#quantiteText")).setText("Quantite : " + reservationDechets.getQuantite());

        //    ((Text) innerContainer.lookup("#userText")).setText("User : " + (reservationDechets.getUser() == null ? "null" : reservationDechets.getUser().toString()));
            ((Text) innerContainer.lookup("#dechetTextt")).setText("Dechet : " + (reservationDechets.getDechets() == null ? "null" : reservationDechets.getDechets().toString()));


            ((Button) innerContainer.lookup("#editButton")).setOnAction((event) -> modifierReservationDechets(reservationDechets));
            ((Button) innerContainer.lookup("#deleteButton")).setOnAction((event) -> supprimerReservationDechets(reservationDechets));


        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return parent;
    }

    private void modifierReservationDechets(ReservationDechets reservationDechets) {
        ManageController.currentDechets = null;
        currentReservationDechets = reservationDechets;
        try {
            // Charger le fichier FXML du nouvel interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_FRONT_MANAGE_RESERVATION_DECHETS));
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

    private void supprimerReservationDechets(ReservationDechets reservationDechets) {
        currentReservationDechets = null;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText(null);
        alert.setContentText("Etes vous sûr de vouloir supprimer reservationDechets ?");
        Optional<ButtonType> action = alert.showAndWait();

        if (action.isPresent()) {
            if (action.get() == ButtonType.OK) {
                if (ReservationDechetsService.getInstance().delete(reservationDechets.getId())) {
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_FRONT_DISPLAY_ALL_RESERVATION_DECHETS));
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
                    AlertUtils.makeError("Could not delete reservationDechets");
                }
            }
        }
    }
}
