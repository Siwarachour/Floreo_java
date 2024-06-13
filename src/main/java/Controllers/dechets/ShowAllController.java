package Controllers.dechets;

import Controllers.reservationDechets.ManageController;
import entities.Dechets;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.DechetsService;
import utils.AlertUtils;
import utils.Constants;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ShowAllController implements Initializable {

    public static Dechets currentDechets;

    @FXML
    public Text topText;
    @FXML
    public Button addButton;
    @FXML
    public VBox mainVBox;
    @FXML
    private TextField searchField2;

    @FXML
    private ImageView qrcode;
    List<Dechets> listDechets;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listDechets = DechetsService.getInstance().getAll();

        displayData();

        // Add a listener to the text property of searchField
        searchField2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.isEmpty()) {
                // Use Java streams to filter the listDechets based on the search text
                List<Dechets> searchResults = listDechets.stream()
                        .filter(dechets ->
                                dechets.getType().toLowerCase().contains(newValue.toLowerCase()) ||
                                        dechets.getDescription().toLowerCase().contains(newValue.toLowerCase()) ||
                                        String.valueOf(dechets.getQuantite()).toLowerCase().contains(newValue.toLowerCase()) ||
                                        String.valueOf(dechets.getId()).toLowerCase().contains(newValue.toLowerCase()) ||
                                        dechets.getDateEntre().toString().toLowerCase().contains(newValue.toLowerCase()))
                        .collect(Collectors.toList());

                if (!searchResults.isEmpty()) {
                    // Update the listDechets with search results and display data
                    listDechets.clear();
                    listDechets.addAll(searchResults);
                    displayData();
                } else {
                    AlertUtils.makeInformation("No matching results found.");
                }
            } else {
                // If the search field is empty, display all data
                listDechets.clear();
                listDechets.addAll(DechetsService.getInstance().getAll());
                displayData();
            }
        });

    }

    void displayData() {
        mainVBox.getChildren().clear();

        Collections.reverse(listDechets);

        if (!listDechets.isEmpty()) {
            for (Dechets dechets : listDechets) {

                mainVBox.getChildren().add(makeDechetsModel(dechets));

            }
        } else {
            StackPane stackPane = new StackPane();
            stackPane.setAlignment(Pos.CENTER);
            stackPane.setPrefHeight(200);
            stackPane.getChildren().add(new Text("Aucune Donnée"));
            mainVBox.getChildren().add(stackPane);
        }
    }

    public Parent makeDechetsModel(
            Dechets dechets
    ) {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(Constants.FXML_FRONT_MODEL_DECHETS)));

            HBox innerContainer = ((HBox) ((AnchorPane) ((AnchorPane) parent).getChildren().get(0)).getChildren().get(0));
            ((Text) innerContainer.lookup("#typeText")).setText("Type : " + dechets.getType());
            ((Text) innerContainer.lookup("#dateEntreText")).setText("DateEntre : " + dechets.getDateEntre());
            ((Text) innerContainer.lookup("#descriptionText")).setText("Description : " + dechets.getDescription());
            ((Text) innerContainer.lookup("#quantiteText")).setText("Quantite : " + dechets.getQuantite());

            // Generate QR code for each Dechets object
            String qrCodeText = dechets.toString1(); // Make sure the toString() method of Dechets returns a string representation of the Dechets data
            System.out.println(qrCodeText);
            ByteArrayOutputStream out = QRCode.from(qrCodeText).to(ImageType.PNG).stream();
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            Image qrCodeImage = new Image(in);

            // Set the Image to your ImageView
            ((ImageView) innerContainer.lookup("#qrcode")).setImage(qrCodeImage);

            Path selectedImagePath = FileSystems.getDefault().getPath(dechets.getImage());
            if (selectedImagePath.toFile().exists()) {
                ((ImageView) innerContainer.lookup("#imageIV")).setImage(new Image(selectedImagePath.toUri().toString()));
            }


            ((Button) innerContainer.lookup("#reserverBtn")).setOnAction((event) -> reserver(dechets));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return parent;
    }

    private void reserver(Dechets dechets) {
        currentDechets = dechets;
        ManageController.currentDechets = dechets;
        Controllers.reservationDechets.ShowAllController.currentReservationDechets = null;
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
}
