package Controllers.back.reponse;


import entities.Reclamation;
import entities.Reponse;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.ReponseService;
import utils.AlertUtils;
import utils.BadWords;
import utils.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Properties;
import java.util.ResourceBundle;

public class ManageController implements Initializable {

    @FXML
    public TextField descriptionTF;
    @FXML
    public Button btnAjout;
    @FXML
    public Text topText;

    Reponse currentReponse;

    public static Reclamation selectedReclamation;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        currentReponse = ShowAllController.currentReponse;

        if (currentReponse != null) {
            topText.setText("Modifier reponse");
            btnAjout.setText("Modifier");

            try {
                descriptionTF.setText(currentReponse.getDescription());
            } catch (NullPointerException ignored) {
                System.out.println("NullPointerException");
            }
        } else {
            topText.setText("Ajouter reponse");
            btnAjout.setText("Ajouter");
        }
    }

    @FXML
    private void manage(ActionEvent ignored) {

        if (controleDeSaisie()) {

            Reponse reponse = new Reponse();
            reponse.setDescription(descriptionTF.getText());
            reponse.setDatemodif(LocalDate.now());
            reponse.setReclamation(selectedReclamation);

            if (currentReponse == null) {
                reponse.setDateajout(LocalDate.now());

                if (ReponseService.getInstance().add(reponse)) {
                    try {
                        sendMail("grimhype97@gmail.com");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    AlertUtils.successNotification("Reponse ajouté avec succés");
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_DISPLAY_ALL_REPONSE));
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
                    AlertUtils.errorNotification("Error");
                }
            } else {
                reponse.setId(currentReponse.getId());
                if (ReponseService.getInstance().edit(reponse)) {
                    AlertUtils.successNotification("Reponse modifié avec succés");
                    ShowAllController.currentReponse = null;
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_DISPLAY_ALL_REPONSE));
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
                    AlertUtils.errorNotification("Error");
                }
            }

        }
    }


    private boolean controleDeSaisie() {
        if (descriptionTF.getText().isEmpty()) {
            AlertUtils.informationNotification("description ne doit pas etre vide");
            return false;
        }

        if (BadWords.filterText(descriptionTF.getText())) {
            AlertUtils.informationNotification("description contient des mots interdits");
            return false;
        }

        return true;
    }

    public static void sendMail(String recipient) throws Exception {
        System.out.println("Preparing to send email");
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.zoho.com");
        properties.put("mail.smtp.port", "587");
        String myAccountEmail = "pidev.esprit@zohomail.com";
        String password = "tFS7s957HzVd";

        // Create a session with account credentials
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccountEmail, password);
            }
        });

        // Prepare email message
        Message message;
        try {
            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myAccountEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject("Réponse reclamation");
            message.setContent("Votre reclamation a été traitée avec succès", "text/html");
        } catch (MessagingException ex) {
            System.out.println("Error in sending email");
            ex.printStackTrace();
            return;
        }

        // Send mail
        Transport.send(message);
        System.out.println("Mail sent successfully");
    }

}