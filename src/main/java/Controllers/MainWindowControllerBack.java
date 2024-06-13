package Controllers;

import utils.Animations;
import utils.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainWindowControllerBack implements Initializable {

    static AnchorPane staticContent;
    private static MainWindowControllerBack instance;

    private final Color COLOR_GRAY = new Color(0.9, 0.9, 0.9, 1);
    private final Color COLOR_PRIMARY = Color.web("#88B44E");
    private final Color COLOR_DARK = new Color(1, 1, 1, 0.65);

    private Button[] liens;

    @FXML
    private AnchorPane content;

    @FXML
    private Button btnProduits;
    @FXML
    private Button btnCommandes;

    public static MainWindowControllerBack getInstance() {
        if (instance == null) {
            instance = new MainWindowControllerBack();
        }
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        staticContent = content;

        liens = new Button[]{
                btnProduits,
                btnCommandes,
        };

        for (Button lien : liens) {
            lien.setTextFill(COLOR_DARK);
            lien.setBackground(new Background(new BackgroundFill(COLOR_PRIMARY, CornerRadii.EMPTY, Insets.EMPTY)));
            Animations.animateButton(lien, COLOR_GRAY, Color.WHITE, COLOR_PRIMARY, 0, false);
        }
        btnProduits.setTextFill(Color.WHITE);
        btnCommandes.setTextFill(Color.WHITE);

        loadInterface(Constants.FXML_BACK_HOME);
    }


    @FXML
    private void afficherProduits(ActionEvent ignored) {
        goToLink(Constants.FXML_BACK_DISPLAY_ALL_PRODUIT);

        btnProduits.setTextFill(COLOR_PRIMARY);
        Animations.animateButton(btnProduits, COLOR_GRAY, Color.WHITE, COLOR_PRIMARY, 0, false);
    }

    @FXML
    private void afficherCommandes(ActionEvent ignored) {
        goToLink(Constants.FXML_BACK_DISPLAY_ALL_COMMANDE);

        btnCommandes.setTextFill(COLOR_PRIMARY);
        Animations.animateButton(btnCommandes, COLOR_GRAY, Color.WHITE, COLOR_PRIMARY, 0, false);
    }


    private void goToLink(String fxmlLink) {
        for (Button lien : liens) {
            lien.setTextFill(COLOR_DARK);
            Animations.animateButton(lien, COLOR_GRAY, COLOR_DARK, COLOR_PRIMARY, 0, false);
        }
        loadInterface(fxmlLink);
    }

    public void loadInterface(String location) {
        staticContent.getChildren().clear();
        if (getClass().getResource(location) == null) {
            System.out.println("Could not load FXML check the path");
        } else {
            try {
                Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(location)));
                AnchorPane.setTopAnchor(parent, 0.0);
                AnchorPane.setBottomAnchor(parent, 0.0);
                AnchorPane.setRightAnchor(parent, 0.0);
                AnchorPane.setLeftAnchor(parent, 0.0);
                staticContent.getChildren().add(parent);
            } catch (IOException e) {
                System.out.println("Could not load FXML : " + e.getMessage() + " check your controller");
                e.printStackTrace();
            }
        }
    }
}
