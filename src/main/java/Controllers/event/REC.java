package Controllers.event;

import entities.Evenement;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import services.ServiceEvenement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class REC {

    @FXML
    private HBox recommendedEventsHBox;
    private List<Evenement> evenements = new ArrayList<>();
    private Image image;
    private Controllers.event.Mylistener Mylistener;

    private ServiceEvenement serviceEvenement = new ServiceEvenement();


    private List<Evenement> getDataFromDatabase() {
        return serviceEvenement.afficherEVWithSponsors();
    }






    // Method to populate recommended events
    public void populateRecommendedEvents() {
        // Get recommended events based on proximity to current date
        try {
            // Get recommended events based on proximity to current date
            List<Evenement> recommendedEvents = serviceEvenement.recommendEventsByProximity(5); // Change 5 to the desired number of recommendations

            // Clear existing children of the VBox
            recommendedEventsHBox.getChildren().clear();

            // Iterate through recommended events and create custom cards
            for (Evenement recommendedEvent : recommendedEvents) {
                // Create a VBox to hold the attributes of the event
                VBox eventCard = new VBox();
              eventCard.getStyleClass().add("event-card"); // Add CSS class for styling
                ImageView imageView = new ImageView();
                String imagePath = recommendedEvent.getImage();

// Load the image from the file path
                File file = new File(imagePath);
                Image image = new Image(file.toURI().toString());

// Set the image to the image view
                imageView.setImage(image);
// Set the width and height of the image view
                imageView.setFitWidth(100); // Set your desired width
                imageView.setFitHeight(100); // Set your desired height

// Add the image view to the event card
                eventCard.getChildren().add(imageView);


               // Set the height of the image
                // Create labels for each attribute of the event
                Label nameLabel = new Label("Name: " + recommendedEvent.getNameevent());
                Label typeLabel = new Label("Type: " + recommendedEvent.getType());
                Label startDateLabel = new Label("Start Date: " + recommendedEvent.getDatedebut());
                Label endDateLabel = new Label("End Date: " + recommendedEvent.getDatefin());
                Label descriptionLabel = new Label("Description: " + recommendedEvent.getDescription());
                Label participantsLabel = new Label("Participants: " + recommendedEvent.getNbparticipant());
                Label locationLabel = new Label("Location: " + recommendedEvent.getLieu());

                // Add labels to the event card
                eventCard.getChildren().addAll(nameLabel, typeLabel, startDateLabel, endDateLabel, descriptionLabel, participantsLabel, locationLabel);

                // Add the event card to the VBox
                recommendedEventsHBox.getChildren().add(eventCard);
            }
        } catch (Exception e) {
            // Log any exceptions
            e.printStackTrace();
        }
    }

@FXML
private HBox eventDetailsContainer;
    @FXML
    private HBox allEventDetailsVBox;
    private VBox createEventDetailsVBox(Map<String, String> event) {
        VBox eventVBox = new VBox(15); // Spacing between children
        //eventVBox.setAlignment(Pos.CENTER);

        AnchorPane.setTopAnchor(allEventDetailsVBox, 0.0);
        AnchorPane.setRightAnchor(allEventDetailsVBox, 0.0);
        AnchorPane.setBottomAnchor(allEventDetailsVBox, 0.0);
        AnchorPane.setLeftAnchor(allEventDetailsVBox, 0.0);
        eventVBox.getStyleClass().add("event-cardd"); // Apply the.event-card style
        // Populate the VBox with labels
        Label idLabel = new Label("ID: ");
        Label nameEventLabel = new Label("Name Event: ");
        Label dateDebutLabel = new Label("Date Debut: ");
        Label imageLabel = new Label("Image: ");
        Label daysUntilEventLabel = new Label("Days Until Event: ");

        // Assuming you have methods to update these labels
        idLabel.setText(event.get("id"));
        nameEventLabel.setText(event.get("nameevent"));
        dateDebutLabel.setText(event.get("datedebut"));
        imageLabel.setText(event.get("image")); // Or load and display the image
     // Replace with actual logic
        // Set the text for daysUntilEventLabel based on the event data
        String daysUntilEvent = event.get("days_until_event");
        if (daysUntilEvent!= null &&!daysUntilEvent.isEmpty()) {
            daysUntilEventLabel.setText(daysUntilEvent);
        } else {
            daysUntilEventLabel.setText("N/A"); // Default text if no days_until_event is available
        }

        daysUntilEventLabel.setText(event.get("days_until_event"));  // Replace with actual logic


        // Check if the image path is not null before attempting to read the image
        if (event.get("image")!= null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(new File(event.get("image")));
                Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
                ImageView imageView = new ImageView(fxImage);

                // Set the ImageView size to a fixed value
                imageView.setFitWidth(100); // Width
                imageView.setFitHeight(100); // Height

                // Add the ImageView to the VBox
                eventVBox.getChildren().addAll(nameEventLabel, dateDebutLabel, imageView, daysUntilEventLabel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Handle the case where the image path is null
            System.out.println("No image found for event ID: " + event.get("id"));
        }


        return eventVBox;}
    public void setEvents(List<Map<String, String>> eventsList) {
        HBox allEventDetailsVBox = (HBox) eventDetailsContainer.getChildren().get(0);// Get the container for all event details

        for (Map<String, String> event : eventsList) {
            VBox eventVBox = createEventDetailsVBox(event);
            allEventDetailsVBox.getChildren().add(eventVBox);
        }
    }
}