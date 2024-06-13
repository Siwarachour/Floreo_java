/*package Controllers.factureDon;

import Controllers.UserController.UserSession;
import MainJavaFx.MainApp;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import entities.Don;
import entities.FactureDon;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.FactureDonService;
import utils.AlertUtils;
import utils.Constants;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ManageController implements Initializable {

    @FXML
    public TextField nomDonateurTF;

    @FXML
    public TextField montant;
    @FXML
    public TextField prenomDonateurTF;
    @FXML
    public TextField emailTF;
    @FXML
    public TextField adressesTF;
    @FXML
    public TextField numeroTelephoneTF;
    @FXML
    public TextField descriptionTF;
    @FXML
    public ComboBox<Don> donCB;
    @FXML
    public Button btnAjout;
    @FXML
    public Text topText;

    private FactureDon currentFactureDon;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String authenticatedEmail = UserSession.getInstance().getAuthenticatedEmail();
        emailTF.setText(authenticatedEmail);

        montant.setVisible(false);
        for (Don don : FactureDonService.getInstance().getAllDons()) {
            donCB.getItems().add(don);
        }

        donCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && (newValue.getType().equals("argent") || newValue.getType().equals("money"))) {
                montant.setVisible(true);
            } else {
                montant.setVisible(false);
            }
        });

        currentFactureDon = ShowAllController.currentFactureDon;

        if (currentFactureDon != null) {
            topText.setText("Modifier factureDon");
            btnAjout.setText("Modifier");

            try {
                nomDonateurTF.setText(currentFactureDon.getNomDonateur());
                prenomDonateurTF.setText(currentFactureDon.getPrenomDonateur());
                emailTF.setText(currentFactureDon.getEmail());
                adressesTF.setText(currentFactureDon.getAdresses());
                numeroTelephoneTF.setText(String.valueOf(currentFactureDon.getNumeroTelephone()));
                descriptionTF.setText(currentFactureDon.getDescription());

                donCB.setValue(currentFactureDon.getDon());

            } catch (NullPointerException ignored) {
                System.out.println("NullPointerException");
            }
        } else {
            topText.setText("Ajouter factureDon");
            btnAjout.setText("Ajouter");
        }
    }

    @FXML
    private void manage(ActionEvent ignored) {
        if (controleDeSaisie()) {
            FactureDon factureDon = new FactureDon();
            factureDon.setNomDonateur(nomDonateurTF.getText());
            factureDon.setPrenomDonateur(prenomDonateurTF.getText());
            factureDon.setEmail(emailTF.getText());
            factureDon.setAdresses(adressesTF.getText());
            factureDon.setNumeroTelephone(Integer.parseInt(numeroTelephoneTF.getText()));
            factureDon.setDescription(descriptionTF.getText());
            factureDon.setDon(donCB.getValue());

            if (currentFactureDon == null) {
                if (FactureDonService.getInstance().add(factureDon)) {
                    if (montant.isVisible() && !montant.getText().isEmpty()) {
                        // Convert montantTF text to an integer
                        int amount = Integer.parseInt(montant.getText());
                        // Process payment with the amount
                        processPayment(amount);
                    }
                    AlertUtils.makeSuccessNotification("FactureDon ajouté avec succès");
                    showNotification("Facture added","secessfully");
                    // MainApp.getInstance().loadLogin();
                } else {
                    AlertUtils.makeError("Error");
                }
            } else {
                factureDon.setId(currentFactureDon.getId());
                if (FactureDonService.getInstance().edit(factureDon)) {
                    AlertUtils.makeSuccessNotification("FactureDon modifié avec succès");
                    ShowAllController.currentFactureDon = null;
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_FRONT_DISPLAY_ALL_FACTURE_DON));
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
                    AlertUtils.makeError("Error");
                }
            }
        }
    }

    private boolean controleDeSaisie() {
        if (nomDonateurTF.getText().isEmpty()) {
            AlertUtils.makeInformation("nomDonateur ne doit pas être vide");
            return false;
        }

        if (prenomDonateurTF.getText().isEmpty()) {
            AlertUtils.makeInformation("prenomDonateur ne doit pas être vide");
            return false;
        }

        if (emailTF.getText().isEmpty()) {
            AlertUtils.makeInformation("email ne doit pas être vide");
            return false;
        }
        if (!Pattern.compile("^(.+)@(.+)$").matcher(emailTF.getText()).matches()) {
            AlertUtils.makeInformation("Email invalide");
            return false;
        }

        if (adressesTF.getText().isEmpty()) {
            AlertUtils.makeInformation("adresses ne doit pas être vide");
            return false;
        }

        if (numeroTelephoneTF.getText().isEmpty()) {
            AlertUtils.makeInformation("numeroTelephone ne doit pas être vide");
            return false;
        }

        try {
            Integer.parseInt(numeroTelephoneTF.getText());
        } catch (NumberFormatException ignored) {
            AlertUtils.makeInformation("numeroTelephone doit être un nombre");
            return false;
        }

        if (descriptionTF.getText().isEmpty()) {
            AlertUtils.makeInformation("description ne doit pas être vide");
            return false;
        }

        if (donCB.getValue() == null) {
            AlertUtils.makeInformation("Veuillez choisir un don");
            return false;
        }
        return true;
    }
    private void showNotification(String title, String content) {
        Notifications notification =Notifications.create()
                .title(title)
                .text(content);

        Platform.runLater(() -> notification.showInformation());
    }

    public void processPayment(int amount) {
        try {
          //  Stripe.apiKey = "";
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) amount * 100) // Amount should be in cents
                    .setCurrency("usd")
                    .addPaymentMethodType("card")
                    .build();
            PaymentIntent intent = PaymentIntent.create(params);
            System.out.println("PaymentIntent created. Client Secret: " + intent.getClientSecret());
        } catch (StripeException e) {
            System.out.println("Payment failed. Error: " + e.getMessage());
        }
    }

}
*/
