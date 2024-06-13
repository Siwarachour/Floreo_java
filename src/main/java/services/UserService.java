package services;

import Controllers.UserController.RegistrationController;
import at.favre.lib.crypto.bcrypt.BCrypt;
import com.google.zxing.WriterException;
import entities.Reset;
import entities.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.MyDatabase;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserService implements  UserCrud<User> {
    Connection connection;
    public static int id;
    public static String email;
    public static String password;
    public static String name;
    public  static  String image;
    public static   String roles;
    public UserService() {
        connection = MyDatabase.getInstance().getConnection();


    }

    @Override
    public void add(User user) throws SQLException {
        // Check if the email already exists in the database
        if (isEmailUsed(user.getEmail())) {
            System.out.println("Error: Email is already used.");
            // Display an alert here if needed
            return; // Exit the method without adding the user
        }

        String query = "INSERT INTO user (name, lastname, roles, email, password, image, number, is_verified, datenaissance, is_banned,qr_code) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

        int cost = 13; // Exemple de coût utilisé par Symfony

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getLastname());
            preparedStatement.setString(3, "[\"" + user.getRoles() + "\"]");
            preparedStatement.setString(4, user.getEmail());

            BCrypt.Hasher hasher = BCrypt.with(BCrypt.Version.VERSION_2Y);
            String saltedPassword = hasher.hashToString(cost, user.getPassword().toCharArray());
            preparedStatement.setString(5, saltedPassword);

            preparedStatement.setString(6, user.getImage());
            preparedStatement.setInt(7, user.getNumber());
            preparedStatement.setBoolean(8, user.getIs_verified());
            preparedStatement.setDate(9, new java.sql.Date(user.getDatenaissance().getTime()));

            RegistrationController registrationController = new RegistrationController();
            // Set is_banned to 0
            preparedStatement.setBoolean(10, false);
            preparedStatement.setString(11,registrationController.generateQRCodeAndSave( email,password ));

            preparedStatement.executeUpdate();
            System.out.println("User added successfully.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public void update(User user) {
        String query = "UPDATE user SET name=?, lastname=?, roles=?, email=?, image=?, number=?, is_verified=?,datenaissance=? WHERE id=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getLastname());
            preparedStatement.setString(3, "[\"" + user.getRoles() + "\"]");
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setString(5, user.getImage());
            preparedStatement.setInt(6, user.getNumber());
            preparedStatement.setBoolean(7, user.getIs_verified());
            preparedStatement.setDate(8, new java.sql.Date(user.getDatenaissance().getTime()));

            preparedStatement.setInt(9, user.getId());

            preparedStatement.executeUpdate();
            System.out.println("User updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    @Override
    public void delete(User user) {
        String requete = "DELETE FROM user WHERE name = ? AND lastname = ? AND email = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(requete)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getLastname());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.executeUpdate();
            System.out.println("User deleted successfully.");
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @Override
    public List<User> show() {
        List<User> userList = new ArrayList<>();
        String query = "SELECT * FROM user";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setLastname(resultSet.getString("lastname"));
                user.setRoles(resultSet.getString("roles"));
                user.setEmail(resultSet.getString("email"));
                user.setImage(resultSet.getString("image"));
                user.setNumber(resultSet.getInt("number"));
                user.setDatenaissance(resultSet.getDate("datenaissance"));

                user.setIs_verified(resultSet.getBoolean("is_verified"));

                userList.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return userList;
    }

    @Override
    public void signUp(User user) {
        System.out.println("calll this function ");
        if (isEmailUsed(user.getEmail())==false) {
            user.setRoles("ROLE_CLIENT");

            try {
                add(user);

            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Email Already Used", "This email is already registered.", StageStyle.DECORATED);
        }
    }
    @Override
    public boolean signup(User user) {
        if (isEmailUsed(user.getEmail())==false) {
            System.out.println("dont authoriezd ");
            user.setRoles("ROLE_CLIENT");

            try {
                add(user);
                return true; // L'inscription a été effectuée avec succès
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
                return false; // Une erreur s'est produite lors de l'inscription
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Email Already Used", "This email is already registered.", StageStyle.DECORATED);
            return false; // L'email est déjà utilisé, l'inscription n'a pas été effectuée
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String header, String message, StageStyle stageStyle) {
        Alert alert = new Alert(alertType);
        alert.initStyle(stageStyle);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @Override
    public boolean login(String email, String password) {
        String query = "SELECT * FROM user WHERE email = ?";
        boolean isBanned = false; // Pour vérifier si le compte est bloqué

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isBanned = resultSet.getBoolean("is_banned"); // Récupérer l'état de l'interdiction
                if (!isBanned) { // Vérifier si le compte n'est pas bloqué
                    String hashedPasswordFromDB = resultSet.getString("password");
                    if (BCrypt.verifyer().verify(password.toCharArray(), hashedPasswordFromDB).verified) {
                        String role = resultSet.getString("roles");
                        if (role.contains("ROLE_CLIENT")) {
                            loadProfileFXML();
                            return true;
                        } else if (role.contains("ROLE_FOURNISSEUR")) {
                            loadFourFXML();
                            return true;
                        } else if (role.contains("ROLE_ADMIN")) {
                            loadSidebarFXML();
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Si le compte est bloqué ou si les identifiants sont incorrects, retourner false
        return false;
    }
    public String loginQr(User user) {
        String role = "";
        try {
            if (!user.getEmail().isEmpty() && !user.getPassword().isEmpty()) {
                System.out.println("user found");
                String req = "SELECT * FROM user WHERE email = ?";
                PreparedStatement pst = connection.prepareStatement(req); // Utilisation de l'objet de connexion conx
                pst.setString(1, user.getEmail());
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    System.out.println("resuest  executes and ha ve  value");
                    id = rs.getInt("id");
                    name = rs.getString("name");
                    role = rs.getString("roles");
                    image = rs.getString("image");
                    System.out.println("Bonjour :" + name);
                } else {
                    System.err.println("Veuillez vérifier vos données !");
                }
            } else {
                System.out.println("Champs vides");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        System.out.println("le  role de service est" +role);
        return role;
    }



    private void loadProfileFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../UserInterface/Home.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadFourFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../UserInterface/Home2.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void loadSidebarFXML() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../UserInterface/SideBar.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User getUserByEmail(String email) {
        User user = null;
        String query = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setLastname(resultSet.getString("lastname"));
                user.setRoles(resultSet.getString("roles"));
                // Assuming resultSet is an instance of ResultSet
                user.setDatenaissance(resultSet.getDate("datenaissance")); // Utilisez getDate pour récupérer une date
                user.setNumber(resultSet.getInt("number"));
                user.setEmail(resultSet.getString("email"));
                String imagePath = resultSet.getString("image");
                user.setImage(imagePath);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return user;
    }


    @Override
    public void updateEmail(int userId, String newEmail) {
        String query = "UPDATE user SET email=? WHERE id=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, newEmail);
            preparedStatement.setInt(2, userId);

            preparedStatement.executeUpdate();
            System.out.println("Email updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void updateName(User user) {
        String requete = "UPDATE user SET name=? WHERE id=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(requete)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setInt(2, user.getId());
            preparedStatement.executeUpdate();
            System.out.println("Name updated successfully.");
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void updateRole(User user) {
        String requete = "UPDATE user SET roles=? WHERE id=?";
        System.out.println(user);
        try (PreparedStatement preparedStatement = connection.prepareStatement(requete)) {
            preparedStatement.setString(1, user.getRoles()); // Assuming getRole() returns a single role
            preparedStatement.setInt(2, user.getId());
            preparedStatement.executeUpdate();
            System.out.println("Role updated successfully.");
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @Override
    public void updateLastName(User user) {
        String requete = "UPDATE user SET lastname=? WHERE id=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(requete)) {
            preparedStatement.setString(1, user.getLastname());
            preparedStatement.setInt(2, user.getId());
            preparedStatement.executeUpdate();
            System.out.println("Last name updated successfully.");
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<User> sortByEmail() {
        List<User> userList = new ArrayList<>();
        String query = "SELECT * FROM user ORDER BY email";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = new User(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("lastname"),
                        resultSet.getString("password"),
                        resultSet.getString("email"),
                        resultSet.getString("roles"),
                        resultSet.getString("image"),
                        resultSet.getInt("Number"),
                        resultSet.getBoolean("is_verified"),
                        resultSet.getDate("datenaissance")

                );
                userList.add(user);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return userList;
    }

    @Override
    public List<User> sortByName() {
        List<User> userList = new ArrayList<>();
        String query = "SELECT * FROM user ORDER BY name";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("lastname"),
                        resultSet.getString("password"),
                        resultSet.getString("email"),
                        resultSet.getString("roles"),
                        resultSet.getString("image"),
                        resultSet.getInt("Number"),
                        resultSet.getBoolean("is_verified"),
                        resultSet.getDate("datenaissance")
                );
                userList.add(user);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return userList;
    }

    @Override
    public List<User> sortById() {
        List<User> userList = new ArrayList<>();
        String query = "SELECT * FROM user ORDER BY id";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("lastname"),
                        resultSet.getString("password"),
                        resultSet.getString("email"),
                        resultSet.getString("roles"),
                        resultSet.getString("image"),
                        resultSet.getInt("Number"),
                        resultSet.getBoolean("is_verified"),
                        resultSet.getDate("datenaissance")
                );
                userList.add(user);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return userList;
    }

    @Override
    public List<User> searchByName(String name) {
        List<User> userList = new ArrayList<>();
        String query = "SELECT * FROM user WHERE nom LIKE ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Utilisation du caractère Joker '%' pour rechercher des correspondances partielles du nom
            preparedStatement.setString(1, "%" + name + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("password"),
                        resultSet.getString("email"),
                        resultSet.getString("roles"),
                        resultSet.getString("image"),
                        resultSet.getInt("Number"),
                        resultSet.getBoolean("is_verified"),
                        resultSet.getDate("datenaissance")

                );
                userList.add(user);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return userList;
    }


    @Override
    public User searchById(int id) {
        User user = null;
        String query = "SELECT * FROM user WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("lastname"),
                        resultSet.getString("password"),
                        resultSet.getString("email"),
                        resultSet.getString("roles"),
                        resultSet.getString("image"),
                        resultSet.getInt("Number"),
                        resultSet.getBoolean("is_verified"),
                        resultSet.getDate("datenaissance")
                );
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return user;

    }

    @Override
    public List<User> searchByDateOfBirth(Date dateOfBirth) {
        List<User> userList = new ArrayList<>();
        String query = "SELECT * FROM user WHERE datenaissance = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDate(1, new java.sql.Date(dateOfBirth.getTime()));
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("lastname"),
                        resultSet.getString("password"),
                        resultSet.getString("email"),
                        resultSet.getString("roles"),
                        resultSet.getString("image"),
                        resultSet.getInt("Number"),
                        resultSet.getBoolean("is_verified"),
                        resultSet.getDate("datenaissance")
                );
                userList.add(user);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return userList;
    }

    @Override
    public boolean isEmailUsed(String email) {
        List<User> userList = getUsersFromDatabase();
        for (User user : userList) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    private List<User> getUsersFromDatabase() {





        String query = "SELECT * FROM user";
        List<User> listusers = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while  (resultSet.next()) {

                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setLastname(resultSet.getString("lastname"));
                user.setRoles(resultSet.getString("roles"));
                // Assuming resultSet is an instance of ResultSet
                user.setDatenaissance(resultSet.getDate("datenaissance")); // Utilisez getDate pour récupérer une date
                user.setNumber(resultSet.getInt("number"));
                user.setEmail(resultSet.getString("email"));
                String imagePath = resultSet.getString("image");
                user.setImage(imagePath);
                listusers.add(user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return listusers;
    }












    @Override
    public String getRole(String email) {
        String role = "";
        String query = "SELECT roles FROM user WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                role = resultSet.getString("roles");
                // Vérifie si le rôle contient "ROLE_CLIENT" ou "ROLE_ADMIN"
                if (role.contains("ROLE_CLIENT")) {
                    role = "ROLE_CLIENT";
                } else if (role.contains("ROLE_ADMIN")) {
                    role = "ROLE_ADMIN";
                }else if (role.contains("ROLE_FOURNISSEUR")) {
                    role = "ROLE_FOURNISSEUR";}
                else {
                    role = "UNKNOWN_ROLE"; // Si le rôle n'est pas reconnu
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return role;
    }


    public Map<String, Integer> getUserCountByRole() {
        Map<String, Integer> userCountByRole = new HashMap<>();
        String query = "SELECT roles, COUNT(*) AS count FROM user GROUP BY roles";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String role = resultSet.getString("roles");
                int count = resultSet.getInt("count");
                userCountByRole.put(role, count);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return userCountByRole;
    }

    public Map<String, Integer> getDataForAreaChart() {
        Map<String, Integer> userCountByAgeGroup = new HashMap<>();
        String query = "SELECT FLOOR(DATEDIFF(CURRENT_DATE(), datenaissance) / 365.25 / 10) AS age_group, COUNT(*) AS count " +
                "FROM user " +
                "GROUP BY age_group";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int ageGroup = resultSet.getInt("age_group") * 10; // Calcul de l'âge approximatif
                int count = resultSet.getInt("count");
                String ageGroupLabel = ageGroup + "-" + (ageGroup + 9); // Création d'une étiquette pour le groupe d'âge
                userCountByAgeGroup.put(ageGroupLabel, count);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return userCountByAgeGroup;
    }


    @Override
    public void testEamil(String email) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/flo_dbt", "root", "");
            String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            if (count > 0) {
                System.out.println("L'adresse e-mail existe dans la base de données.");
            } else {
                System.out.println("L'adresse e-mail n'existe pas dans la base de données.");
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public boolean reset(Reset t) {
        long end = System.currentTimeMillis();
        try {
            String req = "SELECT * from reset where code=?";
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, t.getCode());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                long StartTime = Long.parseLong(rs.getString("timeMils"));
                long calT = end - StartTime;
                if (calT < 120000) {
                    email = rs.getString("email");
                    return true;
                } else {
                    String reqs = "DELETE FROM reset WHERE code=?";
                    PreparedStatement psts = connection.prepareStatement(reqs);
                    psts.setInt(1, t.getCode());
                    psts.executeUpdate();
                    System.err.println("Time OUT !! Code Introuvable.");
                    return false;
                }
            } else {
                System.err.println("Code Incorrect !");
                return false;
            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());
            return false;

        }
    }

    public void modifierMdp(User t) throws SQLException {
        int cost = 13;
        BCrypt.Hasher hasher = BCrypt.with(BCrypt.Version.VERSION_2Y);
        String saltedPassword = hasher.hashToString(cost, t.getPassword().toCharArray());
        try {

            System.out.println(t.getEmail()+"+"+t.getPassword());
            String reqs = "UPDATE user SET password=? WHERE email=?";
            PreparedStatement pst = connection.prepareStatement(reqs);

            pst.setString(2, t.getEmail());
            pst.setString(1, saltedPassword);
            System.out.println(t.getEmail()+"+"+saltedPassword);

            pst.executeUpdate();

            System.out.println("Mot de passe modifié !");

        } catch (SQLException ex) {
            System.out.println("Erreur lors de la modification du mot de passe :");
            System.out.println(ex.getMessage());
            connection.rollback(); // Annuler la transaction en cas d'erreur

        }
    }
    @Override
    public boolean banUser(User user) throws SQLException {
        String query = "UPDATE user SET is_banned = ? WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, true); // Mettre à jour is_banned à true (1)
            preparedStatement.setString(2, user.getEmail());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User is_banned status updated successfully.");
                return true; // Succès de la mise à jour
            } else {
                System.out.println("User with ID " + user.getId() + " not found.");
                return false; // Aucune ligne mise à jour
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
            return false; // Erreur lors de l'exécution de la requête SQL
        }
    }



    @Override
    public void unbanUser(User user) throws SQLException {
        String query = "UPDATE user SET is_banned = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, false);
            preparedStatement.setInt(2, user.getId());
            preparedStatement.executeUpdate();
            System.out.println("User is_banned status updated successfully.");
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }




}

