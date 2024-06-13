package services;

import entities.Reclamation;
import entities.User;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationService {

    private static ReclamationService instance;
    PreparedStatement preparedStatement;
    Connection connection;

    public ReclamationService() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public static ReclamationService getInstance() {
        if (instance == null) {
            instance = new ReclamationService();
        }
        return instance;
    }

    public List<Reclamation> getAll() {
        List<Reclamation> listReclamation = new ArrayList<>();
        try {

            String query = "SELECT * FROM `reclamation` AS x "
                    + "LEFT JOIN `user` AS y1 ON x.user_id = y1.id "
                    + "WHERE  x.user_id = y1.id OR x.user_id IS NULL";
            preparedStatement = connection.prepareStatement(query);


            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Reclamation reclamation = new Reclamation();
                reclamation.setId(resultSet.getInt("id"));
                reclamation.setType(resultSet.getString("type"));
                reclamation.setDescription(resultSet.getString("description"));
                reclamation.setDateajout(resultSet.getDate("dateajout") != null ? resultSet.getDate("dateajout").toLocalDate() : null);
                reclamation.setDatemodif(resultSet.getDate("datemodif") != null ? resultSet.getDate("datemodif").toLocalDate() : null);

                User user = new User();
                user.setId(resultSet.getInt("y1.id"));
                user.setEmail(resultSet.getString("y1.email"));
                user.setName(resultSet.getString("y1.name"));

                reclamation.setUser(user);

                listReclamation.add(reclamation);
            }
        } catch (SQLException exception) {
            System.out.println("Error (getAll) reclamation : " + exception.getMessage());
        }
        return listReclamation;
    }

    public boolean add(Reclamation reclamation) {
        String request = "INSERT INTO `reclamation`(`type`, `description`, `dateajout`, `datemodif`,`user_id`) VALUES(?, ?, ?, ?,?)";

        try {
            preparedStatement = connection.prepareStatement(request);

            preparedStatement.setString(1, reclamation.getType());
            preparedStatement.setString(2, reclamation.getDescription());
            preparedStatement.setDate(3, Date.valueOf(reclamation.getDateajout()));
            preparedStatement.setDate(4, Date.valueOf(reclamation.getDatemodif()));
            preparedStatement.setInt(5, reclamation.getUser().getId());
            preparedStatement.executeUpdate();
            System.out.println("Reclamation added");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (add) reclamation : " + exception.getMessage());
        }
        return false;
    }

    public boolean edit(Reclamation reclamation) {

        String request = "UPDATE `reclamation` SET `type` = ?, `description` = ?, `datemodif` = ? WHERE `id` = ?";

        try {
            preparedStatement = connection.prepareStatement(request);

            preparedStatement.setString(1, reclamation.getType());
            preparedStatement.setString(2, reclamation.getDescription());
            preparedStatement.setDate(3, Date.valueOf(reclamation.getDatemodif()));
            preparedStatement.setInt(4, reclamation.getId());

            preparedStatement.executeUpdate();
            System.out.println("Reclamation edited");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (edit) reclamation : " + exception.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM `reclamation` WHERE `id`=?");
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println("Reclamation deleted");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (delete) reclamation : " + exception.getMessage());
        }
        return false;
    }
}
