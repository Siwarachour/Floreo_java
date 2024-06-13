package services;

import entities.Dechets;
import entities.ReservationDechets;
import entities.User;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDechetsService {

    private static ReservationDechetsService instance;
    PreparedStatement preparedStatement;
    Connection connection;

    public ReservationDechetsService() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public static ReservationDechetsService getInstance() {
        if (instance == null) {
            instance = new ReservationDechetsService();
        }
        return instance;
    }

    public List<ReservationDechets> getAll() {
        List<ReservationDechets> listReservationDechets = new ArrayList<>();
        try {

            String query = "SELECT * FROM `reservation_dechets` AS x "
                    + "RIGHT JOIN `user` AS y1 ON x.user_id = y1.id "
                    + "RIGHT JOIN `dechets` AS y2 ON x.dechet_id = y2.id "
                    + "WHERE x.user_id = y1.id  " +
                    "AND x.dechet_id = y2.id  " ;
            preparedStatement = connection.prepareStatement(query);


            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ReservationDechets reservationDechets = new ReservationDechets();
                reservationDechets.setId(resultSet.getInt("id"));
                reservationDechets.setDate(resultSet.getDate("date") != null ? resultSet.getDate("date").toLocalDate() : null);
                reservationDechets.setDateRamassage(resultSet.getDate("date_ramassage") != null ? resultSet.getDate("date_ramassage").toLocalDate() : null);
                reservationDechets.setNomFournisseur(resultSet.getString("nom_fournisseur"));
                reservationDechets.setNumeroTell(resultSet.getString("numero_tell"));
                reservationDechets.setQuantite(resultSet.getInt("quantite"));

                User user = new User();
                user.setId(resultSet.getInt("y1.id"));
                user.setName(resultSet.getString("y1.name"));
                reservationDechets.setUser(user);

                Dechets dechets = new Dechets();
                dechets.setId(resultSet.getInt("y2.id"));
                dechets.setType(resultSet.getString("y2.type"));
                reservationDechets.setDechets(dechets);

                listReservationDechets.add(reservationDechets);
            }
        } catch (SQLException exception) {
            System.out.println("Error (getAll) reservationDechets : " + exception.getMessage());
        }
        return listReservationDechets;
    }

    public List<User> getAllUsers() {
        List<User> listUsers = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM `user`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                listUsers.add(user);
            }
        } catch (SQLException exception) {
            System.out.println("Error (getAll) users : " + exception.getMessage());
        }
        return listUsers;
    }


    public boolean add(ReservationDechets reservationDechets) {


        String request = "INSERT INTO `reservation_dechets`(`date`, `date_ramassage`, `nom_fournisseur`, `numero_tell`, `quantite`, `user_id`, `dechet_id`) VALUES(?, ?, ?, ?, ?, ?, ?)" ;

        try {
            preparedStatement = connection.prepareStatement(request);

            preparedStatement.setDate(1, Date.valueOf(reservationDechets.getDate()));
            preparedStatement.setDate(2, Date.valueOf(reservationDechets.getDateRamassage()));
            preparedStatement.setString(3, reservationDechets.getNomFournisseur());
            preparedStatement.setString(4, reservationDechets.getNumeroTell());
            preparedStatement.setInt(5, reservationDechets.getQuantite());

            preparedStatement.setInt(6, reservationDechets.getUser().getId());
            preparedStatement.setInt(7, reservationDechets.getDechets().getId());


            preparedStatement.executeUpdate();
            System.out.println("ReservationDechets added");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (add) reservationDechets : " + exception.getMessage());
        }
        return false;
    }

    public boolean edit(ReservationDechets reservationDechets) {

        String request = "UPDATE `reservation_dechets` SET `date` = ?, `date_ramassage` = ?, `nom_fournisseur` = ?, `numero_tell` = ?, `quantite` = ?, `user_id` = ?, `dechet_id` = ? WHERE `id` = ?" ;

        try {
            preparedStatement = connection.prepareStatement(request);

            preparedStatement.setDate(1, Date.valueOf(reservationDechets.getDate()));
            preparedStatement.setDate(2, Date.valueOf(reservationDechets.getDateRamassage()));
            preparedStatement.setString(3, reservationDechets.getNomFournisseur());
            preparedStatement.setString(4, reservationDechets.getNumeroTell());
            preparedStatement.setInt(5, reservationDechets.getQuantite());

            preparedStatement.setInt(6, reservationDechets.getUser().getId());
            preparedStatement.setInt(7, reservationDechets.getDechets().getId());

            preparedStatement.setInt(8, reservationDechets.getId());


            preparedStatement.executeUpdate();
            System.out.println("ReservationDechets edited");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (edit) reservationDechets : " + exception.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM `reservation_dechets` WHERE `id`=?");
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println("ReservationDechets deleted");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (delete) reservationDechets : " + exception.getMessage());
        }
        return false;
    }
    public List<ReservationDechets> search(String query) {
        List<ReservationDechets> searchResults = new ArrayList<>();
        try {
            String searchQuery = "SELECT * FROM `reservation_dechets` " +
                    "WHERE `nom_fournisseur` LIKE ? OR " +
                    "`numero_tell` LIKE ? OR " +
                    "`quantite` LIKE ? OR " +
                    "`date` LIKE ? OR " +
                    "`date_ramassage` LIKE ?";
            preparedStatement = connection.prepareStatement(searchQuery);
            for (int i = 1; i <= 5; i++) {
                preparedStatement.setString(i, "%" + query + "%");
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ReservationDechets reservationDechets = new ReservationDechets();
                reservationDechets.setId(resultSet.getInt("id"));
                reservationDechets.setDate(resultSet.getDate("date") != null ? resultSet.getDate("date").toLocalDate() : null);
                reservationDechets.setDateRamassage(resultSet.getDate("date_ramassage") != null ? resultSet.getDate("date_ramassage").toLocalDate() : null);
                reservationDechets.setNomFournisseur(resultSet.getString("nom_fournisseur"));
                reservationDechets.setNumeroTell(resultSet.getString("numero_tell"));
                reservationDechets.setQuantite(resultSet.getInt("quantite"));

                // You may need to fetch and set the user and dechets data here if needed

                searchResults.add(reservationDechets);
            }
        } catch (SQLException exception) {
            System.out.println("Error (search) reservationDechets: " + exception.getMessage());
        }
        return searchResults;
    }





}
