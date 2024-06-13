package services;

import entities.Reclamation;
import entities.Reponse;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReponseService {

    private static ReponseService instance;
    PreparedStatement preparedStatement;
    Connection connection;

    public ReponseService() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public static ReponseService getInstance() {
        if (instance == null) {
            instance = new ReponseService();
        }
        return instance;
    }

    public List<Reponse> getAll() {
        List<Reponse> listReponse = new ArrayList<>();
        try {

            String query = "SELECT * FROM `reponse` AS x "
                    + "RIGHT JOIN `reclamation` AS y1 ON x.reclamation_id = y1.id "
                    + "WHERE  x.reclamation_id = y1.id  ";
            preparedStatement = connection.prepareStatement(query);


            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Reponse reponse = new Reponse();
                reponse.setId(resultSet.getInt("id"));
                reponse.setDescription(resultSet.getString("description"));
                reponse.setDateajout(resultSet.getDate("dateajout") != null ? resultSet.getDate("dateajout").toLocalDate() : null);
                reponse.setDatemodif(resultSet.getDate("datemodif") != null ? resultSet.getDate("datemodif").toLocalDate() : null);

                Reclamation reclamation = new Reclamation();
                reclamation.setId(resultSet.getInt("y1.id"));
                reclamation.setType(resultSet.getString("y1.type"));
                reponse.setReclamation(reclamation);

                listReponse.add(reponse);
            }
        } catch (SQLException exception) {
            System.out.println("Error (getAll) reponse : " + exception.getMessage());
        }
        return listReponse;
    }

    public List<Reclamation> getAllReclamations() {
        List<Reclamation> listReclamations = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM `reclamation`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Reclamation reclamation = new Reclamation();
                reclamation.setId(resultSet.getInt("id"));
                reclamation.setType(resultSet.getString("type"));
                listReclamations.add(reclamation);
            }
        } catch (SQLException exception) {
            System.out.println("Error (getAll) reclamations : " + exception.getMessage());
        }
        return listReclamations;
    }


    public boolean add(Reponse reponse) {


        String request = "INSERT INTO `reponse`(`description`, `dateajout`, `datemodif`, `reclamation_id`) VALUES(?, ?, ?, ?)";

        try {
            preparedStatement = connection.prepareStatement(request);

            preparedStatement.setString(1, reponse.getDescription());
            preparedStatement.setDate(2, Date.valueOf(reponse.getDateajout()));
            preparedStatement.setDate(3, Date.valueOf(reponse.getDatemodif()));
            preparedStatement.setInt(4, reponse.getReclamation().getId());


            preparedStatement.executeUpdate();
            System.out.println("Reponse added");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (add) reponse : " + exception.getMessage());
        }
        return false;
    }

    public boolean edit(Reponse reponse) {

        String request = "UPDATE `reponse` SET `description` = ?, `datemodif` = ? WHERE `id` = ?";

        try {
            preparedStatement = connection.prepareStatement(request);

            preparedStatement.setString(1, reponse.getDescription());
            preparedStatement.setDate(2, Date.valueOf(reponse.getDatemodif()));
            preparedStatement.setInt(3, reponse.getId());

            preparedStatement.executeUpdate();
            System.out.println("Reponse edited");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (edit) reponse : " + exception.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM `reponse` WHERE `id`=?");
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println("Reponse deleted");
            return true;
        } catch (SQLException exception) {
            System.out.println("Error (delete) reponse : " + exception.getMessage());
        }
        return false;
    }
}
