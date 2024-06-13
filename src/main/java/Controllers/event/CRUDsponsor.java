package Controllers.event;

import entities.Sponsor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import services.ServiceSpon;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.SQLException;
import java.util.*;

public class CRUDsponsor {
    private ServiceSpon rcrd = new ServiceSpon();


    public ObservableList<Sponsor> data = FXCollections.observableArrayList();
    @FXML
    private TextField tfnom;

    @FXML
    private TextField emailField;

    @FXML
    private TextField numfield;

    @FXML
    private Button btnenvoyer;






    //EMAILLLLLL
    public void sendMail(Sponsor sponsor) {
        String recipient = sponsor.getEmail(); // Use the sponsor's email
        if (!isValidEmail(recipient)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("L'adresse e-mail saisie n'est pas valide.");
            alert.showAndWait();
            return;
        }

        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        Properties props = System.getProperties();
        final String username = "ikbelbenmansour4@gmail.com";
        final String password = "zxkt cbjg klyj cdlo";

        props.setProperty("mail.smtp.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.starttls.enable","true");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");
        props.put("mail.store.protocol", "pop3");
        props.put("mail.transport.protocol", "smtp");

        Session session = Session.getDefaultInstance(props,
                new Authenticator() {
                    @Override
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new javax.mail.PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject("Confirmation de réception de votre événement");
            message.setText("Cher sponsor,\n"

                    + sponsor.getName() // Use the sponsor's name
                    + "+ \"Nous vous remercions sincèrement pour votre soutien à notre événement. Votre contribution est essentielle pour nous permettre de poursuivre nos efforts visant à créer un environnement plus propre et durable. Ensemble, nous pouvons faire une différence significative. Nous sommes impatients de voir les résultats de notre collaboration et de continuer à travailler ensemble pour un meilleur avenir. N'hésitez pas à nous contacter si vous avez besoin d'aide ou si vous souhaitez discuter de nos projets futurs. \nCordialement,\nL'équipe du service client.");

            Transport.send(message);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("the email is sent to the sponsor successfully!");
            alert.showAndWait();
        } catch (MessagingException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors de l'envoi du message : " + e.getMessage());
            alert.showAndWait();
        }
    }
    @FXML
    void ajoutersponsor(ActionEvent event) {
        // Get the data from the input fields
        String nom = tfnom.getText().trim(); // Assuming tfnom is the TextField for sponsor name
        String email = emailField.getText().trim(); // Assuming emailField is the TextField for email
        String numeroText = numfield.getText().trim(); // Assuming numfield is the TextField for number

        // Validate input
        if (nom.isEmpty() || email.isEmpty() || numeroText.isEmpty()) {
            // Afficher un message d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Champs vides");
            alert.setHeaderText(null);
            alert.setContentText("Aucune de ces informations ne doit être vide. Veuillez remplir tous les champs.");
            alert.showAndWait();
            return;
        }
        // Validate name format
        if (!isValidName(nom)) {
            // Afficher un message d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Champs vides");
            alert.setHeaderText(null);
            alert.setContentText("Nom invalide, Le nom doit contenir uniquement des caractères et ne doit pas dépasser 10 caractères.");
            alert.showAndWait();
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            // Afficher un message d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Champs vides");
            alert.setHeaderText(null);
            alert.setContentText("Format d'email invalide, Veuillez entrer une adresse e-mail valide.");
            alert.showAndWait();
            return;
        }

        int numero;
        try {
            numero = Integer.parseInt(numeroText); // Parse the number
        } catch (NumberFormatException e) {

            // Afficher un message d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("integer");
            alert.setHeaderText(null);
            alert.setContentText("veuillez saisir un numero .");
            alert.showAndWait();
            return;
        }
        // Create a Sponsor object with the retrieved data
        Sponsor sponsor = new Sponsor(0, nom, email, numero); // 0 khtr id mtaana auto generated

        // Insert the Sponsor object into the database
        try {
            rcrd.ajouter(sponsor); // Assuming rcrd is the instance of ServiceSpon
            // Optionally, you can clear the input fields after adding the sponsor
            tfnom.clear();
            emailField.clear();
            numfield.clear();
            // Refresh the table view to display the newly added sponsor
            showSuccessAlert("Sponsor ajouté", "Le sponsor a été ajouté avec succès.");
        } catch (SQLException ex) {
            // Handle any SQL exceptions
            ex.printStackTrace();
            // You might want to show an error message to the user
        }
        sendMail(sponsor);
    }


    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(okButton);

        // Event handler for the OK button


        alert.showAndWait();
    }

    // Method to validate name format
    private boolean isValidName(String name) {
        return name.matches("[a-zA-Z]+") && name.length() >= 3 && name.length() <= 10;
    }

    // Method to validate email format using regular expression
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
        return email.matches(emailRegex);


    }


    private Sponsor sponsor;

    // Method to initialize the data for editing
    public void initData(Sponsor sponsor) {
        this.sponsor = sponsor;
        // Populate the text fields with the data of the selected sponsor
        tfnom.setText(sponsor.getName());
        emailField.setText(sponsor.getEmail());
        numfield.setText(String.valueOf(sponsor.getNumber()));
    }




    @FXML
    void modifsponsor() {
        // if (selectedSponsor == null) {
        //   showErrorAlert("Erreur de modification", "Veuillez sélectionner une Sponsor à modifier.");
        //     return;
        //  }

        String nom = tfnom.getText().trim();
        String email = emailField.getText().trim();
        String  numberStr = numfield.getText().trim();

        if (nom.isEmpty() || email.isEmpty() ||  numberStr.isEmpty()) {
            showErrorAlert("Erreur de modification", "Veuillez remplir tous les champs avant de modifier une sponsor.");
            return;
        }

        if (nom.length() < 3) {
            showErrorAlert("Erreur", "Le nom doit contenir au moins 3 caractères");
            return;
        }

        if (!email.matches("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b")) {
            showErrorAlert("Email invalide", "Veuillez saisir une adresse email valide.");
            return;
        }
        int number;
        try {
            number = Integer.parseInt(numberStr);
        } catch (NumberFormatException e) {
            showErrorAlert("Erreur", "Le numéro doit être un entier.");
            return;
        }

        //  Sponsor updatedSponsor = new Sponsor(Sponsor.getId(), nom, email, number);
        // rcrd.modifierSpon(updatedSponsor);
        rcrd.modifierSpon(new Sponsor(sponsor.getId(), nom, email, number));
        showSuccessAlertt("Modification réussie", "La sponsor a été modifiée avec succès.");
        // Refresh the table view or data source as needed
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    Listsponsors listsController = new Listsponsors ();
    private void showSuccessAlertt(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Event handler to refresh data after the alert is closed
        alert.setOnHidden(event -> {
            // Add your code to refresh the data here
            // For example, you can call a method to refresh the list
            listsController.showw();
        });

        alert.showAndWait();
    }












    @FXML
    private Pane chartContainer;

    public void setPieChartData(ObservableList<PieChart.Data> pieChartData) {
        PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Sponsors and Events");
        chartContainer.getChildren().add(chart);

    }
}


















