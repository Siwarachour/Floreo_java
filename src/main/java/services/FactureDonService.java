package services;

import Controllers.UserController.UserSession;
import entities.Don;
import entities.FactureDon;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FactureDonService {

    private static FactureDonService instance;
    PreparedStatement preparedStatement;
    Connection connection;
    UserSession userSession = UserSession.getInstance();


    public FactureDonService() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public static FactureDonService getInstance() {
        if (instance == null) {
            instance = new FactureDonService();
        }
        return instance;
    }

    public List<FactureDon> getAll() {
        List<FactureDon> listFactureDon = new ArrayList<>();
        try {

            String query = "SELECT * FROM `facture_don` AS x "
                    + "RIGHT JOIN `don` AS y1 ON x.don_id = y1.id "

                    + "WHERE  x.don_id = y1.id  " ;
            preparedStatement = connection.prepareStatement(query);


            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                FactureDon factureDon = new FactureDon();
                factureDon.setId(resultSet.getInt("id"));
                factureDon.setNomDonateur(resultSet.getString("nom_donateur"));
                factureDon.setPrenomDonateur(resultSet.getString("prenom_donateur"));
                factureDon.setEmail(resultSet.getString("email"));
                factureDon.setAdresses(resultSet.getString("adresses"));
                factureDon.setNumeroTelephone(resultSet.getInt("numero_telephone"));
                factureDon.setDescription(resultSet.getString("description"));

                Don don = new Don();
                don.setId(resultSet.getInt("y1.id"));
                don.setType(resultSet.getString("y1.type"));
                factureDon.setDon(don);

                listFactureDon.add(factureDon);
            }
        } catch (SQLException exception) {
            System.out.println("Error (getAll) factureDon : " + exception.getMessage());
        }
        return listFactureDon;
    }

    public List<Don> getAllDons() {
        List<Don> listDons = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM `don`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Don don = new Don();
                don.setId(resultSet.getInt("id"));
                don.setType(resultSet.getString("type"));
                listDons.add(don);
            }
        } catch (SQLException exception) {
            System.out.println("Error (getAll) dons : " + exception.getMessage());
        }
        return listDons;
    }


    public boolean add(FactureDon factureDon) {


        String request = "INSERT INTO `facture_don`(`nom_donateur`, `prenom_donateur`, `email`, `adresses`, `numero_telephone`, `description`, `don_id`) VALUES(?, ?, ?, ?, ?, ?, ?)" ;

        try {
            preparedStatement = connection.prepareStatement(request);

            preparedStatement.setString(1, factureDon.getNomDonateur());
            preparedStatement.setString(2, factureDon.getPrenomDonateur());
            preparedStatement.setString(3,userSession.getAuthenticatedEmail());
            preparedStatement.setString(4, factureDon.getAdresses());
            preparedStatement.setInt(5, factureDon.getNumeroTelephone());
            preparedStatement.setString(6, factureDon.getDescription());

            preparedStatement.setInt(7, factureDon.getDon().getId());


            preparedStatement.executeUpdate();
            System.out.println("FactureDon added");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (add) factureDon : " + exception.getMessage());
        }
        return false;
    }

    public boolean edit(FactureDon factureDon) {

        String request = "UPDATE `facture_don` SET `nom_donateur` = ?, `prenom_donateur` = ?, `email` = ?, `adresses` = ?, `numero_telephone` = ?, `description` = ?, `don_id` = ? WHERE `id` = ?" ;

        try {
            preparedStatement = connection.prepareStatement(request);

            preparedStatement.setString(1, factureDon.getNomDonateur());
            preparedStatement.setString(2, factureDon.getPrenomDonateur());
            preparedStatement.setString(3, userSession.getAuthenticatedEmail());
            preparedStatement.setString(4, factureDon.getAdresses());
            preparedStatement.setInt(5, factureDon.getNumeroTelephone());
            preparedStatement.setString(6, factureDon.getDescription());

            preparedStatement.setInt(7, factureDon.getDon().getId());

            preparedStatement.setInt(8, factureDon.getId());

            preparedStatement.executeUpdate();
            System.out.println("FactureDon edited");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (edit) factureDon : " + exception.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM `facture_don` WHERE `id`=?");
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println("FactureDon deleted");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (delete) factureDon : " + exception.getMessage());
        }
        return false;
    }
    public List<FactureDon> search(String query) {
        List<FactureDon> searchResults = new ArrayList<>();
        try {
            String searchQuery = "SELECT * FROM `facture_don` AS x "
                    + "RIGHT JOIN `don` AS y ON x.don_id = y.id "
                    + "WHERE `nom_donateur` LIKE ? OR "
                    + "`prenom_donateur` LIKE ? OR "
                    + "`email` LIKE ? OR "
                    + "`adresses` LIKE ? OR "
                    + "`numero_telephone` LIKE ? OR "
                    + "x.`description` LIKE ? OR "
                    + "y.`type` LIKE ?";  // Assuming 'type' belongs to 'don' table


            preparedStatement = connection.prepareStatement(searchQuery);
            for (int i = 1; i <= 7; i++) {
                preparedStatement.setString(i, "%" + query + "%");
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                FactureDon factureDon = new FactureDon();
                factureDon.setId(resultSet.getInt("id"));
                factureDon.setNomDonateur(resultSet.getString("nom_donateur"));
                factureDon.setPrenomDonateur(resultSet.getString("prenom_donateur"));
                factureDon.setEmail(resultSet.getString("email"));
                factureDon.setAdresses(resultSet.getString("adresses"));
                factureDon.setNumeroTelephone(resultSet.getInt("numero_telephone"));
                factureDon.setDescription(resultSet.getString("description"));
                Don don = new Don();
                don.setId(resultSet.getInt("y.id"));
                don.setType(resultSet.getString("y.type"));
                factureDon.setDon(don);
                searchResults.add(factureDon);
            }
        } catch (SQLException exception) {
            System.out.println("Error (search) factureDon: " + exception.getMessage());
        }
        return searchResults;
    }
}
