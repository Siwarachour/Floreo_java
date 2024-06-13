package Controllers.UserController;
public class UserSession {
    private static UserSession instance;
    private String authenticatedEmail;
    private String userRole; // Ajouter un champ pour stocker le rôle de l'utilisateur
    private boolean loggedIn;

    private UserSession() {
        // Empêcher l'instanciation directe depuis l'extérieur de la classe
    }

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setAuthenticatedUser(String email, String role) {
        authenticatedEmail = email;
        userRole = role;
        loggedIn = true;
    }

    public String getAuthenticatedEmail() {
        return authenticatedEmail;
    }

    public String getUserRole() {
        return userRole;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void logout() {
        authenticatedEmail = null;
        userRole = null;
        loggedIn = false;
    }
}
