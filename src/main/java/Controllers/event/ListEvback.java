package Controllers.event;

import entities.Evenement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.ServiceEvenement;
import utils.MyDatabase;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

public class ListEvback {



    //nalabo welyna

/*
    @FXML
    private void goToRecommendedEvents(ActionEvent event) {
        try {
            // Load the FXML file of the recommended events view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eventfront/RECCEV.fxml"));
            Parent root = loader.load();

            // Get the controller instance
            REC recommendedEventsController = loader.getController();

            // Populate recommended events
            recommendedEventsController.populateRecommendedEvents();

            // Set up the stage and scene
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Show the recommended events view
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
    @FXML
    private Pane content_area;

    @FXML
    private Button btnajouter;

    @FXML
    private ListView<Evenement> listEv;

    @FXML
    private ComboBox<?> statusInput;

    @FXML
    private TextField searchInput;

    @FXML
    private Button trie1;

    @FXML
    private Button trie;


    private ObservableList<Evenement> originalEventList;
   // Assuming you have a TextField named searchInput

    @FXML
    public void initialize() {
        // Set the CustomEventCellFactory as the cell factory for the ListView
        listEv.setCellFactory(new CombinedEventCellFactory());

        // Populate the ListView with event data
        showEvents();

        searchInput.textProperty().addListener((observable, oldValue, newValue) -> {
            // Get the search term from the input field
            String searchTerm = newValue.toLowerCase();

            // If the original list is null, store it for the first time
            if (originalEventList == null) {
                originalEventList = FXCollections.observableArrayList(listEv.getItems());
            }

            // Filter the original list based on the search term
            FilteredList<Evenement> filteredList = new FilteredList<>(originalEventList);
            filteredList.setPredicate(event -> event.getNameevent().toLowerCase().contains(searchTerm));

            // Update the ListView with the filtered list
            listEv.setItems(filteredList);
        });

        // Set the CustomEventCellFactory as the cell factory for the ListView
        listEv.setCellFactory(new CombinedEventCellFactory());
    }
    @FXML
    private void triNomD(ActionEvent event) {
        // Sort the list in descending order by Event name using streams
        listEv.getItems().setAll(listEv.getItems().stream()
                .sorted(Comparator.comparing(Evenement::getNameevent).reversed())
                .toList());
    }

    // Method to perform sorting in ascending order
    @FXML
    private void triNomA(ActionEvent event) {
        // Sort the list in ascending order by Event name using streams
        listEv.getItems().setAll(listEv.getItems().stream()
                .sorted(Comparator.comparing(Evenement::getNameevent))
                .toList());
    }



    // Populate the ListView with event data



    @FXML
    public void showEvents() {
        // Clear the existing data
        listEv.getItems().clear();
        try {
            String query = "SELECT * FROM evenement";
            Statement statement = MyDatabase.getInstance().getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Evenement event = new Evenement(
                        resultSet.getInt("id"),
                        resultSet.getInt("sponsor_id"),
                        resultSet.getString("nameevent"),
                        resultSet.getString("type"),
                        resultSet.getString("datedebut"),
                        resultSet.getString("datefin"),
                        resultSet.getString("description"),
                        resultSet.getInt("nbparticipant"),
                        resultSet.getString("lieu"),
                        resultSet.getString("image")
                );
                listEv.getItems().add(event); // Add Event objects directly
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    // Custom ListCell factory to display Event data
    class CombinedEventCellFactory implements Callback<ListView<Evenement>, ListCell<Evenement>> {
        @Override
        public ListCell<Evenement> call(ListView<Evenement> param) {
            return new ListCell<Evenement>() {
                private final Button editButton = new Button();
                private final Button deleteButton = new Button();
                private final Button participateButton = new Button("Participate");
                private final HBox buttonsContainer = new HBox(editButton, deleteButton);
                private final BorderPane cellPane = new BorderPane();
                {
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
                        Evenement eventt = getItem();
                        if (eventt != null) {
                            openEditEventInterface(eventt);
                        }
                    });

                    deleteButton.setOnAction(event -> {
                        Evenement eventt = getItem();
                        if (eventt != null) {
                            deleteEvent(eventt.getId());
                            listEv.getItems().remove(eventt); // Remove the event from the ListView
                        }
                    });
                    // Add action listener for the Participate button
                    participateButton.setOnAction(event -> {
                        Evenement eventt = getItem();
                        if (eventt != null) {
                            participateEvent(eventt);
                        }
                    });
                    buttonsContainer.setSpacing(0);
                    cellPane.setRight(buttonsContainer);
                }


                @Override
                protected void updateItem(Evenement event, boolean empty) {
                    super.updateItem(event, empty);
                    if (empty || event == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Display event details and image as before
                        ImageView imageView = new ImageView();
                        String imagePath = event.getImage();
                        if (imagePath != null && !imagePath.isEmpty()) {
                            File file = new File(imagePath);
                            if (file.exists()) {
                                imageView.setImage(new Image(file.toURI().toString()));
                            } else {
                                imageView.setImage(new Image(getClass().getResourceAsStream(imagePath)));
                            }
                        }
                        imageView.setFitWidth(100);
                        imageView.setFitHeight(100);
                        Label eventDetailsLabel = new Label(String.format("Event Name: %s\nType: %s\nStart Date: %s\nEnd Date: %s\nDescription: %s\nParticipants: %d\nLocation: %s",
                                event.getNameevent(), event.getType(), event.getDatedebut(), event.getDatefin(),
                                event.getDescription(), event.getNbparticipant(), event.getLieu()));
                        System.out.println("Event Name: " + event.getNameevent());
                        System.out.println("Type: " + event.getType());
                        System.out.println("Start Date: " + event.getDatedebut());
                        System.out.println("End Date: " + event.getDatefin());
                        System.out.println("Description: " + event.getDescription());
                        System.out.println("Participants: " + event.getNbparticipant());
                        System.out.println("Location: " + event.getLieu());
                        // Set the event details label to the center of the BorderPane
                        cellPane.setCenter(eventDetailsLabel);

                        // Set the ImageView as the left graphic of the BorderPane
                        cellPane.setLeft(imageView);

                        // Set the BorderPane as the graphic of the cell
                        setGraphic(cellPane);
                        }
                    }

            };
        }

        private void openEditEventInterface(Evenement eventt) {

                try {
                    // Load the edit event interface
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/eventback/editEV.fxml"));
                    Parent root = loader.load();

                    // Access the controller of the edit event interface
                    Crudevent controller = loader.getController();

                    // Pass the selected event to the controller
                    controller.initData(eventt); // This line initializes the controller with the selected event's data

                    // Create a new stage for the edit event interface
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Edit Event");
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();

                    // Optionally, refresh the list view after editing
                    showEvents(); // Ensure you have a method to refresh the list view
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        private void deleteEvent(int id) {
            try {
                String deleteEventSQL = "DELETE FROM evenement WHERE id = ?";
                PreparedStatement pstDeleteEvent = MyDatabase.getInstance().getConnection().prepareStatement(deleteEventSQL);
                pstDeleteEvent.setInt(1, id);

                int rowsAffectedEvent = pstDeleteEvent.executeUpdate();

                if (rowsAffectedEvent > 0) {
                    System.out.println("Event with ID " + id + " deleted successfully.");
                    // Optionally, show a success alert
                } else {
                    System.out.println("Failed to delete event with ID " + id + ".");
                    // Optionally, show an error alert
                }

                pstDeleteEvent.close();
            } catch (SQLException ex) {
                System.err.println("Error deleting event: " + ex.getMessage());
                ex.printStackTrace();
                // Optionally, show an error alert
            }
        }
        private void participateEvent(Evenement event) {
            // Call the addUserToEvent method from the ServiceEvenement class
            ServiceEvenement serviceEvenement = new ServiceEvenement();
            // Assuming getCurrentUserId() returns the ID of the current user
            int userId =1; // Provide the user ID here
            serviceEvenement.updateParticipantsCount( event.getId());
        }
    }



    @FXML
    void openaddev(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eventback/addevent.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            // Wait for the "addSponn" form to be closed
            stage.initModality(Modality.APPLICATION_MODAL);

            // Set onCloseRequest event handler to refresh the list of sponsors when the form is closed
            stage.setOnCloseRequest(e -> {
                // Refresh the list of sponsors
                showEvents();
            });

            // Show the stage and wait for it to be closed
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }





    }




    //PYTHONNNNNN
    @FXML
    private void goToRecommendedEvent(MouseEvent event) {
        try {
            // Execute the Python script to get recommended event IDs
            ProcessBuilder pb = new ProcessBuilder("python", "src/main/resources/recsystem.py");
            Process p = pb.start();
            p.waitFor();

            // Read recommended event IDs from the file
            List<String> recommendedEventIds = Files.readAllLines(Paths.get("src/main/resources/recommended_event_ids.txt"), StandardCharsets.UTF_8);

            // Fetch event data for each recommended ID from the database
            List<Map<String, String>> eventsList = new ArrayList<>();
            for (String line : recommendedEventIds) {
                // Parse the line to extract the event ID and the days until the event
                String[] parts = line.replace("[", "").replace("]", "").split(",");
                String eventId = parts[0]; // The first part is the event ID
                String daysUntilEvent = parts[1]; // The second part is the days until the event

                // Fetch event details from the database using the extracted event ID
                Map<String, String> eventDetails = fetchEventDetailsFromDatabase(eventId);
                eventDetails.put("days_until_event", daysUntilEvent); // Add the days until the event to the event details

                eventsList.add(eventDetails);
            }

            // Display the events in a separate interface (REC)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eventfront/TRY.fxml"));
            Parent parent = loader.load();
            REC controller = loader.getController();
            controller.setEvents(eventsList);
            Stage stage = new Stage();
            stage.setTitle("Recommended Events");
            stage.setScene(new Scene(parent));




            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Implement this method to fetch event details from the database
    private Map<String, String> fetchEventDetailsFromDatabase(String eventId) {
        // Assuming you have a method to establish a connection to your database
        Connection conn = MyDatabase.getInstance().getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Map<String, String> eventDetails = new HashMap<>();

        try {
            // SQL query to fetch event details by ID
            String sql = "SELECT id, nameevent, datedebut, image FROM evenement WHERE id =?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, eventId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                eventDetails.put("id", rs.getString("id"));
                eventDetails.put("nameevent", rs.getString("nameevent"));
                eventDetails.put("datedebut", rs.getString("datedebut"));
                eventDetails.put("image", rs.getString("image"));
            }

            // Only add days_until_event if it's not an empty string
            String daysUntilEvent = readDaysUntilEventFromFile(eventId);
            if (!daysUntilEvent.isEmpty()) {
                eventDetails.put("days_until_event", daysUntilEvent);
            }

            // Print the fetched event details to the console
            System.out.println("Fetched Event Details:");
            System.out.println("ID: " + eventDetails.get("id"));
            System.out.println("Name Event: " + eventDetails.get("nameevent"));
            System.out.println("Date Debut: " + eventDetails.get("datedebut"));
            System.out.println("Image: " + eventDetails.get("image"));
            if (eventDetails.containsKey("days_until_event")) {
                System.out.println("Days Until Event: " + eventDetails.get("days_until_event"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs!= null) rs.close();
                if (stmt!= null) stmt.close();
                if (conn!= null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return eventDetails;
    }
    // New method to read days_until_event from fich.txt
    private String readDaysUntilEventFromFile(String eventId) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("src/main/resources/recommended_event_ids.txt"), StandardCharsets.UTF_8);
            for (String line : lines) {
                // Remove the square brackets and split by comma to get the ID and days until event
                String[] parts = line.replace("[", "").replace("]", "").split(",");
                if (parts[0].equals(eventId)) {
                    // Assuming the second element is the days until event
                    return parts[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ""; // Return an empty string if not found
    }
}





