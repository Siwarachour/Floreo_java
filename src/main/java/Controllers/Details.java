package Controllers;
import entities.Evenement;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import services.ServiceEvenement;

public class Details {
//dossier zeyed




    @FXML
    private Label nameLabel;

    @FXML
    private Label typeLabel;

    @FXML
    private Label placeLabel;

    @FXML
    private Button participateButton;

    private Evenement evenement;

    public void setEvent(Evenement evenement) {
        this.evenement = evenement;

        typeLabel.setText("Type: " + evenement.getType());
        placeLabel.setText("Location: " + evenement.getLieu());
    }

    @FXML
    private void participateInEvent() {
        int userId = 1; // Replace with actual userId
        int eventId = evenement.getId();
        ServiceEvenement serviceEvenement = new ServiceEvenement();
        serviceEvenement.updateParticipantsCount( eventId);
        // Add any further logic after participating in the event
    }
}