package Controllers.factureDon_back;
import entities.FactureDon;
import javafx.scene.Scene;
import services.FactureDonService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import javafx.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import utils.AlertUtils;
import utils.Constants;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;

public class ShowAllController implements Initializable {

    public static FactureDon currentFactureDon;

    @FXML
    public Text topText;
    @FXML
    public VBox mainVBox;
    @FXML
    private TextField search2;

    @FXML
    private Button print;

    List<FactureDon> listFactureDon;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listFactureDon = FactureDonService.getInstance().getAll();

        displayData();
        search2.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.isEmpty()) {
                    List<FactureDon> searchResults = FactureDonService.getInstance().search(newValue);
                    if (!searchResults.isEmpty()) {
                        listFactureDon.clear();
                        listFactureDon.addAll(searchResults);
                        displayData();
                    } else {
                        AlertUtils.makeInformation("No matching results found.");
                    }
                } else {
                    // If the search field is empty, display all data
                    listFactureDon.clear();
                    listFactureDon.addAll(FactureDonService.getInstance().getAll());
                    displayData();
                }
            }
        });
    }

    void displayData() {
        mainVBox.getChildren().clear();

        Collections.reverse(listFactureDon);

        if (!listFactureDon.isEmpty()) {
            for (FactureDon factureDon : listFactureDon) {

                mainVBox.getChildren().add(makeFactureDonModel(factureDon));

            }
        } else {
            StackPane stackPane = new StackPane();
            stackPane.setAlignment(Pos.CENTER);
            stackPane.setPrefHeight(200);
            stackPane.getChildren().add(new Text("Aucune donnée"));
            mainVBox.getChildren().add(stackPane);
        }
    }

    public Parent makeFactureDonModel(
            FactureDon factureDon
    ) {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(Constants.FXML_BACK_MODEL_FACTURE_DON)));

            HBox innerContainer = ((HBox) ((AnchorPane) ((AnchorPane) parent).getChildren().get(0)).getChildren().get(0));
            ((Text) innerContainer.lookup("#nomDonateurText")).setText("NomDonateur : " + factureDon.getNomDonateur());
            ((Text) innerContainer.lookup("#prenomDonateurText")).setText("PrenomDonateur : " + factureDon.getPrenomDonateur());
            ((Text) innerContainer.lookup("#emailText")).setText("Email : " + factureDon.getEmail());
            ((Text) innerContainer.lookup("#adressesText")).setText("Adresses : " + factureDon.getAdresses());
            ((Text) innerContainer.lookup("#numeroTelephoneText")).setText("NumeroTelephone : " + factureDon.getNumeroTelephone());
            ((Text) innerContainer.lookup("#descriptionText")).setText("Description : " + factureDon.getDescription());

            ((Text) innerContainer.lookup("#donText")).setText("Don : " + factureDon.getDon().toString());


            ((Button) innerContainer.lookup("#editButton")).setOnAction((event) -> modifierFactureDon(factureDon));
            ((Button) innerContainer.lookup("#deleteButton")).setOnAction((event) -> supprimerFactureDon(factureDon));


        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return parent;
    }

    private void modifierFactureDon(FactureDon factureDon) {
        currentFactureDon = factureDon;
        try {
            // Charger le fichier FXML du nouvel interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_MANAGE_FACTURE_DON));
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

    private void supprimerFactureDon(FactureDon factureDon) {
        currentFactureDon = null;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText(null);
        alert.setContentText("Etes vous sûr de vouloir supprimer factureDon ?");
        Optional<ButtonType> action = alert.showAndWait();
        if (action.isPresent()) {
            if (action.get() == ButtonType.OK) {
                if (FactureDonService.getInstance().delete(factureDon.getId())) {
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_DISPLAY_ALL_FACTURE_DON));
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
                    AlertUtils.makeError("Could not delete factureDon");
                }
            }
        }
    }
    private void generatePDF(List<FactureDon> factureDonList) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File selectedFile = fileChooser.showSaveDialog(new Stage());

        if (selectedFile != null) {
            String dest = selectedFile.getAbsolutePath();
            try {
                PdfDocument pdfDoc = new PdfDocument(new com.itextpdf.kernel.pdf.PdfWriter(dest));
                Document document = new Document(pdfDoc);

                // Add a title to the document
                Paragraph title = new Paragraph("Liste des factures de dons");
                title.setFontSize(16).setBold();
                document.add(title);

                // Create a table to display the data
                Table table = new Table(7);
                table.addCell("Nom Donateur");
                table.addCell("Prenom Donateur");
                table.addCell("Email");
                table.addCell("Adresses");
                table.addCell("Numero Telephone");
                table.addCell("Description");
                table.addCell("Don");

                // Fill the table with data from the factureDonList
                for (FactureDon factureDon : factureDonList) {
                    table.addCell(factureDon.getNomDonateur());
                    table.addCell(factureDon.getPrenomDonateur());
                    table.addCell(factureDon.getEmail());
                    table.addCell(factureDon.getAdresses());
                    table.addCell(String.valueOf(factureDon.getNumeroTelephone())); // Convert int to String
                    table.addCell(factureDon.getDescription());
                    table.addCell(String.valueOf(factureDon.getDon())); // Convert int to String
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
    private void print(ActionEvent event) {
        List<FactureDon> factureDonList = listFactureDon;
        generatePDF(factureDonList);
    }

}