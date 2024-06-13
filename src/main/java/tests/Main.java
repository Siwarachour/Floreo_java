package tests;

import entities.Admin;
import entities.Client;
import entities.Fournisseur;
import entities.User;
import services.UserService;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {

        UserService a = new UserService();

        User u2 = new Admin("houssem", "khalfaoui", "siwar123", "ROLE_CLIENT", "siwar1@gmail.com", "logo", 26986405, false, new Date(99, 5, 11));
        Admin admin1 = new Admin("John", "Doe", "password", "ROLE_ADMIN", "john@example.com", "image.jpg", 123456, true, new Date());

        Fournisseur admin2 = new Fournisseur(1, "Jane", "Smith", "password", "janess98@example.com", "image.jpg", 654321, true, new Date());

        User u3 = new Client("houssem", "khalfaoui", "siwar123", "ROLE_CLIENT", "siwar22@gmail.com", "logo", 26986405, false, new Date(99, 5, 11));
        User u4 = new User("hii", "khalfaoui", "siwar123",  "siwar656@gmail.com","ROLE_CLIENT", "logo", 26986405, false, new Date(99, 5, 11));

        a.add(u2);
        u2.setEmail("khalfaouui");
        a.update(u2);
        a.signUp(u4);
        System.out.println(a.show());

        List<User> usersSortedByName = a.sortByName();
        System.out.println("Users sorted by name:");
        for (User user : usersSortedByName) {
            System.out.println(user);
        }

        List<User> usersSortedByEmail = a.sortByEmail();
        System.out.println("Users sorted by Email :");
        for (User user : usersSortedByEmail) {
            System.out.println(user);
        }

        int userIdToSearch = 173; // Remplacez 3 par l'ID que vous souhaitez rechercher
        User foundUser = a.searchById(userIdToSearch);

        if (foundUser != null) {
            System.out.println("User found:");
            System.out.println(foundUser);
        } else {
            System.out.println("User with ID " + userIdToSearch + " not found.");
        }

        Date dateOfBirth = new Date(99, 5, 11);
        java.sql.Date sqlDateOfBirth = new java.sql.Date(dateOfBirth.getTime());
        List<User> usersByDateOfBirth = a.searchByDateOfBirth(sqlDateOfBirth);

        if (usersByDateOfBirth.isEmpty()) {
            System.out.println("Aucun utilisateur trouvé avec la date de naissance spécifiée.");
        } else {
            System.out.println("Utilisateurs trouvés avec la date de naissance spécifiée :");
            for (User user : usersByDateOfBirth) {
                System.out.println(user);
            }
        }
        a.getUserByEmail("siwar1@gmail.com");
    }
}
