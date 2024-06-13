package Controllers.event;

import Controllers.event.CRUDsponsor;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entities.Sponsor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import utils.MyDatabase;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Listsponsors {

    public ObservableList<Sponsor> data = FXCollections.observableArrayList();


    @FXML
    private AnchorPane content_area;

    @FXML
    private Button btnajouter;
    @FXML
    private ListView<Sponsor> listrec;

    @FXML
    private Button trie;

    @FXML
    private Button trie1;

    @FXML
    private TextField searchInput;

    private ObservableList<Sponsor> originalSponsorList;

    @FXML
    private void triNomD(ActionEvent event) {
        // Sort the list in descending order by Sponsor name using streams
        listrec.getItems().setAll(listrec.getItems().stream()
                .sorted(Comparator.comparing(Sponsor::getName).reversed())
                .toList());
    }

    // Method to perform sorting in ascending order
    @FXML
    private void triNomA(ActionEvent event) {
        // Sort the list in ascending order by Sponsor name using streams
        listrec.getItems().setAll(listrec.getItems().stream()
                .sorted(Comparator.comparing(Sponsor::getName))
                .toList());
    }


    @FXML
    private void initialize() {
        // Set listener for search input field to perform search automatically
        searchInput.textProperty().addListener((observable, oldValue, newValue) -> {
            // Get the search term from the input field
            String searchTerm = newValue.toLowerCase();

            // If the original list is null, store it for the first time
            if (originalSponsorList == null) {
                originalSponsorList = FXCollections.observableArrayList(listrec.getItems());
            }

            // Filter the original list based on the search term
            FilteredList<Sponsor> filteredList = new FilteredList<>(originalSponsorList);
            filteredList.setPredicate(sponsor -> sponsor.getName().toLowerCase().contains(searchTerm));

            // Update the ListView with the filtered list
            listrec.setItems(filteredList);
        });

        // Set the SponsorListCell as the cell factory for the ListView
        listrec.setCellFactory(new SponsorCellFactory());
        listrec.setCellFactory(param -> new SponsorListCell(listrec));
    }

    @FXML
    private VBox SPListContainer;

   /* @FXML

    public void initialize() {
        // Set the SponsorListCell as the cell factory for the ListView
        listrec.setCellFactory(new SponsorCellFactory());
        listrec.setCellFactory(param -> new SponsorListCell(listrec));
        // Add listener to hide/show the scroll pane container based on list items
        // listrec.getItems().addListener((ListChangeListener<Object>) change -> {
        //   if (change.getList().isEmpty()) {
        //     SPListContainer.setVisible(false);
        //} else {
        //  SPListContainer.setVisible(true);
        //}
        //});
    }*/

    @FXML
    public void showw() {
        // Clear the existing data bsh metdupliquish l old data khtr k naaml show f testing o show houny ijouny mertyn
        listrec.getItems().clear();
        try {
            String requete = "SELECT * FROM sponsor";
            Statement st = MyDatabase.getInstance().getConnection().createStatement();
            ResultSet rs = st.executeQuery(requete);
            while (rs.next()) {
                Sponsor r = new Sponsor(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getInt("number"));
                listrec.getItems().add(r); // Add Sponsor objects directly
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public class SponsorCellFactory implements Callback<ListView<Sponsor>, ListCell<Sponsor>> {
        @Override
        public ListCell<Sponsor> call(ListView<Sponsor> param) {
            return new ListCell<Sponsor>() {
                @Override
                protected void updateItem(Sponsor sponsor, boolean empty) {
                    super.updateItem(sponsor, empty);
                    if (empty || sponsor == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Customize the display of the Sponsor object
                        setText(String.format("Name: %s, Number: %d, Email: %s", sponsor.getName(), sponsor.getNumber(), sponsor.getEmail()));
                    }
                }
            };
        }
    }

    public class SponsorListCell extends ListCell<Sponsor> {
        private final BorderPane cellPane = new BorderPane();
        private final Button editButton;
        private final Button deleteButton;
        private final HBox buttonsContainer; // Container to hold the buttons
        private final ListView<Sponsor> listView; // Reference to the ListView

        public SponsorListCell(ListView<Sponsor> listView) {
            this.listView = listView;
            editButton = new Button();
            deleteButton = new Button();
            buttonsContainer = new HBox(editButton, deleteButton);

            // Set icons for edit and delete buttons
            FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
            editIcon.setSize("30");
            editButton.setGraphic(editIcon);
            editButton.setStyle("-fx-background-color: transparent;"); // Remove background color
            editButton.setCursor(Cursor.HAND); // Set cursor to hand

            FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            deleteIcon.setSize("30");
            deleteButton.setGraphic(deleteIcon);
            deleteButton.setStyle("-fx-background-color: transparent;"); // Remove background color
            deleteButton.setCursor(Cursor.HAND); // Set cursor to hand

            // Add action listeners for edit and delete buttons
            editButton.setOnAction(event -> {
                Sponsor sponsor = getItem();
                if (sponsor != null) {
                    openEditSponsorInterface(sponsor);
                }
            });

            deleteButton.setOnAction(event -> {
                Sponsor sponsor = getItem();
                if (sponsor != null) {
                    // Remove the sponsor from the ListView
                    listView.getItems().remove(sponsor);
                    // Call your deletion method here passing the sponsor's ID
                    deleteSponsor(sponsor.getId());
                }
            });

            // Optionally, you can set spacing between buttons or customize the layout
            buttonsContainer.setSpacing(10);

            // Place the buttons container on the right side of the BorderPane
            cellPane.setRight(buttonsContainer);

            // Set the container as the graphic of the cell
            setGraphic(cellPane);
        }


        @Override
        protected void updateItem(Sponsor sponsor, boolean empty) {
            super.updateItem(sponsor, empty);

            if (empty || sponsor == null) {
                setText(null);
                setGraphic(null);
            } else {
                Label sponsorDetailsLabel = new Label(String.format("Name: %s\nNumber: %d\nEmail: %s",
                        sponsor.getName(), sponsor.getNumber(), sponsor.getEmail()));

                // Set the sponsor details label to the left of the BorderPane
                cellPane.setLeft(sponsorDetailsLabel);

                // Set the BorderPane as the graphic of the cell
                setGraphic(cellPane);
            }
        }
    }


    private void openEditSponsorInterface(Sponsor sponsor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eventback/modifspon.fxml"));
            Parent root = loader.load();

            // Access the controller of the modifspon interface
            CRUDsponsor controller = loader.getController();

            // Pass the selected sponsor to the controller
            controller.initData(sponsor);

            // Create a new stage for the modifspon interface
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Sponsor");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Optionally, you can update the UI after editing
            // For example, you can refresh the list view
            showw();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteSponsor(int id) {
        try {
            // Prepare the SQL statement to delete the sponsor
            String deleteSponsorSQL = "DELETE FROM sponsor WHERE id = ?";
            PreparedStatement pstDeleteSponsor = MyDatabase.getInstance().getConnection().prepareStatement(deleteSponsorSQL);
            pstDeleteSponsor.setInt(1, id);

            // Execute deletion of sponsor
            int rowsAffectedSponsor = pstDeleteSponsor.executeUpdate();

            if (rowsAffectedSponsor > 0) {
                // Deletion of sponsor successful
                System.out.println("Sponsor deleted successfully.");
                showSuccessAlert("Deletion Successful", "Sponsor with ID  deleted successfully.");
            } else {
                // No rows affected, sponsor not found or deletion failed
                System.out.println("Failed to delete sponsor with ID .");
                showErrorAlertWithRefresh("Deletion Failed", "Failed to delete sponsor with ID .");
            }

            // Close the prepared statement
            pstDeleteSponsor.close();
        } catch (SQLException ex) {
            // Error occurred during deletion
            System.err.println("Error deleting sponsor: " + ex.getMessage());
            ex.printStackTrace();
            showErrorAlertWithRefresh("Error", "An error occurred while deleting sponsor: " + ex.getMessage());
        }
    }

    private void showErrorAlertWithRefresh(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(okButton);

        // Event handler for the OK button
        alert.setOnCloseRequest(event -> {
            // Refresh the table view to ensure deleted sponsor is still displayed
            showw();
        });

        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    //By extending ListCell<Sponsor>, you're creating a
    // custom cell implementation tailored to display Sponsor objects in a ListView.
    //When you extend ListCell<Sponsor>, you need to override the updateItem method to define how each cell should be updated when its item changes or when it's being reused for a different item.
    // This method is crucial for customizing the appearance and behavior of your list cells.


    @FXML
    void opensponadd(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eventback/addsponsors.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            // Wait for the "addSponn" form to be closed
            stage.initModality(Modality.APPLICATION_MODAL);

            // Set onCloseRequest event handler to refresh the list of sponsors when the form is closed
            stage.setOnCloseRequest(e -> {
                // Refresh the list of sponsors
                showw();
            });

            // Show the stage and wait for it to be closed
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    //PIEEEEEEEEYYYYYYYYYY


    @FXML
    private Pane chartContainer;
    @FXML
    private void stat() {
        try {
            // Get the data for the pie chart
            List<Map<String, Object>> sponsorEvents = getSponsorEvents();

            // Prepare the pie chart data
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for (Map<String, Object> row : sponsorEvents) {
                String sponsorName = (String) row.get("sponsorName");
                int eventCount = (Integer) row.get("eventCount");
                pieChartData.add(new PieChart.Data(sponsorName, eventCount));
            }

            // Load the popup window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eventback/stat.fxml"));
            Parent popupContent = loader.load();
            Stage popupStage = new Stage();
            Scene scene = new Scene(popupContent);
            popupStage.setScene(scene);

            // Pass the pie chart data to the popup controller
            CRUDsponsor controller = loader.getController();
            controller.setPieChartData(pieChartData);

            popupStage.show();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, Object>> getSponsorEvents() {
        List<Map<String, Object>> sponsorEvents = new ArrayList<>();
        try {
            String query = "SELECT s.name AS sponsorName, COUNT(e.id) AS eventCount " +
                    "FROM sponsor s " +
                    "JOIN evenement e ON s.id = e.sponsor_id " +
                    "GROUP BY s.name";
            PreparedStatement pst = MyDatabase.getInstance().getConnection().prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("sponsorName", rs.getString("sponsorName"));
                row.put("eventCount", rs.getInt("eventCount"));
                sponsorEvents.add(row);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return sponsorEvents;
    }

}







