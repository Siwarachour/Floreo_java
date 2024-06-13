package Controllers.front.reclamation;

import entities.Reclamation;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.ReclamationService;
import utils.AlertUtils;
import utils.Constants;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;

public class ShowAllController implements Initializable {

    public static Reclamation currentReclamation;

    @FXML
    public Text topText;
    @FXML
    public Button addButton;
    @FXML
    public VBox mainVBox;
    @FXML
    public ComboBox<String> sortCB;
    @FXML
    public TextField searchTF;


    List<Reclamation> listReclamation;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listReclamation = ReclamationService.getInstance().getAll();

        sortCB.getItems().addAll(
                "Tri par type",
                "Tri par description",
                "Tri par dateajout",
                "Tri par datemodif",
                "Tri par user"
        );

        displayData("");
    }


    void displayData(String searchText) {
        mainVBox.getChildren().clear();

        Collections.reverse(listReclamation);

        listReclamation.stream()
                .filter(
                        reclamation -> searchText.isEmpty()
                                || reclamation.getType().contains(searchText)
                                || reclamation.getDescription().contains(searchText)
                                || (reclamation.getUser().toString() != null && reclamation.getUser().toString().contains(searchText))
                )
                .map(this::makeReclamationModel)
                .forEach(mainVBox.getChildren()::add);

        if (listReclamation.isEmpty()) {
            StackPane stackPane = new StackPane();
            stackPane.setAlignment(Pos.CENTER);
            stackPane.setPrefHeight(200);
            stackPane.getChildren().add(new Text("Aucune donnée"));
            mainVBox.getChildren().add(stackPane);
        }
    }

    public Parent makeReclamationModel(
            Reclamation reclamation
    ) {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(Constants.FXML_FRONT_MODEL_RECLAMATION)));

            HBox innerContainer = ((HBox) ((AnchorPane) ((AnchorPane) parent).getChildren().get(0)).getChildren().get(0));
            ((Text) innerContainer.lookup("#typeText")).setText("Type : " + reclamation.getType());
            ((Text) innerContainer.lookup("#descriptionText")).setText("Description : " + reclamation.getDescription());
            ((Text) innerContainer.lookup("#dateajoutText")).setText("Dateajout : " + reclamation.getDateajout());
            ((Text) innerContainer.lookup("#datemodifText")).setText("Datemodif : " + reclamation.getDatemodif());

            ((Text) innerContainer.lookup("#userText")).setText("User : " + reclamation.getUser().getName());

            ((javafx.scene.control.Button) innerContainer.lookup("#pdfButton")).setOnAction((ignored) -> genererPDF(reclamation));
            ((Button) innerContainer.lookup("#editButton")).setOnAction((ignored) -> modifierReclamation(reclamation));
            ((Button) innerContainer.lookup("#deleteButton")).setOnAction((ignored) -> supprimerReclamation(reclamation));


        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return parent;
    }

    @FXML
    private void ajouterReclamation(ActionEvent ignored) {
        currentReclamation = null;
        try {
            // Charger le fichier FXML du nouvel interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_FRONT_MANAGE_RECLAMATION));
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

    private void modifierReclamation(Reclamation reclamation) {
        currentReclamation = reclamation;
        try {
            // Charger le fichier FXML du nouvel interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_FRONT_MANAGE_RECLAMATION));
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

    private void supprimerReclamation(Reclamation reclamation) {
        currentReclamation = null;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText(null);
        alert.setContentText("Etes vous sûr de vouloir supprimer reclamation ?");
        Optional<ButtonType> action = alert.showAndWait();

        if (action.isPresent()) {
            if (action.get() == ButtonType.OK) {
                if (ReclamationService.getInstance().delete(reclamation.getId())) {
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_FRONT_DISPLAY_ALL_RECLAMATION));
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
                    AlertUtils.errorNotification("Could not delete reclamation");
                }
            }
        }
    }

    @FXML
    public void sort(ActionEvent actionEvent) {
        Reclamation.compareVar = sortCB.getValue();
        listReclamation.sort(Reclamation::compareTo);
        displayData(searchTF.getText() == null ? "" : searchTF.getText());
    }

    public void search(KeyEvent keyEvent) {
        displayData(searchTF.getText());
    }

    private void genererPDF(Reclamation reclamation) {
        String filename = "reclamation.pdf";

        Document document = new Document();
        try {
            PdfWriter writer = PdfWriter.getInstance(document, Files.newOutputStream(Paths.get(filename)));
            document.open();

            com.itextpdf.text.Font font = new com.itextpdf.text.Font();
            font.setSize(20);

            document.add(new Paragraph("- Reclamation -"));

            document.add(new Paragraph("Type : " + reclamation.getType()));
            document.add(new Paragraph("Description : " + reclamation.getDescription()));
            document.add(new Paragraph("Dateajout : " + reclamation.getDateajout()));
            document.add(new Paragraph("Datemodif : " + reclamation.getDatemodif()));
            document.add(new Paragraph("User : " + reclamation.getUser()));

            document.newPage();
            document.close();

            writer.close();

            Desktop.getDesktop().open(new File(filename));
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }


}
