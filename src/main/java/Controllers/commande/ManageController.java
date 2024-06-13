
package Controllers.commande;


import Controllers.UserController.UserSession;
import entities.Commande;
import entities.Produit;
import entities.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.CommandeService;
import services.UserService;
import utils.AlertUtils;
import utils.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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

import static Controllers.UserController.HomeController.authenticatedEmail;

public class ManageController implements Initializable {

    //HELLO
    @FXML
    public TextField montantTF;
    @FXML
    public TextField lieucmdTF;
    @FXML
    public TextField quantiteTF;
    @FXML
    public ComboBox<Produit> produitCB;

    @FXML
    public Button btnAjout;
    @FXML
    public Text topText;
    public static User user;

    Commande currentCommande;

    public static Produit selectedProduit;
    // private  User user;
//public static User user;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String authenticatedEmail = UserSession.getInstance().getAuthenticatedEmail();

        //   currentCommande.setUser();
        System.out.println("commande" + user);

        for (Produit produit : CommandeService.getInstance().getAllProduits()) {
            produitCB.getItems().add(produit);
        }

        currentCommande = ShowAllController.currentCommande;

        produitCB.setValue(selectedProduit);

        if (selectedProduit == null) {
            montantTF.setText("0");
            quantiteTF.setText("0");
        } else {
            montantTF.setText(String.valueOf(selectedProduit.getPrix()));
            quantiteTF.setText("1");
        }


        produitCB.setOnAction(event -> {
            Produit produit = produitCB.getValue();

            if (produit == null) {
                montantTF.setText("0");
                return;
            }

            int quantite = 0;
            try {
                quantite = Integer.parseInt(quantiteTF.getText());
            } catch (NumberFormatException ignored) {
            }

            montantTF.setText(String.valueOf(produit.getPrix() * quantite));
        });

        quantiteTF.textProperty().addListener((observable, oldValue, newValue) -> {
            Produit produit = produitCB.getValue();

            if (produit == null) {
                montantTF.setText("0");
                return;
            }

            int quantite = 0;
            try {
                quantite = Integer.parseInt(quantiteTF.getText());
            } catch (NumberFormatException ignored) {
            }

            montantTF.setText(String.valueOf(produit.getPrix() * quantite));
        });

        if (currentCommande != null) {
            topText.setText("Modifier commande");
            btnAjout.setText("Modifier");

            try {
                montantTF.setText(String.valueOf(currentCommande.getMontant()));
                lieucmdTF.setText(currentCommande.getLieucmd());
                quantiteTF.setText(String.valueOf(currentCommande.getQuantite()));
                produitCB.setValue(currentCommande.getProduit());
            } catch (NullPointerException ignored) {
                System.out.println("NullPointerException");
            }
        } else {
            topText.setText("Ajouter commande");
            btnAjout.setText("Ajouter");
        }
    }

    @FXML
    private void manage(ActionEvent ignored) {
        if (controleDeSaisie()) {
            Commande commande = new Commande();
            commande.setMontant(Float.parseFloat(montantTF.getText()));
            commande.setDatecmd(LocalDate.now());
            commande.setLieucmd(lieucmdTF.getText());
            commande.setQuantite(Integer.parseInt(quantiteTF.getText()));
            commande.setProduit(produitCB.getValue());
            commande.setUser(user);

            if (currentCommande == null) {
                if (CommandeService.getInstance().add(commande)) {
                    AlertUtils.makeSuccessNotification("Commande ajouté avec succés");

                    try {
                        System.out.println(authenticatedEmail);
                        sendMail("dhia.bellakoud@esprit.tn");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_FRONT_DISPLAY_ALL_PRODUIT));
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
            } else {
                commande.setId(currentCommande.getId());
                if (CommandeService.getInstance().edit(commande)) {
                    AlertUtils.makeSuccessNotification("Commande modifié avec succés");
                    ShowAllController.currentCommande = null;
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_FRONT_DISPLAY_ALL_COMMANDE));
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
        if (montantTF.getText().isEmpty()) {
            AlertUtils.makeInformation("montant ne doit pas etre vide");
            return false;
        }

        try {
            Float.parseFloat(montantTF.getText());
        } catch (NumberFormatException ignored) {
            AlertUtils.makeInformation("montant doit etre un réel");
            return false;
        }

        if (lieucmdTF.getText().isEmpty()) {
            AlertUtils.makeInformation("lieucmd ne doit pas etre vide");
            return false;
        }

        if (quantiteTF.getText().isEmpty()) {
            AlertUtils.makeInformation("quantite ne doit pas etre vide");
            return false;
        }

        try {
            Integer.parseInt(quantiteTF.getText());
        } catch (NumberFormatException ignored) {
            AlertUtils.makeInformation("quantite doit etre un nombre");
            return false;
        }

        if (produitCB.getValue() == null) {
            AlertUtils.makeInformation("Veuillez choisir un produit");
            return false;
        }
        return true;
    }

    public static void sendMail(String recipient) throws Exception {
        System.out.println("Preparing to send email");
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com"); // Gmail SMTP server
        properties.put("mail.smtp.port", "587"); // Gmail SMTP port

        // Set up authentication
        String myAccountEmail = "dhiabellakoud22@gmail.com"; // Your Gmail address
        String password = "vuglgfpgvpoesgqw"; // Your Gmail password

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
            message.setSubject("Commande");
            message.setContent("Votre commande a été bien enregistrée", "text/html");
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
