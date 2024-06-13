package Controllers.event;

import Controllers.event.Listsponsors;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class Sidebar {

    @FXML
    private Pane content_area;
    @FXML

    private String authenticatedEmail;
    @FXML
    private void handleEvenementsClick() {
        try {
            // Load the FXML file containing the list of events interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eventback/listEvback.fxml"));
            Parent listEventsView = loader.load();

            // Clear the content area before loading the new view
            content_area.getChildren().clear();

            // Add the new view to the content area
            content_area.getChildren().add(listEventsView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handlesponClick() {
        try {
            // Load the FXML file containing the list of events interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eventback/Listsponsors.fxml"));
            Parent listEventsView = loader.load();
            Listsponsors controller = loader.getController();
            controller.showw();
            // Clear the content area before loading the new view
            content_area.getChildren().clear();

            // Add the new view to the content area
            content_area.getChildren().add(listEventsView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setAuthenticatedEmail(String email) {
        this.authenticatedEmail = email;
    }

    @FXML
    private void openArticleList(MouseEvent event) throws IOException {
    }


    @FXML
    public void openUserList(MouseEvent mouseEvent) throws IOException {
    }

    @FXML
    public void UpdateUser(MouseEvent mouseEvent) throws IOException {

    }

    @FXML
    void logoutclicked(MouseEvent mouseEvent) {

    }
}



