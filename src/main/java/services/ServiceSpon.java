package services;

import entities.Sponsor;
import utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceSpon implements CRUDspon<Sponsor>{
    Connection connection;
    public ServiceSpon(){
        connection= MyDatabase.getInstance().getConnection();

    }
    @Override
    //ajouuttt
    public void ajouter(Sponsor sponsor) throws SQLException {
        String req = "insert into sponsor (name,email,number) values (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setString(1, sponsor.getName());
        preparedStatement.setString(2, sponsor.getEmail());
        preparedStatement.setInt(3, sponsor.getNumber());
        preparedStatement.executeUpdate();
        System.out.println("Sponsor added");
    }
//DELETESPONNN
    @Override
    public void supprimerSP(int id) {
        try {
            String requete = "DELETE FROM sponsor WHERE id=?";
            PreparedStatement pst = MyDatabase.getInstance().getConnection().prepareStatement(requete);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("sponsor supprimé avec succès !");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
//UPDATESPONNNN
@Override
public void modifierSpon(Sponsor s) {
    try {
        String requete = "UPDATE sponsor SET name=?, email=?, number=? WHERE id=?";
        PreparedStatement pst = MyDatabase.getInstance().getConnection().prepareStatement(requete);
        pst.setString(1, s.getName());
        pst.setString(2, s.getEmail());
        pst.setInt(3, s.getNumber());
        pst.setInt(4, s.getId());
        pst.executeUpdate();
        System.out.println("Sponsor modifié");
    } catch (SQLException ex) {
        System.err.println(ex.getMessage());
    }
}
//display sponsor
    @Override
    public List<Sponsor> affichersponsor() {
        List<Sponsor> sponsorList = new ArrayList<>();
        try {
            String requete = "SELECT * FROM sponsor";
            PreparedStatement pst = MyDatabase.getInstance().getConnection().prepareStatement(requete);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Sponsor sponsor = new Sponsor(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getInt("number"));
                sponsorList.add(sponsor);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return sponsorList;
    }



    //function FOR STATTTTTTTTTTTT



    public List<Map<String, Object>> getSponsorEvents() {
        List<Map<String, Object>> sponsorEvents = new ArrayList<>();
        try {
            String query = "SELECT s.name AS sponsorName, COUNT(e.id) AS eventCount " +
                    "FROM sponsor s " +
                    "JOIN evenement e ON s.id = e.sponsorId " +
                    "GROUP BY s.name";
            PreparedStatement pst = connection.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("sponsorName", rs.getString("sponsorName"));
                row.put("eventCount", rs.getInt("eventCount"));
                sponsorEvents.add(row);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return sponsorEvents;
    }
    }



