package Controllers.reservationDechets_back;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import entities.ReservationDechets;
import services.ReservationDechetsService;
import utils.AlertUtils;
import utils.Constants;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ShowAllController implements Initializable {

    public static ReservationDechets currentReservationDechets;

    @FXML
    public Text topText;
    @FXML
    private Button Calendar;
    @FXML
    public VBox mainVBox;

    @FXML
    private TextField searchField4;
    List<ReservationDechets> listReservationDechets;
    @FXML
    private Button trie2;

    @FXML
    private Button trie3;

    @FXML
    private Button print;

    @FXML
    void Calendar(MouseEvent event) throws IOException {
        // Load the FXML file for the calendar scene
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/edu/esprit/flo/reservationDechets_back/Calendar.fxml"));
        Parent root = fxmlLoader.load();

        // Create a new stage for the calendar scene
        Stage stage = new Stage();
        stage.setTitle("Calendar");
        stage.setScene(new Scene(root));
        stage.show();
    }


    private boolean isDateTaken(LocalDate date) {
        for (ReservationDechets reservationDechets : listReservationDechets) {
            if (reservationDechets.getDateRamassage().isEqual(date)) {
                return true; // Date is taken by reservationDechets
            }
        }
        return false; // Date is not taken by reservationDechets
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listReservationDechets = ReservationDechetsService.getInstance().getAll();

        displayData();
        searchField4.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
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
            ReservationDechets reservationDechets) {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(Constants.FXML_BACK_MODEL_RESERVATION_DECHETS)));

            HBox innerContainer = ((HBox) ((AnchorPane) ((AnchorPane) parent).getChildren().get(0)).getChildren().get(0));
            ((Text) innerContainer.lookup("#dateText")).setText("Date : " + reservationDechets.getDate());
            ((Text) innerContainer.lookup("#dateRamassageText")).setText("DateRamassage : " + reservationDechets.getDateRamassage());
            ((Text) innerContainer.lookup("#nomFournisseurText")).setText("NomFournisseur : " + reservationDechets.getUser().getName());
            ((Text) innerContainer.lookup("#numeroTellText")).setText("NumeroTell : " + reservationDechets.getNumeroTell());
            ((Text) innerContainer.lookup("#quantiteText")).setText("Quantite : " + reservationDechets.getQuantite());
            // ((Text) innerContainer.lookup("#userText")).setText("User : " + (reservationDechets.getUser() == null ? "null" : reservationDechets.getUser().getName()));
            ((Text) innerContainer.lookup("#dechetTextt")).setText("Dechet : " + (reservationDechets.getDechets() == null ? "null" : reservationDechets.getDechets().toString()));



            ((Button) innerContainer.lookup("#editButton")).setOnAction((event) -> modifierReservationDechets(reservationDechets));
            ((Button) innerContainer.lookup("#deleteButton")).setOnAction((event) -> supprimerReservationDechets(reservationDechets));



        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return parent;
    }

    private void modifierReservationDechets(ReservationDechets reservationDechets) {
        currentReservationDechets = reservationDechets;
        try {
            // Charger le fichier FXML du nouvel interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_MANAGE_RESERVATION_DECHETS));
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
                    AlertUtils.makeError("Could not delete reservationDechets");
                }
            }
        }
    }

    @FXML
    private void triNomD(ActionEvent event) {
        // Sort the list in descending order by Sponsor name
        listReservationDechets.sort(Comparator.comparing(ReservationDechets::getQuantite).reversed());
        displayData();
    }

    // Method to perform sorting in ascending order
    @FXML
    private void triNomA(ActionEvent event) {
        // Sort the list in ascending order by Sponsor name
        listReservationDechets.sort(Comparator.comparing(ReservationDechets::getQuantite));
        displayData();
    }
    public void generatePDF(List<ReservationDechets> reservationDechetsList) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File selectedFile = fileChooser.showSaveDialog(new Stage());

        if (selectedFile != null) {
            String dest = selectedFile.getAbsolutePath();
            try {
                PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
                Document document = new Document(pdfDoc);

                // Add a title to the document
                Paragraph title = new Paragraph("Liste des réservations de déchets");
                title.setFontSize(16).setBold();
                document.add(title);

                // Create a table to display the data
                Table table = new Table(7);
                table.addCell("Date");
                table.addCell("Date de Ramassage");
                table.addCell("Nom du Fournisseur");
                table.addCell("Numéro de Téléphone");
                table.addCell("Quantité");
                table.addCell("User");
                table.addCell("Déchet");

                // Fill the table with data from the reservationDechetsList
                for (ReservationDechets reservationDechets : reservationDechetsList) {
                    table.addCell(reservationDechets.getDate().toString()); // Assuming you want to convert LocalDate to String
                    table.addCell(reservationDechets.getDateRamassage().toString()); // Assuming you want to convert LocalDate to String
                    table.addCell(reservationDechets.getNomFournisseur());
                    table.addCell(reservationDechets.getNumeroTell());
                    table.addCell(String.valueOf(reservationDechets.getQuantite()));
                    table.addCell(reservationDechets.getUser() != null ? reservationDechets.getUser().toString() : "null");
                    table.addCell(reservationDechets.getDechets() != null ? reservationDechets.getDechets().toString() : "null");
                }

                // Add the table to the document
                document.add(table);

                // Close the document
                document.close();

                // Show a success alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("PDF file saved successfully!");
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No file selected.");
        }
    }
    @FXML
    void print(ActionEvent event) {
        List<ReservationDechets> reservationDechetsList = listReservationDechets;
        generatePDF(reservationDechetsList);
    }




}




