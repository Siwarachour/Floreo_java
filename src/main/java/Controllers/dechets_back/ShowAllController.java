package Controllers.dechets_back;


import entities.Dechets;
import services.DechetsService;
import utils.AlertUtils;
import utils.Constants;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
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
    private TextField searchField;

    @FXML
    private ImageView qrcode;


    @FXML
    private Button trie;

    @FXML
    private Button trie1;

    List<Dechets> listDechets;



    @FXML
    void stat(MouseEvent event) {
        // Create a new stage
        Stage stage = new Stage();

        // Assuming you have a method to get all Dechets
        List<Dechets> allDechets = listDechets;

        // Group by type and count
        Map<String, Long> typeCounts = allDechets.stream()
                .collect(Collectors.groupingBy(Dechets::getType, Collectors.counting()));

        // Create pie chart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (Map.Entry<String, Long> entry : typeCounts.entrySet()) {
            PieChart.Data data = new PieChart.Data(entry.getKey(), entry.getValue());
            pieChartData.add(data);
        }

        // Create the PieChart
        PieChart pieChart = new PieChart(pieChartData);

        // Add a label to each slice
        pieChartData.forEach(data ->
                data.nameProperty().bind(
                        Bindings.concat(
                                data.getName(), " ", data.pieValueProperty()
                        )
                )
        );

        // Create the scene and show the stage
        StackPane root = new StackPane(pieChart);
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listDechets = DechetsService.getInstance().getAll();
        displayData();

        // Add a listener to the text property of searchField
        searchField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
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


    public Parent makeDechetsModel(Dechets dechets) {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(Constants.FXML_BACK_MODEL_DECHETS)));

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

            ((Button) innerContainer.lookup("#editButton")).setOnAction((event) -> modifierDechets(dechets));
            ((Button) innerContainer.lookup("#deleteButton")).setOnAction((event) -> supprimerDechets(dechets));

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return parent;
    }


    @FXML
    private void ajouterDechets(ActionEvent ignored) {
        currentDechets = null;
        try {
            // Charger le fichier FXML du nouvel interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_MANAGE_DECHETS));
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

    private void modifierDechets(Dechets dechets) {
        currentDechets = dechets;
        try {
            // Charger le fichier FXML du nouvel interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_MANAGE_DECHETS));
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

    private void supprimerDechets(Dechets dechets) {
        currentDechets = null;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText(null);
        alert.setContentText("Etes vous sûr de vouloir supprimer dechets ?");
        Optional<ButtonType> action = alert.showAndWait();

        if (action.isPresent()) {
            if (action.get() == ButtonType.OK) {
                if (DechetsService.getInstance().delete(dechets.getId())) {
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_DISPLAY_ALL_DECHETS));
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
                    AlertUtils.makeError("Could not delete dechets");
                }
            }
        }
    }
    @FXML
    private void triNomD(ActionEvent event) {
        // Sort the list in descending order by Sponsor name
        listDechets.sort(Comparator.comparing(Dechets::getQuantite).reversed());
        displayData();
    }

    // Method to perform sorting in ascending order
    @FXML
    private void triNomA(ActionEvent event) {
        // Sort the list in ascending order by Sponsor name
        listDechets.sort(Comparator.comparing(Dechets::getQuantite));
        displayData();
    }

}
