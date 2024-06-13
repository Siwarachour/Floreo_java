package Controllers.UserController;

import Controllers.commande.ManageController;
import Controllers.commande.ShowAllController;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.mysql.cj.Session;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.UserService;
import utils.MyDatabase;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LoginController {

    static String emailc;
    @FXML
    private ImageView imageView;
    @FXML
    private TextField emailField;

    @FXML
    private TextField Passwordfield;
    private static Session session;
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private int loginAttempts = 0;
    boolean accountBlocked = false;
    boolean  qr=false;
    @FXML
    void loginclicked(ActionEvent event) throws SQLException {
        String email = emailField.getText();
        String password = Passwordfield.getText();



        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Fields Empty", "Please fill in both email and password fields.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid Email", "Please enter a valid email address.");
            return;
        }

        UserService userService = new UserService();
        boolean loginSuccessful = userService.login(email, password);

        if (loginSuccessful) {
            System.out.println("Login successful!");
            String role = userService.getRole(email);
            //String role = userService.getRole(email);
            UserSession.getInstance().setAuthenticatedUser(email, role);


            try {
                FXMLLoader loader;
                if ("ROLE_CLIENT".equals(role)) {
                    FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/UserInterface/home.fxml"));
                    Parent root = loader1.load(); // Load the FXML file
                    User userSiwar =userService.getUserByEmail(email);
                    System.out.println(userSiwar);
                    Controllers.back.reclamation.ManageController manageRController = new Controllers.back.reclamation.ManageController();
                    manageRController.user = userSiwar;

                    HomeController homeController = loader1.getController(); // Get the controller instance
                    ManageController manageController = new ManageController();
                    manageController.user = userSiwar;
                    homeController.setAuthenticatedEmail(email);
                    this.emailc = email;
                    ManageController ajouterArticleController = new ManageController();
                    ajouterArticleController.user = userSiwar;

                } else if ("ROLE_ADMIN".equals(role)) {
                    loader = new FXMLLoader(getClass().getResource("/UserInterface/sidebar.fxml"));
                    Parent root = loader.load();
                    SideBarController sidebarController = loader.getController();
                    this.emailc = email;

                    sidebarController.setAuthenticatedEmail(email);

                } else if ("ROLE_FOURNISSEUR".equals(role)) {
                    System.out.println(role);
                    FXMLLoader loader2= new FXMLLoader(getClass().getResource("/UserInterface/Home2.fxml"));
                    Parent root = loader2.load(); // Load the FXML file
                    User userSiwar =userService.getUserByEmail(email);
                    System.out.println(userSiwar);
                    Controllers.back.reclamation.ManageController manageRController = new Controllers.back.reclamation.ManageController();
                    manageRController.user = userSiwar;

//                    HomeController homeController = loader1.getController(); // Get the controller instance
                    ManageController manageController = new ManageController();
                    manageController.user = userSiwar;
//                    homeController.setAuthenticatedEmail(email);
                    HomeController homeController = loader2.getController(); // Get the controller instance

                    homeController.setAuthenticatedEmail(email);
                    this.emailc = email;
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Unknown Role", "Your role is not recognized.");
                    return;
                }
                // Other processing
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loginAttempts++;

            if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
                // Bloquer le compte après trois tentatives de connexion infructueuses
                User user = userService.getUserByEmail(email);
                boolean isBanned = userService.banUser(user); // Appel de la méthode banUser et récupération de sa valeur de retour

                if (isBanned) {
                    // La méthode banUser a réussi à bloquer l'utilisateur
                    user.setIs_banned(true); // Mettre à jour localement la propriété is_banned de l'utilisateur
                    showAlert(Alert.AlertType.ERROR, "Error", "Account Blocked", "Your account has been blocked due to too many failed login attempts.");
                    return;
                } else {
                    // La méthode banUser a échoué
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to Block Account", "An error occurred while blocking your account.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Login Failed", "Incorrect email or password.");
            }
        }
    }

    @FXML
    private void scanQRCode(ActionEvent event) {
        Connection connection = MyDatabase.getInstance().getConnection();
        UserService userService = new UserService();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select QR code image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));

        File selectedFile = fileChooser.showOpenDialog(imageView.getScene().getWindow());
        if (selectedFile == null) {
            return;
        }

        try (FileInputStream fileInputStream = new FileInputStream(selectedFile)) {
            BufferedImage bufferedImage = ImageIO.read(fileInputStream);
            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            MultiFormatReader reader = new MultiFormatReader();
            Result result = reader.decode(bitmap);
            if (result != null) {
                String code = result.getText().trim();
                System.out.println(code);

                String[] parts = code.split("\\|\\|\\|"); // Split the string using "|||"
                String passwordqr = parts[0];
                String emailqr = parts[1];
                System.out.println("emqr:"+emailqr);
                System.out.println("passqr"+passwordqr);
                try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM user WHERE email = ?")) {
                    ps.setString(1, code);
                    try   {
                        boolean  ok=true;
                        if (ok) {
                            //String email = rs.getString("email");
                            //String password = rs.getString("password");
                            // System.out.println(password);
                            User de= new User();
                            de.setEmail(emailqr);
                            de.setPassword(passwordqr);
                            String loginSuccessful = userService.loginQr(de);

                            System.out.println("le role est "+loginSuccessful);
                            if (loginSuccessful  !=  null) {
                                System.out.println(loginSuccessful);

                                switch (loginSuccessful) {
                                    case "\"ROLE_ADMIN\"":
                                        openScene("/UserInterface/SideBar.fxml", event);
                                        break;
                                    case "[\"ROLE_CLIENT\"]":
                                        openScene("/UserInterface/Home.fxml", event);
                                        break;
                                    case "\"ROLE_FOURNISSEUR\"":
                                        openScene("/UserInterface/Home1.fxml", event);
                                        break;
                                    default:
                                        showAlert(Alert.AlertType.ERROR, "Invalid Role", "Invalid Role", "Invalid user role detected.", ButtonType.CLOSE);
                                }
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Invalid Credentials", "Invalid Credentials", "Invalid email or password.", ButtonType.CLOSE);
                            }
                        } else {
                            showAlert(Alert.AlertType.ERROR, "User Not Found", "User Not Found", "Email not found in database.", ButtonType.CLOSE);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid QR Code", "Invalid QR Code", "Unable to scan QR code.", ButtonType.CLOSE);
            }
        } catch (IOException | NotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error", "Unable to process QR code.", ButtonType.CLOSE);
            e.printStackTrace();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Database Error", "Error executing SQL query.", ButtonType.CLOSE);
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String message, ButtonType... buttons) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.getButtonTypes().setAll(buttons);
        alert.showAndWait();
    }


    private void openScene(String fxmlFile, ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            Parent page = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene scene = new Scene(page);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void ClickedSign(MouseEvent mouseEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/UserInterface/Registration.fxml"));
            Scene scene = emailField.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void forgot(MouseEvent event) {
        try {
            Node source = (Node) event.getSource();
            Scene scene = source.getScene();

            Parent root = FXMLLoader.load(getClass().getResource("/UserInterface/ResetPwd.fxml"));
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Session getSession() {
        return session;
    }

    private boolean passwordVisible = false;

    public void toggleShowPassword(ActionEvent actionEvent) {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            Passwordfield.setPromptText(Passwordfield.getText());
            Passwordfield.setText("");
        } else {
            StringBuilder maskedPassword = new StringBuilder();
            for (int i = 0; i < Passwordfield.getPromptText().length(); i++) {
                maskedPassword.append("*");
            }
            Passwordfield.setText(Passwordfield.getPromptText());
            Passwordfield.setPromptText(null);
        }
    }


    public void logQr(ActionEvent event) {
        this.scanQRCode(event);

    }/*
    @FXML
    private void scanQRCode(ActionEvent event) {
        Connection connection = MyDatabase.getInstance().getConnection();
        UserService userService = new UserService();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select QR code image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));


        File selectedFile = fileChooser.showOpenDialog(imageView.getScene().getWindow());
        if (selectedFile == null) {
            return;
        }

        Image image = new Image(selectedFile.toURI().toString());
        imageView.setImage(image);

        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);


        try (FileInputStream fileInputStream = new FileInputStream(selectedFile)) {
            BufferedImage bufferedImage = ImageIO.read(fileInputStream);

            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            MultiFormatReader reader = new MultiFormatReader();
            Result result = reader.decode(bitmap);

            if (result != null) {
                String code = result.getText();

                // Utilisez le code pour rechercher l'utilisateur dans la base de données
                try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM user WHERE qr_code = ?")) {
                    ps.setString(1, code);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String email = rs.getString("email");
                            String password = rs.getString("password");
                            String roles = userService.loginQr(new User(email, password));

                            if (!roles.isEmpty()) {
                                switch (roles) {
                                    case "admin":
                                        openScene("/UserInterface/SideBar.fxml", event);
                                        break;
                                    case "fournisseur":
                                        openScene("/UserInterface/Home2.fxml", event);
                                        break;
                                    case "client":
                                        openScene("/UserInterface/Home1.fxml", event);
                                        break;
                                    default:
                                        new Alert(Alert.AlertType.ERROR, "Invalid QR code", ButtonType.CLOSE).show();
                                }
                            } else {
                                new Alert(Alert.AlertType.ERROR, "Empty role", ButtonType.CLOSE).show();
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Error executing SQL query", ButtonType.CLOSE).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Unable to scan QR code", ButtonType.CLOSE).show();
            }
        } catch (IOException | NotFoundException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Unable to scan QR code", ButtonType.CLOSE).show();
        }

    }
*/


    private void openScene(String fxmlFile, MouseEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent page = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene scene = new Scene(page);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


   /* public String loginQr(User user) {
        try {
            String role = "";
            if (!user.getEmail().isEmpty() && !user.getPassword().isEmpty()) {
                String req = "SELECT roles FROM user WHERE email = ? AND password = ?";
                PreparedStatement pst = MyDatabase.getInstance().getConnection().prepareStatement(req);
                pst.setString(1, user.getEmail());
                pst.setString(2, user.getPassword());
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    role = rs.getString("roles");
                    System.out.println("Role for user " + user.getEmail() + ": " + role);
                } else {
                    System.err.println("Invalid credentials!");
                }
            } else {
                System.out.println("Empty fields");
            }
            return role;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return "";
        }
    }*/

}
