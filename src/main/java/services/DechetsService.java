package services;

import entities.Dechets;
import entities.ReservationDechets;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DechetsService {

    private static DechetsService instance;
    PreparedStatement preparedStatement;
    Connection connection;

    public DechetsService() {

        connection = DatabaseConnection.getInstance().getConnection();
    }

    public static DechetsService getInstance() {
        if (instance == null) {
            instance = new DechetsService();
        }
        return instance;
    }

    public List<Dechets> getAll() {
        List<Dechets> listDechets = new ArrayList<>();
        try {

            String query = "SELECT * FROM `dechets`;" ;
            preparedStatement = connection.prepareStatement(query);


            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Dechets dechets = new Dechets();
                dechets.setId(resultSet.getInt("id"));
                dechets.setType(resultSet.getString("type"));
                dechets.setDateEntre(resultSet.getDate("date_entre") != null ? resultSet.getDate("date_entre").toLocalDate() : null);
                dechets.setDescription(resultSet.getString("description"));
                dechets.setQuantite(resultSet.getInt("quantite"));
                dechets.setImage(resultSet.getString("image"));

                listDechets.add(dechets);
            }
        } catch (SQLException exception) {
            System.out.println("Error (getAll) dechets : " + exception.getMessage());
        }
        return listDechets;
    }

    public List<ReservationDechets> getAllReservationDechetss() {
        List<ReservationDechets> listReservationDechetss = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM `reservation_dechets`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ReservationDechets reservationDechets = new ReservationDechets();
                reservationDechets.setId(resultSet.getInt("id"));
                reservationDechets.setNomFournisseur(resultSet.getString("nom_fournisseur"));
                listReservationDechetss.add(reservationDechets);
            }
        } catch (SQLException exception) {
            System.out.println("Error (getAll) reservationDechetss : " + exception.getMessage());
        }
        return listReservationDechetss;
    }


    public boolean add(Dechets dechets) {


        String request = "INSERT INTO `dechets`(`type`, `date_entre`, `description`, `quantite`, `image`) VALUES(?, ?, ?, ?, ?)" ;

        try {
            preparedStatement = connection.prepareStatement(request);

            preparedStatement.setString(1, dechets.getType());
            preparedStatement.setDate(2, Date.valueOf(dechets.getDateEntre()));
            preparedStatement.setString(3, dechets.getDescription());
            preparedStatement.setInt(4, dechets.getQuantite());
            preparedStatement.setString(5, dechets.getImage());

            preparedStatement.executeUpdate();
            System.out.println("Dechets added");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (add) dechets : " + exception.getMessage());
        }
        return false;
    }

    public boolean edit(Dechets dechets) {

        String request = "UPDATE `dechets` SET `type` = ?, `date_entre` = ?, `description` = ?, `quantite` = ?, `image` = ? WHERE `id` = ?" ;

        try {
            preparedStatement = connection.prepareStatement(request);

            preparedStatement.setString(1, dechets.getType());
            preparedStatement.setDate(2, Date.valueOf(dechets.getDateEntre()));
            preparedStatement.setString(3, dechets.getDescription());
            preparedStatement.setInt(4, dechets.getQuantite());
            preparedStatement.setString(5, dechets.getImage());

            preparedStatement.setInt(6, dechets.getId());

            preparedStatement.executeUpdate();
            System.out.println("Dechets edited");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (edit) dechets : " + exception.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM `dechets` WHERE `id`=?");
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println("Dechets deleted");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (delete) dechets : " + exception.getMessage());
        }
        return false;
    }

    public List<Dechets> search(String query) {
        List<Dechets> searchResults = new ArrayList<>();
        try {
            String searchQuery = "SELECT * FROM `dechets` WHERE " +
                    "`type` LIKE ? OR " +
                    "`date_entre` LIKE ? OR " +
                    "`description` LIKE ? OR " +
                    "`quantite` LIKE ? OR " +
                    "`image` LIKE ?";
            preparedStatement = connection.prepareStatement(searchQuery);
            for (int i = 1; i <= 5; i++) {
                preparedStatement.setString(i, "%" + query + "%");
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Dechets dechets = new Dechets();
                dechets.setId(resultSet.getInt("id"));
                dechets.setType(resultSet.getString("type"));
                dechets.setDateEntre(resultSet.getDate("date_entre") != null ? resultSet.getDate("date_entre").toLocalDate() : null);
                dechets.setDescription(resultSet.getString("description"));
                dechets.setQuantite(resultSet.getInt("quantite"));
                dechets.setImage(resultSet.getString("image"));
                searchResults.add(dechets);
            }
        } catch (SQLException exception) {
            System.out.println("Error (search) dechets: " + exception.getMessage());
        }
        return searchResults;
    }







}
