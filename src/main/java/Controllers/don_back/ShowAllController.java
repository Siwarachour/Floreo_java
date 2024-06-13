package Controllers.don_back;

import entities.Don;
import entities.User;
import services.DonService;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ShowAllController implements Initializable {

    public static Don currentDon;

    @FXML
    private Button STAT;

    @FXML
    public Text topText;
    @FXML
    public VBox mainVBox;
    @FXML
    private TextField search;
    @FXML
    private Button trie1;

    @FXML
    private Button trie2;


    List<Don> listDon;

    public void initialize(URL url, ResourceBundle rb) {
        listDon = DonService.getInstance().getAll();
        displayData();

        search.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.isEmpty()) {
                // Use Java streams to filter the listDechets based on the search text
                List<Don> searchResults = listDon.stream()
                        .filter(currentDon ->
                                currentDon.getType().toLowerCase().contains(newValue.toLowerCase()) ||
                                        currentDon.getDescription().toLowerCase().contains(newValue.toLowerCase())
                        )
                        .collect(Collectors.toList());

                if (!searchResults.isEmpty()) {
                    // Update the listDechets with search results and display data
                    listDon.clear();
                    listDon.addAll(searchResults);
                    displayData();
                } else {
                    AlertUtils.makeInformation("No matching results found.");
                }
            } else {
                // If the search field is empty, display all data
                listDon.clear();
                listDon.addAll(DonService.getInstance().getAll());
                displayData();
            }
        });
    }
    @FXML
    void STAT(ActionEvent event) {
        // Create a new stage
        Stage stage = new Stage();

        // Assuming you have a method to get all Donations
        List<Don> allDonations = listDon;

        // Group by type and count
        Map<String, Long> typeCounts = allDonations.stream()
                .collect(Collectors.groupingBy(Don::getType, Collectors.counting()));

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
    void displayData() {
        mainVBox.getChildren().clear();

        Collections.reverse(listDon);

        if (!listDon.isEmpty()) {
            for (Don don : listDon) {

                mainVBox.getChildren().add(makeDonModel(don));

            }
        } else {
            StackPane stackPane = new StackPane();
            stackPane.setAlignment(Pos.CENTER);
            stackPane.setPrefHeight(200);
            stackPane.getChildren().add(new Text("Aucune donnée"));
            mainVBox.getChildren().add(stackPane);
        }
    }

    public Parent makeDonModel(Don don) {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(Constants.FXML_BACK_MODEL_DON)));

            HBox innerContainer = ((HBox) ((AnchorPane) ((AnchorPane) parent).getChildren().get(0)).getChildren().get(0));
            ((Text) innerContainer.lookup("#typeText")).setText("Type : " + don.getType());
            ((Text) innerContainer.lookup("#descriptionText")).setText("Description : " + don.getDescription());
            ((Text) innerContainer.lookup("#dateDonText")).setText("DateDon : " + don.getDateDon());

            // Check if User object is not null before accessing toString()
            User user = don.getUser();
            if (user != null) {
                ((Text) innerContainer.lookup("#userText")).setText("User : " + user.getName());
            } else {
                ((Text) innerContainer.lookup("#userText")).setText("User : [Unknown]");
            }

            ((Button) innerContainer.lookup("#editButton")).setOnAction((event) -> modifierDon(don));
            ((Button) innerContainer.lookup("#deleteButton")).setOnAction((event) -> supprimerDon(don));

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return parent;
    }

    private void modifierDon(Don don) {
        currentDon = don;
        try {
            // Charger le fichier FXML du nouvel interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_MANAGE_DON));
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

    private void supprimerDon(Don don) {
        currentDon = null;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText(null);
        alert.setContentText("Etes vous sûr de vouloir supprimer don ?");
        Optional<ButtonType> action = alert.showAndWait();

        if (action.isPresent()) {
            if (action.get() == ButtonType.OK) {
                if (DonService.getInstance().delete(don.getId())) {
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_DISPLAY_ALL_DON));
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
                    AlertUtils.makeError("Could not delete don");
                }
            }
        }
    }
    @FXML
    private void triNomD(ActionEvent event) {
        // Sort the list in descending order by donation date
        listDon.sort(Comparator.comparing(Don::getDateDon).reversed());
        displayData();
    }
    @FXML
    private void triNomA(ActionEvent event) {
        // Sort the list in ascending order by donation date
        listDon.sort(Comparator.comparing(Don::getDateDon));
        displayData();
    }




}
