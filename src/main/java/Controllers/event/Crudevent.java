package Controllers.event;

import entities.Evenement;
import entities.Sponsor;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ServiceEvenement;
import services.ServiceSpon;
import utils.MyDatabase;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Crudevent implements Initializable {
    private ServiceEvenement ecrd = new ServiceEvenement();
    public ObservableList<Evenement> data = FXCollections.observableArrayList();
    public ObservableList<Sponsor> data1 = FXCollections.observableArrayList();

    ObservableList<Evenement> list;

    String path="";


    @FXML
    private TextField tfnameEV;

    @FXML
    private TextField tflieu;

    @FXML
    private TextField tftype;

    @FXML
    private TextField tfdescription;

    @FXML
    private TextField tfnbParticipant;

    @FXML
    private DatePicker tfdatedebut;

    @FXML
    private DatePicker tfdatefin;





    @FXML
    private Button btnsupprimer;

    @FXML
    private Button browseimg;

    @FXML
    private ComboBox<String> cbsponsor;

    @FXML
    private Button btnrf;

    @FXML
    private TextField tfsp;

    @FXML
    private Button btnajouterev;

    @FXML
    private ImageView image2;

    @FXML
    private Button btnsoponsor;

    @FXML
    private Button btnev;

    @FXML
    private ImageView image1;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Récupérer les sponsors de la base de données et les ajouter à la liste de noms de sponsors
        ServiceSpon cd = new ServiceSpon();
        List<Sponsor> sponsors = cd.affichersponsor();

        // Créer une liste de noms de sponsors
        List<String> sponsorNames = new ArrayList<>();
        for (Sponsor sponsor : sponsors) {
            sponsorNames.add(sponsor.getName());
        }

        // Définir la liste des noms de sponsors comme éléments du ComboBox
        cbsponsor.setItems(FXCollections.observableArrayList(sponsorNames));

        // Ajouter un gestionnaire d'événements pour le ComboBox
        cbsponsor.setOnAction(event -> selectsponsor(event));
    }
    @FXML
    void ajouterev(ActionEvent event) {
        try {
            // Vérifier que tous les champs sont remplis
            if (cbsponsor.getValue() == null || tfnameEV.getText().isEmpty() || tflieu.getText().isEmpty()
                    || tftype.getText().isEmpty() || tfdescription.getText().isEmpty()
                    || tfdatedebut.getValue() == null || tfdatefin.getValue() == null
                    || tfnbParticipant.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Champs vides");
                alert.setHeaderText(null);
                alert.setContentText("Veuillez remplir tous les champs !");
                alert.showAndWait();
                return;
            }

            // Récupérer sponsor
            String sponsor = cbsponsor.getValue();

            // Récupérer les valeurs des champs
            String nom = tfnameEV.getText();
            String lieu = tflieu.getText();
            String type = tftype.getText();
            String description = tfdescription.getText();
            ////////Description ne depasse pas 200 caractere ////
            if (tfdescription.getText().length() > 200) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Description trop longue");
                alert.setHeaderText(null);
                alert.setContentText("La description ne doit pas dépasser 200 caractères !");
                alert.showAndWait();
                return;
            }

            // Récupérer la valeur sélectionnée dans le composant DatePicker
            LocalDate datedebut1 = tfdatedebut.getValue();
            LocalDate datefin1 = tfdatefin.getValue();

            // Vérifier que la date de début est avant la date de fin
            if (datefin1.isBefore(datedebut1)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Date invalide");
                alert.setHeaderText(null);
                alert.setContentText("La date de fin doit être après la date de début !");
                alert.showAndWait();
                return;
            }

            // Convertir les dates en chaînes
            String datedebut = datedebut1.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String datefin = datefin1.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // Convertir les chaînes de date en valeurs de date MySQL
            String mysqlDateString = LocalDate.parse(datedebut, DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString();
            String mysqlDateString11 = LocalDate.parse(datefin, DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString();

            // Récupérer les autres champs
            String image = path;
            image2.setImage(new Image("file:" + image));


            // Vérifier que les champs pour le nombre de participants est un nombre entier
            int nb_participant = 0;

            try {
                nb_participant = Integer.parseInt(tfnbParticipant.getText());
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Champs invalides");
                alert.setHeaderText(null);
                alert.setContentText("Le champ pour le nombre de participants doit être un nombre entier !");
                alert.showAndWait();
                return;
            }

            // Vérifier que le nombre de participants est supérieur à zéro
            if (nb_participant <= 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Champs invalides");
                alert.setHeaderText(null);
                alert.setContentText("Le nombre de participants doit être supérieur à zéro !");
                alert.showAndWait();
                return;
            }

            // Get sponsor ID
            int sponsorId = ecrd.getIdSponsor(sponsor);

            // Add the event to the database
            ecrd.ajouterEvenement(new Evenement(sponsorId, nom, type,datedebut, datefin,description,nb_participant,lieu, image ));

            // Afficher un message de confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("L evenement a été ajoutée avec succès");
            alert.showAndWait();

            // Effacer les zones de texte
            cbsponsor.setValue("");
        } catch (NumberFormatException ex) {
            // Afficher une alerte si la zone de texte idConducteur ne contient pas un entier

        }
    }

    @FXML
    void ajouterimage(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            // load the selected image into the image view
            path=selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            image2.setImage(image);
        }


    }


    @FXML
    void gererevenement(ActionEvent event) {

    }

    @FXML
    void gerersponsor(ActionEvent event) {
        try {
            Parent SponsorParent = FXMLLoader.load(getClass().getResource("/eventback/sponsor.fxml"));
            Scene SponsorScene = new Scene(SponsorParent);
            Stage window = (Stage)(((Button)event.getSource()).getScene().getWindow());
            window.setScene(SponsorScene);
            window.show();
        } catch (IOException e) {
        }
    }



    @FXML
    void modifierev(ActionEvent event) {

    }

    @FXML
    void rafraichir(ActionEvent event) {

    }

    @FXML
    void selectedEvent(MouseEvent event) {

    }

    @FXML
    void selectsponsor(ActionEvent event) {
        String selectedSponsor = cbsponsor.getValue();
        // do something with the selected conducteur
        System.out.println("Selected sponsor: " + selectedSponsor);
    }

    @FXML
    void supprimerev(ActionEvent event) {

    }




private Evenement evenement;
    public String getSponsorNameById(int sponsorId) {
        String sponsorName = "";
        try {
            String requete = "SELECT name FROM sponsor WHERE id = ?";
            PreparedStatement pst = MyDatabase.getInstance().getConnection().prepareStatement(requete);
            pst.setInt(1, sponsorId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                sponsorName = rs.getString("name");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return sponsorName;
    }

    public String getImagePathById(int eventId) {
        String imagePath = "";
        try {
            String requete = "SELECT image FROM evenement WHERE id =?"; // Assuming 'events' is your table name and 'image_path' is the column storing the image path
            PreparedStatement pst = MyDatabase.getInstance().getConnection().prepareStatement(requete);
            pst.setInt(1, eventId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                imagePath = rs.getString("image");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return imagePath;
    }


    public void initData(Evenement evenement) {
        this.evenement = evenement; // Assuming you have a field named evenement of type Evenement
        // Populate the text fields with the data of the selected evenement
        tfnameEV.setText(evenement.getNameevent());
        tftype.setText(evenement.getType());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Adjust the pattern as needed
        LocalDate datedebut = LocalDate.parse(evenement.getDatedebut(), formatter);
        LocalDate datefin = LocalDate.parse(evenement.getDatefin(), formatter);
        tfdatedebut.setValue(datedebut);
        tfdatefin.setValue(datefin);
        tfdescription.setText(evenement.getDescription());
        tfnbParticipant.setText(String.valueOf(evenement.getNbparticipant()));
        tflieu.setText(evenement.getLieu());

        // Retrieve the sponsor name using the sponsor ID from the Evenement object
        String sponsorName = getSponsorNameById(evenement.getSponsor_id());
        cbsponsor.setValue(sponsorName); // Set the sponsor name in the ComboBox

        // Retrieve the image path using the event ID from the Evenement object
        String imagePath = getImagePathById(evenement.getId());
        System.out.println("Retrieved image path: " + imagePath);
       // Print the retrieved image path// Assuming getId() returns the event ID
        if (imagePath!= null &&!imagePath.isEmpty()) {
            // Ensure the path starts with "file:///"
            String fullImagePath = "file://" + imagePath;
            Image image = new Image(fullImagePath);
            image2.setImage(image);
        } else {
            // Handle the case where there is no image associated with the evenement
            image2.setImage(null);
        }

    }




    @FXML
    void modifierEvenement(ActionEvent event) {
        try {
            // Check if all fields are filled
            if (cbsponsor.getValue() == null || tfnameEV.getText().isEmpty() || tflieu.getText().isEmpty()
                    || tftype.getText().isEmpty() || tfdescription.getText().isEmpty()
                    || tfdatedebut.getValue() == null || tfdatefin.getValue() == null
                    || tfnbParticipant.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Champs vides");
                alert.setHeaderText(null);
                alert.setContentText("Veuillez remplir tous les champs!");
                alert.showAndWait();
                return;
            }

            // Retrieve sponsor
            String sponsor = cbsponsor.getValue();

            // Retrieve values from fields
            String nom = tfnameEV.getText();
            String lieu = tflieu.getText();
            String type = tftype.getText();
            String description = tfdescription.getText();

            // Description does not exceed 200 characters
            if (tfdescription.getText().length() > 200) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Description trop longue");
                alert.setHeaderText(null);
                alert.setContentText("La description ne doit pas dépasser 200 caractères!");
                alert.showAndWait();
                return;
            }

            // Retrieve selected value in DatePicker component
            LocalDate datedebut1 = tfdatedebut.getValue();
            LocalDate datefin1 = tfdatefin.getValue();

            // Check that start date is before end date
            if (datefin1.isBefore(datedebut1)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Date invalide");
                alert.setHeaderText(null);
                alert.setContentText("La date de fin doit être après la date de début!");
                alert.showAndWait();
                return;
            }

            // Directly use LocalDate objects for date parameters
            String mysqlDateString = datedebut1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String mysqlDateString11 = datefin1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // Retrieve other fields
            String image = "file://" + evenement.getImage(); // Ensure the image path is correctly retrieved
            image2.setImage(new Image(image));

            // Check that the participant count is an integer
            int nb_participant = 0;
            try {
                nb_participant = Integer.parseInt(tfnbParticipant.getText());
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Champs invalides");
                alert.setHeaderText(null);
                alert.setContentText("Le champ pour le nombre de participants doit être un nombre entier!");
                alert.showAndWait();
                return;
            }

            // Check that the participant count is greater than zero
            if (nb_participant <= 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Champs invalides");
                alert.setHeaderText(null);
                alert.setContentText("Le nombre de participants doit être supérieur à zéro!");
                alert.showAndWait();
                return;
            }

            // Get sponsor ID
            int sponsorId = ecrd.getIdSponsor(sponsor);

            // Update the event in the database
            try {
                ecrd.modifierEvenement(new Evenement(sponsorId, nom, type, mysqlDateString, mysqlDateString11, description, nb_participant, lieu, image));
                // Display a confirmation message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("L'événement a été modifié avec succès");
                alert.setContentText("Cliquez sur OK pour fermer et rafraîchir la liste.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Refresh the list here
                    Platform.runLater(() -> {
                        // Assuming you have a method to refresh the list
                        ecrd.afficherEV();
                    });
                }
            } catch (Exception ex) {
                // Handle database operation errors
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Erreur de modification");
                errorAlert.setHeaderText("Une erreur est survenue lors de la modification de l'événement");
                errorAlert.setContentText(ex.getMessage());
                errorAlert.showAndWait();
            }

            // Clear the text fields
            cbsponsor.setValue("");
        } catch (NumberFormatException ex) {
            // Show an alert if the text field for sponsor ID does not contain an integer
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs invalides");
            alert.setHeaderText(null);
            alert.setContentText("Le champ pour le sponsor doit être un nombre entier!");
            alert.showAndWait();
        }



}}