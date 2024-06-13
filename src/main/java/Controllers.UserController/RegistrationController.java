package Controllers.UserController;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import entities.User;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import services.UserService;
import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

public class RegistrationController  implements Initializable {



    @FXML
    private DatePicker Datefield;

    @FXML
    private TextField Emailfield;

    @FXML
    private TextField Firstnamefield;

    @FXML
    private TextField Lastnamefield;

    @FXML
    private TextField Numberfield;

    @FXML
    private PasswordField Passwordfield;
    @FXML
    private ImageView imageView;


    private String imagePath;

    @FXML
    private Button cam;
    String pic ;
    @FXML
    private ImageView cap;

    @FXML
    private TextField code;

    @FXML
    private Button reset;

    @FXML
    private Button submit;
    private String qrPath;
    private RegistrationController QRCodeGenerator;

    public Captcha setCaptcha() {
        Captcha captchaV = new Captcha.Builder(250, 200)
                .addText()
                .addBackground(new GradiatedBackgroundProducer()) // Ajout d'un fond gradient
                .addNoise()
                .addBorder()
                .build();

        System.out.println(captchaV.getImage());
        Image image = SwingFXUtils.toFXImage(captchaV.getImage(), null);

        cap.setImage(image);

        return captchaV;
    }


    Captcha captcha;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Chargez le captcha dans le WebView
        captcha = setCaptcha();
    }

    @FXML
    void Registratinclicked(ActionEvent event) throws WriterException {
        String name = Firstnamefield.getText();
        String lastname = Lastnamefield.getText();
        String email = Emailfield.getText();
        String numberText = Numberfield.getText();
        String password = Passwordfield.getText();
        DatePicker datenaissance = Datefield;
        String roles = "ROLE_CLIENT";

        if (email.isEmpty() || !email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            showAlert(Alert.AlertType.ERROR, "Alert", "Erreur Saisie", "Veuillez saisir une adresse email valide.", StageStyle.DECORATED);
            return;
        }

        if (name.isEmpty() || !name.matches("[a-zA-Z]+")) {
            showAlert(Alert.AlertType.ERROR, "Alert", "Erreur Saisie", "Veuillez saisir un prénom valide.", StageStyle.DECORATED);
            return;
        }

        if (lastname.isEmpty() || !lastname.matches("[a-zA-Z]+")) {
            showAlert(Alert.AlertType.ERROR, "Alert", "Erreur Saisie", "Veuillez saisir un nom de famille valide.", StageStyle.DECORATED);
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Alert", "Erreur Saisie", "Veuillez saisir un mot de passe d'au moins 6 caractères.", StageStyle.DECORATED);
            return;
        }

        if (!numberText.matches("[2479]\\d{7}")) {
            showAlert(Alert.AlertType.ERROR, "Alert", "Erreur Saisie", "Le numéro de téléphone doit contenir 8 chiffres et commencer par 2, 4, 7 ou 9.", StageStyle.DECORATED);
            return;
        }

        int number;
        try {
            number = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Alert", "Erreur Saisie", "Veuillez saisir un numéro de téléphone valide.", StageStyle.DECORATED);
            return;
        }

        UserService userService = new UserService();
        if (userService.isEmailUsed(email)) {
            showAlert(Alert.AlertType.ERROR, "Alert", "Erreur Saisie", "Cette adresse email est déjà utilisée.", StageStyle.DECORATED);
            return;
        }

        if (imagePath == null) {
            showAlert(Alert.AlertType.ERROR, "Alert", "Erreur Saisie", "Veuillez capturer ou sélectionner une image.", StageStyle.DECORATED);
            return;
        }

        boolean isValidCaptcha = validateCaptcha(code.getText());
        if (!isValidCaptcha) {
            showAlert(Alert.AlertType.ERROR, "Alert", "Erreur Saisie", "Captcha incorrect.", StageStyle.DECORATED);
            return;
        }

        // Générer et enregistrer le code QR
        String qrPath = generateQRCodeAndSave(email,Passwordfield.getText());

        User user = new User(name,  lastname, password, email,  roles,  imagePath,  number,  false,  java.sql.Date.valueOf(datenaissance.getValue()), null, false );
        user.setIs_banned(false);


        // Afficher une notification de succès
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur ajouté", "L'utilisateur a été ajouté avec succès à la base de données.", StageStyle.DECORATED);
        addUserToDatabase(name,  lastname, password, email,  roles,  imagePath,  number,  false,  java.sql.Date.valueOf(datenaissance.getValue()), null, false );

        clearInputFields();
        userService.signUp(user);

    }
    private void saveQRCodePathToDatabase(String email, String qrPath) {

    }


    @FXML
    private void camera(ActionEvent event) {
        Webcam webcam = Webcam.getDefault();
        if (webcam != null) {
            webcam.open();

            // Ouvrir une fenêtre pour afficher l'aperçu de la caméra
            JFrame window = new JFrame("Camera Preview");
            window.setLayout(new BorderLayout());
            window.setSize(640, 480);

            WebcamPanel panel = new WebcamPanel(webcam);
            panel.setMirrored(true);
            window.add(panel, BorderLayout.CENTER);

            JButton captureButton = new JButton("Capture");
            captureButton.addActionListener(e -> {
                // Prendre une capture d'écran et la sauvegarder
                try {
                    Random rnd = new Random();
                    int number = rnd.nextInt(999999999);
                    String filename = number + "_capture.jpg";
                    String filePath = "C:\\Users\\siwar\\OneDrive\\Bureau\\webnow\\wtvrrr\\3A56_MD_ZOSS-Xperts_P3-main\\public\\uploads\\" + filename;
                    File file = new File(filePath);
                    ImageIO.write(webcam.getImage(), "JPG", file);
                    System.out.println("Capture saved: " + filename);

                    // Définir l'image capturée dans l'ImageView
                    Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);

                    // Assigner le chemin de l'image capturée à l'attribut imagePath
                    imagePath = filePath;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            window.add(captureButton, BorderLayout.SOUTH);

            window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            window.setVisible(true);
        } else {
            System.out.println("Aucune webcam détectée.");
        }
    }



    @FXML
    void uploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            // Obtenez le chemin du dossier où les images capturées sont sauvegardées
            String folderPath = "C:\\Users\\siwar\\OneDrive\\Bureau\\final\\Integration\\src\\main\\resources\\img\\uploads";

            // Obtenez le nom du fichier sélectionné
            String fileName = selectedFile.getName();

            // Concaténez le nom du fichier avec le chemin du dossier pour obtenir le chemin complet du fichier de destination
            String destinationPath = folderPath + fileName;

            // Essayez de copier le fichier sélectionné vers le dossier de destination
            try {
                Files.copy(selectedFile.toPath(), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image saved: " + destinationPath);

                // Charger l'image à partir du chemin du fichier de destination
                Image image = new Image(selectedFile.toURI().toString());

                // Afficher l'image dans l'imageView
                imageView.setImage(image);

                // Enregistrer le chemin du fichier sélectionné
                this.imagePath = destinationPath;

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to save image: " + destinationPath);
            }
        }
        Image image = new Image(new File(imagePath).toURI().toString());

        // Affichez l'image dans l'imageView destiné à afficher la photo de profil
        imageView.setImage(image);
    }




    private void showAlert(Alert.AlertType alertType, String title, String header, String message, StageStyle stageStyle) {
        Alert alert = new Alert(alertType);
        alert.initStyle(stageStyle);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/logo.png")));

        ButtonType buttonType = new ButtonType("Retour", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(buttonType);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonType) {
        }
    }
    private  void addUserToDatabase(String name, String lastname, String password, String email, String roles, String image, int number, boolean is_verified, Date datenaissance,String qr_code,Boolean is_banned) {
        String imageName = new File(image).getPath(); // Récupérer le nom de fichier à partir du chemin complet
        User user = new User( name,  lastname, password, email,  roles,  image,  number,  is_verified,  datenaissance, qr_code, is_banned);


        // Assurez-vous que is_banned est initialisé à 0
        user.setIs_banned(false);

        UserService userService = new UserService();

    }






    private void clearInputFields() {
        Firstnamefield.clear();
        Lastnamefield.clear();
        Emailfield.clear();
        Numberfield.clear();
        Passwordfield.clear();
        imageView.setImage(null);
        imagePath = null;
        Datefield.setValue(null);


    }


    @FXML
    public void RetourLogin(MouseEvent mouseEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/UserInterface/login.fxml"));
            Scene scene = Emailfield.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void reseting(ActionEvent actionEvent) {
        captcha =  setCaptcha();
        code.setText("");
    }

    public void submit(ActionEvent actionEvent)  throws IOException{
        if (captcha.isCorrect(code.getText())) {

            String tilte = "Captcha";
            String message = "Vous avez saisi le code avec succés!";
            TrayNotification tray = new TrayNotification();
            AnimationType type = AnimationType.POPUP;

            tray.setAnimationType(type);
            tray.setTitle(tilte);
            tray.setMessage(message);
            tray.setNotificationType(NotificationType.SUCCESS);
            tray.showAndDismiss(Duration.millis(3000));

            //     try {

//            stage.show();
            //Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            //  stage.close();

//        } catch (IOException ex) {
//            Logger.getLogger(Agent_mainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        else {

            String tilte = "Captcha";
            String message = "Vous avez saisi un faux code !";
            TrayNotification tray = new TrayNotification();
            AnimationType type = AnimationType.POPUP;

            tray.setAnimationType(type);
            tray.setTitle(tilte);
            tray.setMessage(message);
            tray.setNotificationType(NotificationType.ERROR);
            tray.showAndDismiss(Duration.millis(3000));

            captcha =  setCaptcha();
            code.setText("");
        }


    }


    public String generateQRCodeAndSave(String email, String password) throws WriterException {
        // Concatenate the password and image path
        String data = password + "|||" + email;
        generateAndSaveQRCode(data);
        // Generate the QR code for the concatenated data
        String qrCodeFilePath = generateAndSaveQRCode(data);

        // Return the file path of the generated QR code
        return qrCodeFilePath;
    }

    private String generateAndSaveQRCode(String data) throws WriterException {
        String  fileNamePrefix="fefefefefe";
        // Generate the QR code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 250, 250);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // Convert the BufferedImage to a JavaFX Image
        Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);

        // Save the image to the specified directory
        String directoryPath = "C:\\Users\\siwar\\OneDrive\\Bureau\\final\\Integration\\src\\main\\resources\\img\\uploads";
        Path directory = Paths.get(directoryPath);
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String filePath = directoryPath + fileNamePrefix + "_" + data.hashCode() + ".png";
        File file = new File(filePath);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(fxImage, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;
    }

    private boolean validateCaptcha(String enteredCaptcha) {
        System.out.println(enteredCaptcha.equalsIgnoreCase(code.getText()));
        // Comparer le captcha entré par l'utilisateur avec le texte du captcha généré
        return enteredCaptcha.equalsIgnoreCase(code.getText());

    }


}
