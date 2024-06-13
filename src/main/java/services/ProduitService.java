package services;

import Controllers.UserController.UserSession;
import entities.Produit;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProduitService implements Iservice<Produit> {
    UserSession userSession = UserSession.getInstance();

    private static ProduitService instance;
    PreparedStatement preparedStatement;
    Connection connection;

    public ProduitService() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public static ProduitService getInstance() {
        if (instance == null) {
            instance = new ProduitService();
        }
        return instance;
    }

    public List<Produit> getAll() {
        List<Produit> listProduit = new ArrayList<>();
        try {

            preparedStatement = connection.prepareStatement("SELECT * FROM `produit`");


            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Produit produit = new Produit();
                produit.setId(resultSet.getInt("id"));
                produit.setNomp(resultSet.getString("nomp"));
                produit.setDescp(resultSet.getString("descp"));
                produit.setCatg(resultSet.getString("catg"));
                produit.setPrix(resultSet.getFloat("prix"));
                produit.setImage(resultSet.getString("image"));

                listProduit.add(produit);
            }
        } catch (SQLException exception) {
            System.out.println("Error (getAll) produit : " + exception.getMessage());
        }
        return listProduit;
    }


    public boolean add(Produit produit) {
        String request = "INSERT INTO `produit`(`nomp`, `descp`, `catg`, `prix`, `image`) VALUES(?, ?, ?, ?, ?)" ;

        try {
            preparedStatement = connection.prepareStatement(request);

            preparedStatement.setString(1, produit.getNomp());
            preparedStatement.setString(2, produit.getDescp());
            preparedStatement.setString(3, produit.getCatg());
            preparedStatement.setFloat(4, produit.getPrix());
            preparedStatement.setString(5, produit.getImage());

            preparedStatement.executeUpdate();
            System.out.println("Produit added");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (add) produit : " + exception.getMessage());
        }
        return false;
    }

    public boolean edit(Produit produit) {

        String request = "UPDATE `produit` SET `nomp` = ?, `descp` = ?, `catg` = ?, `prix` = ?, `image` = ? WHERE `id` = ?" ;

        try {
            preparedStatement = connection.prepareStatement(request);

            preparedStatement.setString(1, produit.getNomp());
            preparedStatement.setString(2, produit.getDescp());
            preparedStatement.setString(3, produit.getCatg());
            preparedStatement.setFloat(4, produit.getPrix());
            preparedStatement.setString(5, produit.getImage());


            preparedStatement.setInt(6, produit.getId());

            preparedStatement.executeUpdate();
            System.out.println("Produit edited");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (edit) produit : " + exception.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM `produit` WHERE `id`=?");
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println("Produit deleted");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (delete) produit : " + exception.getMessage());
        }
        return false;
    }
}
