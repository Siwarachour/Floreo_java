package services;

import Controllers.UserController.UserSession;
import entities.Commande;
import entities.Produit;
import entities.User;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeService implements Iservice<Commande> {
    UserSession userSession = UserSession.getInstance();

    private static CommandeService instance;
    PreparedStatement preparedStatement;
    Connection connection;

    public CommandeService() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public static CommandeService getInstance() {
        if (instance == null) {
            instance = new CommandeService();
        }
        return instance;
    }

    public List<Commande> getAll() {
        List<Commande> listCommande = new ArrayList<>();

        System.out.println("entererd to this function as verfication show all ");
        try {

            String query = "SELECT * FROM `commande` AS x "
                    + "LEFT JOIN `user` AS y1 ON x.user_id = y1.id "
                    + "LEFT JOIN `produit` AS y2 ON x.produit_id = y2.id "
                    + "WHERE  (x.user_id = y1.id OR x.user_id IS NULL) AND x.produit_id = y2.id  ";
            preparedStatement = connection.prepareStatement(query);


            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Commande commande = new Commande();
                commande.setId(resultSet.getInt("id"));
                commande.setMontant(resultSet.getFloat("montant"));
                commande.setDatecmd(resultSet.getDate("datecmd") != null ? resultSet.getDate("datecmd").toLocalDate() : null);
                commande.setLieucmd(resultSet.getString("lieucmd"));
                commande.setQuantite(resultSet.getInt("quantite"));

                User user = new User();
                user.setId(resultSet.getInt("y1.id"));
                user.setName(resultSet.getString("y1.name"));
                user.setLastname(resultSet.getString("y1.lastname"));
                user.setEmail(resultSet.getString("y1.email"));


                user.setEmail(resultSet.getString("y1.email"));
                commande.setUser(user);
                Produit produit = new Produit();
                produit.setId(resultSet.getInt("y2.id"));
                produit.setNomp(resultSet.getString("y2.nomp"));
                commande.setProduit(produit);

                listCommande.add(commande);
            }
        } catch (SQLException exception) {
            System.out.println("Error (getAll) commande : " + exception.getMessage());
        }
        return listCommande;
    }

    public List<User> getAllUsers() {
        List<User> listUsers = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM `user`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setEmail(resultSet.getString("email"));
                listUsers.add(user);
            }
        } catch (SQLException exception) {
            System.out.println("Error (getAll) users : " + exception.getMessage());
        }
        return listUsers;
    }

    public List<Produit> getAllProduits() {
        List<Produit> listProduits = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM `produit`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Produit produit = new Produit();
                produit.setId(resultSet.getInt("id"));
                produit.setNomp(resultSet.getString("nomp"));
                produit.setPrix(resultSet.getFloat("prix"));
                listProduits.add(produit);
            }
        } catch (SQLException exception) {
            System.out.println("Error (getAll) produits : " + exception.getMessage());
        }
        return listProduits;
    }


    public boolean add(Commande commande) {
        String request = "INSERT INTO `commande` (`montant`, `datecmd`, `lieucmd`, `quantite`, `produit_id`,`user_id`) VALUES(?, ?, ?, ?, ?,?)";

        try {
            preparedStatement = connection.prepareStatement(request);

            preparedStatement.setFloat(1, commande.getMontant());
            preparedStatement.setDate(2, Date.valueOf(commande.getDatecmd()));
            preparedStatement.setString(3, commande.getLieucmd());
            preparedStatement.setInt(4, commande.getQuantite());

            preparedStatement.setInt(5, commande.getProduit().getId());
            preparedStatement.setInt(6, commande.getUser().getId());



            preparedStatement.executeUpdate();
            System.out.println("Commande added");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (add) commande : " + exception.getMessage());
        }
        return false;
    }

    public boolean edit(Commande commande) {

        String request = "UPDATE `commande` SET `montant` = ?, `lieucmd` = ?, `quantite` = ?, `produit_id` = ? WHERE `id` = ?";

        try {
            preparedStatement = connection.prepareStatement(request);

            preparedStatement.setFloat(1, commande.getMontant());
            preparedStatement.setString(2, commande.getLieucmd());
            preparedStatement.setInt(3, commande.getQuantite());
            preparedStatement.setInt(4, commande.getProduit().getId());
            preparedStatement.setInt(5, commande.getId());

            preparedStatement.executeUpdate();
            System.out.println("Commande edited");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (edit) commande : " + exception.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM `commande` WHERE `id`=?");
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println("Commande deleted");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (delete) commande : " + exception.getMessage());
        }
        return false;
    }
}
