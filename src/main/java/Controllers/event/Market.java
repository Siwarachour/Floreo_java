package Controllers.event;
import Controllers.UserController.UserSession;
import entities.Evenement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import services.Scalendar;
import services.ServiceEvenement;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import services.UserService;
import utils.DatabaseConnection;
import utils.MyDatabase;

import static javafx.application.Application.launch;
import static services.UserService.roles;

public class Market  implements Initializable {
    @FXML
    private Button participateButton;
    @FXML
    private ImageView dislikeCountL;

    @FXML
    private ImageView likeCountabel;

    @FXML
    private Label dislikeCountLabel;

    @FXML
    private Label likeCountLabel;
    private int likeCount = 0;
    private int dislikeCount = 0;
    @FXML
    private VBox chosenEventCard;

    @FXML
    private Label eventNameLabel;

    @FXML
    private Label eventTypeLabel;

    @FXML
    private Label eventDateLabel1;
    @FXML
    private Label eventDateLabel2;
    @FXML
    private Label eventSponsorLabel;

    @FXML
    private Label eventDescriptionLabel;
    @FXML
    private Label eventID;
    @FXML
    private Label eventLocationLabel;
    @FXML
    private Label nbpartc;

    @FXML
    private ImageView eventImg;
    @FXML
    private ImageView qrCodeImageView;
    @FXML
    private ScrollPane scroll;

    @FXML
    private GridPane grid;

    private List<Evenement> evenements = new ArrayList<>();
    private Image image;
    private Controllers.event.Mylistener Mylistener;

    private ServiceEvenement serviceEvenement = new ServiceEvenement();
    private int selectedEventId; // Variable to store the selected event ID
    private Mylistener ylistener;
    // private User currentUser;
    private Scalendar scalendarService;

    @FXML
    private TextField searchTextField;

    @FXML
    private TextField typeFilterTextField;

    @FXML
    private TextField locationFilterTextField;

    @FXML
    private DatePicker startDateFilterDatePicker;

    @FXML
    private Button j;

    //PYTHONNNNNN
    /*
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
    }*/
//INITIALIZERRRR FOR SEARCH AND DISPLAYING EVENTS F OUST EKEE SHSSMO
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate grid with initial data
        populateGrid();

        // Attach event listener to the searchTextField
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchEvents(newValue);
        });

        // Fetch data from the database
        evenements.addAll(getDataFromDatabase());

        // Set listener for event clicks
        ylistener = evenement -> setChosenEvent(evenement);

        // Display events in the grid
        displayEventsInGrid();
    }





    private void populateGrid() {
        // Your existing code to populate the grid with initial data
    }

    private void setChosenEvent(Evenement evenement) {
        selectedEventId = evenement.getId();
        // Show the participate button and like/dislike icons
        setEventSelected(true);
        // Event Name
        eventNameLabel.setText("Event: " + evenement.getNameevent());

        eventNameLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 24; -fx-font-weight: bold;");


        // Event Type
        eventTypeLabel.setText("Type: " + evenement.getType());
        eventTypeLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18;");
        eventTypeLabel.setGraphic(createIcon("/imgs/type.png"));

        // Start Date
        eventDateLabel1.setText("Start Date: " + evenement.getDatedebut());
        eventDateLabel1.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18;");
        eventDateLabel1.setGraphic(createIcon("/imgs/calendar.png"));

        // End Date
        eventDateLabel2.setText("End Date: " + evenement.getDatefin());
        eventDateLabel2.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18;");
        eventDateLabel2.setGraphic(createIcon("/imgs/calendar.png"));

        // Sponsor
        eventSponsorLabel.setText("Sponsor: " + evenement.getSponsor().getName());
        eventSponsorLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18;");
        eventSponsorLabel.setGraphic(createIcon("/imgs/sponsor.png"));

        // Description
        eventDescriptionLabel.setText("Description: " + evenement.getDescription());
        eventDescriptionLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18;");
        eventDescriptionLabel.setGraphic(createIcon("/imgs/type.png"));

        // Location
        eventLocationLabel.setText("Location: " + evenement.getLieu());
        eventLocationLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18;");
        eventLocationLabel.setGraphic(createIcon("/imgs/location.png"));

        // Number of Participants
        nbpartc.setText("Participants: " + evenement.getNbparticipant());
        nbpartc.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18;");
        nbpartc.setGraphic(createIcon("/imgs/ancestors.png"));

        // Load event image
        Image image = new Image("file:" + evenement.getImage());
        eventImg.setImage(image);

        // Set alignment for labels


    }

    private Node createIcon(String imagePath) {
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        icon.setFitWidth(16); // Set width of the icon
        icon.setFitHeight(16); // Set height of the icon
        return icon;
    }







    //RECOMMMM STREAAAAAAAAAAAAM
    private List<Evenement> getDataFromDatabase() {
        return serviceEvenement.afficherEVWithSponsors();
    }

    @FXML
    private void goToRecommendedEvents(MouseEvent event) {
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

//NHEZZ FID FOR LIKE O DISLIKE
    private void setEventSelected(boolean isSelected) {
        participateButton.setVisible(isSelected);
        likeCountabel.setVisible(isSelected);
        dislikeCountL.setVisible(isSelected);
    }



//PARTICIPERRRRRRRRRRRRRRRRR





    // Assuming this is part of a controller class
    @FXML
    private void participate(ActionEvent event) {
        int eventId = selectedEventId;
        // Retrieve the authenticated user's email and role
        String email = UserSession.getInstance().getAuthenticatedEmail();
        String role = UserSession.getInstance().getUserRole();

        // Directly call fetchUserIdByEmail since it's in the same class
        try {
            int userId = fetchUserIdByEmail(email);
            System.out.println("User ID: " + userId);
        } catch (RuntimeException e) {
            System.err.println("Error fetching user ID: " + e.getMessage());
        }

        try {
            int userId = fetchUserIdByEmail(email);
            // Assuming serviceEvenement is correctly initialized and accessible
            serviceEvenement.updateParticipantsCount(eventId);
            serviceEvenement.addUserToEvent(userId, eventId);

            System.out.println("Participation recorded for event ID: " + eventId + " and user ID: " + userId);
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Participation Confirmed");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("You have successfully participated in the event!");
            infoAlert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Could not update participation");
            alert.setContentText("An error occurred: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // The fetchUserIdByEmail method remains unchanged
    private static final String FETCH_USER_ID_BY_EMAIL_SQL = "SELECT id FROM user WHERE email =?";

    public int fetchUserIdByEmail(String email) {
        int userId = -1; // Default value, should be replaced with actual logic to fetch user ID
        try (Connection conn = MyDatabase.getInstance().getConnection(); // Assuming DatabaseConnection has a getInstance method
             PreparedStatement statement = conn.prepareStatement(FETCH_USER_ID_BY_EMAIL_SQL)) {

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                userId = resultSet.getInt("id"); // Assuming "id" is the column name for the user ID in your users table
            } else {
                System.err.println("User not found with email: " + email);
                throw new RuntimeException("User not found");
            }
        } catch (SQLException ex) {
            System.err.println("Error fetching user ID: " + ex.getMessage());
            throw new RuntimeException("Failed to fetch user ID", ex);
        }
        return userId;
    }






   /* @FXML
    private void participate(ActionEvent event) {
        int eventId = selectedEventId;
        int userId = 1;
        try {
            serviceEvenement.updateParticipantsCount(eventId);
            serviceEvenement.addUserToEvent(userId, eventId);

            System.out.println("Participation recorded for event ID: " + eventId + " and user ID: " + userId);
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Participation Confirmed");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("You have successfully participated in the event!");
            infoAlert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Could not update participation");
            alert.setContentText("An error occurred: " + e.getMessage());
            alert.showAndWait();
        }
    }*/

//LIKEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE

    @FXML
    private void initialize() {
        // Set initial counts
        updateLikeCount();
        updateDislikeCount();

        // Add event handlers
        likeCountabel.setOnMouseClicked(event -> like());
        dislikeCountL.setOnMouseClicked(event -> dislike());
    }

    @FXML
    private void like() {
        likeCount++;
        updateLikeCount();
    }

    @FXML
    private void dislike() {
        dislikeCount++;
        updateDislikeCount();
    }

    private void updateLikeCount() {
        likeCountLabel.setText(String.valueOf(likeCount));
    }

    private void updateDislikeCount() {
        dislikeCountLabel.setText(String.valueOf(dislikeCount));
    }




//SEARCH STREAMMMMMMMMMMMMMMMMMMMMMM


    private void searchEvents(String keyword) {
        // Filter events based on the search keyword
        List<Evenement> searchResults = evenements.stream()
                .filter(event -> event.getNameevent().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());

        // Update the grid with the search results
        displayEventsInGrid(searchResults);
    }

    private void displayEventsInGrid(List<Evenement> events) {
        // Clear existing content in the grid
        grid.getChildren().clear();

        // Populate the grid with events
        int column = 0;
        int row = 1;
        try {
            for (Evenement evenement : events) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/eventfront/card1.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                Card1 itemController = fxmlLoader.getController();
                itemController.setData(evenement, ylistener);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayEventsInGrid() {
        // Clear existing content in the grid
        grid.getChildren().clear();

        // Populate the grid with events
        int column = 0;
        int row = 1;
        try {
            for (Evenement evenement : evenements) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/eventfront/card1.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                Card1 itemController = fxmlLoader.getController();
                itemController.setData(evenement, Mylistener);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//FILTRRRRRRRRRRRRRRRRRRRRRRR

    @FXML
    private void applyFilters(ActionEvent event) {
        String typeFilter = typeFilterTextField.getText().trim().toLowerCase();
        String locationFilter = locationFilterTextField.getText().trim().toLowerCase();
        LocalDate startDateFilter = startDateFilterDatePicker.getValue();

        // Filter the event list based on the criteria
        List<Evenement> filteredEvents = evenements.stream()
                .filter(evenement -> evenement.getType().toLowerCase().contains(typeFilter))
                .filter(evenement -> evenement.getLieu().toLowerCase().contains(locationFilter))
                .filter(evenement -> startDateFilter == null ||
                        LocalDate.parse(evenement.getDatedebut()).isAfter(startDateFilter))
                .collect(Collectors.toList());

        // Update the displayed events with the filtered list
        updateDisplayedEvents(filteredEvents);
    }

    private void updateDisplayedEvents(List<Evenement> events) {
        grid.getChildren().clear(); // Clear existing display

        // Populate the grid with the filtered events
        int column = 0;
        int row = 1;
        try {
            for (Evenement evenement : events) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/eventfront/card1.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                Card1 itemController = fxmlLoader.getController();
                itemController.setData(evenement, ylistener);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}





    //EKHR MERAAA SINON BEDLO
    //
    //
    //
    //
    //private Evenement evenement;
   //
    /*@FXML
    private void participate(MouseEvent mouseEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/eventfront/details.fxml"));
        Parent root;
        try {
            root = loader.load();
            // Get the controller of the loaded FXML
            Details controller = loader.getController();
            // Pass the selected event details to the controller
            controller.setEvent(evenement);

            // Create a new scene with the details interface
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/















/*
    private List<Evenement> getDataFromDatabase() {
        return serviceEvenement.afficherEVWithSponsors();
    }
    private void setChosenEvent(Evenement evenement) {
        selectedEventId = evenement.getId();
        System.out.println("Selected Event ID: " + selectedEventId);
        eventNameLabel.setText(evenement.getNameevent());
        // Retrieve sponsor name directly from the database using the sponsor's ID
        Sponsor sponsor = evenement.getSponsor();
        if (sponsor != null) {
            eventSponsorLabel.setText("Sponsored By: " + sponsor.getName());
        } else {
            eventSponsorLabel.setText("Sponsor: Not available");
        }
        eventTypeLabel.setText("Type: " + evenement.getType());
        eventDateLabel1.setText("Start date: " + evenement.getDatedebut());
        eventDateLabel2.setText("End date: " + evenement.getDatefin());
      //aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa

        nbpartc.setText("nb: " + evenement.getNbparticipant());
        eventDescriptionLabel.setText("Description: " + evenement.getDescription());
        eventDescriptionLabel.setWrapText(true); // Enable text wrapping
        eventLocationLabel.setText("Location: " + evenement.getLieu());
        image = new Image("file:" + evenement.getImage());
        eventImg.setImage(image);
      //  chosenEventCard.setStyle("-fx-background-color: #" + evenement.getColor() + ";\n" +
        //        "    -fx-background-radius: 30;");
    }




    private void populateGrid() {
        // Your existing code to populate the grid with initial data
    }





    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateGrid();

        // Attach event listener to the searchTextField
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchEvents(newValue);
        });
        evenements.addAll(getDataFromDatabase());
        if (!evenements.isEmpty()) {
            setChosenEvent(evenements.get(0));
            Mylistener = new ylistener() {
                @Override
                public void onClickListener(Evenement evenement) {
                    setChosenEvent(evenement);
                   // Set the selected event
                }
            };
        }
        int column = 0;
        int row = 1;
        try {
            for (Evenement evenement : evenements) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/eventfront/card1.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                Card1 itemController = fxmlLoader.getController();
                itemController.setData(evenement, Mylistener);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void applyFilters(ActionEvent event) {
        String typeFilter = typeFilterTextField.getText().trim().toLowerCase();
        String locationFilter = locationFilterTextField.getText().trim().toLowerCase();
        LocalDate startDateFilter = startDateFilterDatePicker.getValue();

        // Filter the event list based on the criteria
        List<Evenement> filteredEvents = evenements.stream()
                .filter(evenement -> evenement.getType().toLowerCase().contains(typeFilter))
                .filter(evenement -> evenement.getLieu().toLowerCase().contains(locationFilter))
                .filter(evenement -> startDateFilter == null ||
                        LocalDate.parse(evenement.getDatedebut()).isAfter(startDateFilter))
                .collect(Collectors.toList());

        // Update the displayed events with the filtered list
        updateDisplayedEvents(filteredEvents);
    }

    private void updateDisplayedEvents(List<Evenement> events) {
        grid.getChildren().clear(); // Clear existing display

        // Populate the grid with the filtered events
        int column = 0;
        int row = 1;
        try {
            for (Evenement evenement : events) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/eventfront/card1.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                Card1 itemController = fxmlLoader.getController();
                itemController.setData(evenement, mylistener);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void searchEvents(String keyword) {
        List<Evenement> searchResults = evenements.stream()
                .filter(event -> event.getNameevent().toLowerCase().contains(keyword.toLowerCase())
                        || event.getLieu().toLowerCase().contains(keyword.toLowerCase())
                        || event.getType().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());

        // Clear previous content in the grid
        grid.getChildren().clear();

        int column = 0;
        int row = 1;
        try {
            for (Evenement evenement : searchResults) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/eventfront/card1.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                Card1 itemController = fxmlLoader.getController();
                itemController.setData(evenement, Mylistener);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




*/









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
    }*/
/*
    @FXML
    private void showCalendar() {
        try {
            List<Event> googleEvents = scalendarService.fetchEventsFromEvenementEntity();
            if (googleEvents.isEmpty()) {
                System.out.println("No events found.");
                return; // Exit early if no events are found
            }

            System.out.println("Displaying events...");
            for (Event event : googleEvents) {
                System.out.println(event.getSummary());
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch events: " + e.getMessage());
            e.printStackTrace();
        }
    }*/




    /* int userId = 1;
    @FXML
    private void addToCartButtonClicked(ActionEvent event) {
        if (selectedEventId != -1 && userId != 0) {
            serviceEvenement.addUserToEvent(userId, selectedEventId);
            showAlert("You have successfully participated in the event!");
            // Update the UI or take further actions as needed
        } else {
            showAlert("Please select an event first!");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }*/

    //   Make sure to adjust the User entity according to your application's structure and to set the currentUser appropriately when a user logs in. This controller








//    private int getUserId() {
    // Implement this method to retrieve the user ID
    //      return 1; // Placeholder value
    // }




    /*@FXML
    private void generateQRCode(ActionEvent event) {
        String nom = eventNameLabel.getText();
        String date = eventDateLabel1.getText();
        String datefin = eventDateLabel2.getText();
        String lieu = eventTypeLabel.getText();

        // Example value for demonstration purpose, replace with actual value if necessary
        int nbParticipant = 100;
        System.out.println("Nom: " + nom);
        System.out.println("Date: " + date);
        System.out.println("Date de fin: " + datefin);
        System.out.println("Lieu: " + lieu);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        String information = "Nom: " + nom + "\n" + "Lieu: " + lieu + "\n" + "Date: " + date + "\n" + "Nombre de participants restant: " + nbParticipant + "\n" + "Date de fin: " + datefin;
        int width = 300; // Adjust width and height as needed
        int height = 300;

        BufferedImage bufferedImage = null;
        try {
            BitMatrix byteMatrix = qrCodeWriter.encode(information, BarcodeFormat.QR_CODE, width, height);
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    bufferedImage.setRGB(i, j, byteMatrix.get(i, j) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }
            qrCodeImageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        } catch (WriterException ex) {
            ex.printStackTrace();
        }
    }*/


   /* @FXML
    private void participate(ActionEvent event) {
        // Use the stored selected event ID
        int eventId = selectedEventId;
        int userId = 1; // Replace with the actual user ID
        // Use the stored selected event ID

        // Call the addUserToEvent function
        ServiceEvenement serviceEvenement = new ServiceEvenement();
        serviceEvenement.updateParticipantsCount( eventId);

        // Add any further logic after participating in the event
    }
*/

