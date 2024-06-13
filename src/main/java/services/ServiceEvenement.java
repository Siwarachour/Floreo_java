package services;

import entities.Evenement;
import entities.Sponsor;
import utils.DatabaseConnection;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.sql.DriverManager.getConnection;


public class ServiceEvenement implements CRUDEvent<Evenement> {
    Connection connection;

    public ServiceEvenement() {
        connection = MyDatabase.getInstance().getConnection();

    }

    @Override

    public void ajouterEvenement(Evenement r) {
        try {
            String requete1 = "INSERT INTO evenement(sponsor_id,nameevent,type, datedebut, datefin, description,nbparticipant, lieu, image) VALUES(?,?,?,STR_TO_DATE(?, '%d/%m/%Y'),STR_TO_DATE(?, '%d/%m/%Y'),?,?,?,?)";
            PreparedStatement pst = MyDatabase.getInstance().getConnection().prepareStatement(requete1);
            pst.setInt(1, r.getSponsor_id());
            pst.setString(2, r.getNameevent());
            pst.setString(3, r.getType());
            pst.setString(4, r.getDatedebut());
            pst.setString(5, r.getDatefin());
            pst.setString(6, r.getDescription());
            pst.setInt(7, r.getNbparticipant());
            pst.setString(8, r.getLieu());
            pst.setString(9, r.getImage());

            pst.executeUpdate();
            System.out.println("Evenement ajouté avec succès !");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void supprimerEvenement(int id) {
        try {
            String requete = "DELETE FROM evenement WHERE id=?";
            PreparedStatement pst = MyDatabase.getInstance().getConnection().prepareStatement(requete);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Evenement supprimé avec succès !");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void modifierEvenement(Evenement r) {
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = MyDatabase.getInstance().getConnection();
            String requete = "UPDATE evenement SET sponsor_id=?, nameevent=?, type=?, datedebut=STR_TO_DATE(?, '%Y-%m-%d'), datefin=STR_TO_DATE(?, '%Y-%m-%d'), description=?, nbparticipant=?, lieu=?, image=? WHERE id=?";
            pst = conn.prepareStatement(requete);
            pst.setInt(1, r.getSponsor_id());
            pst.setString(2, r.getNameevent());
            pst.setString(3, r.getType());
            pst.setString(4, r.getDatedebut()); // Ensure this matches the database expected format
            pst.setString(5, r.getDatefin()); // Ensure this matches the database expected format
            pst.setString(6, r.getDescription());
            pst.setInt(7, r.getNbparticipant());
            pst.setString(8, r.getLieu());
            pst.setString(9, r.getImage());
            pst.setInt(10, r.getId());

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Evenement modifié avec succès!");
            } else {
                System.out.println("Aucun enregistrement mis à jour.");
            }
        } catch (SQLException ex) {
            System.err.println("Error updating event: " + ex.getMessage());
            ex.printStackTrace(); // Log the stack trace for more detailed error information
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public List<Evenement> fetchSelectedFields() {
        List<Evenement> liste = new ArrayList<>();
        try {
            String requete = "SELECT id, nameevent, datedebut, datefin FROM evenement";
            Statement st = MyDatabase.getInstance().getConnection().createStatement();
            ResultSet rs = st.executeQuery(requete);
            while (rs.next()) {
                Evenement evenement = new Evenement();
                evenement.setId(rs.getInt("id"));
                evenement.setNameevent(rs.getString("nameevent"));
                evenement.setDatedebut(rs.getString("datedebut"));
                evenement.setDatefin(rs.getString("datefin"));
                liste.add(evenement);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return liste;
    }

    @Override
    public List<Evenement> afficherEV() {
        List<Evenement> liste = new ArrayList<>();
        try {
            String requete = "SELECT * FROM evenement";
            Statement st = MyDatabase.getInstance().getConnection().createStatement();
            ResultSet rs = st.executeQuery(requete);
            while (rs.next()) {
                Evenement evenement;
                evenement = new Evenement(
                        rs.getInt("id"),
                        rs.getInt("sponsor_id"),
                        rs.getString("nameevent"),
                        rs.getString("type"),
                        rs.getString("datedebut"),
                        rs.getString("datefin"),
                        rs.getString("description"),
                        rs.getInt("nbparticipant"),
                        rs.getString("lieu"),
                        rs.getString("image")


                );
                liste.add(evenement);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return liste;
    }


    public Sponsor getSponsorById(int id) {
        Sponsor sponsor = null;
        try {
            Connection conn = MyDatabase.getInstance().getConnection();
            String query = "SELECT * FROM sponsor WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                sponsor = new Sponsor();
                sponsor.setId(rs.getInt("id"));
                sponsor.setName(rs.getString("name"));
                sponsor.setEmail(rs.getString("email"));
                // Compléter avec les autres champs
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sponsor;
    }

    public String getNomSponsor(Sponsor sponsor) {
        String nomSponsor = null;
        try {
            Connection connection = MyDatabase.getInstance().getConnection();
            String query = "SELECT name FROM sponsor WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, sponsor.getId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                nomSponsor = resultSet.getString("name");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return nomSponsor;
    }

    public Integer getIdSponsorFromNom(Sponsor sponsor) {
        Integer id = null;
        try (PreparedStatement pstmt = MyDatabase.getInstance().getConnection().prepareStatement("SELECT id  FROM Sponsor WHERE nom_sponsor= ?")) {
            pstmt.setString(1, sponsor.getName());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("ID du sponsor : " + id);
        return id;
    }

    public String getNomSponsor(int id) {
        String nom = "";
        try (PreparedStatement pstmt = MyDatabase.getInstance().getConnection().prepareStatement("SELECT name FROM sponsor WHERE id= ?")) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                nom = rs.getString("name");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return nom;
    }

    public List<Evenement> afficherEVWithSponsors() {
        List<Evenement> evenements = new ArrayList<>();
        try {
            Connection conn = MyDatabase.getInstance().getConnection();
            String query = "SELECT e.*, s.* FROM evenement e JOIN sponsor s ON e.sponsor_id = s.id";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Sponsor sponsor = new Sponsor();
                sponsor.setId(rs.getInt("s.id"));
                sponsor.setName(rs.getString("s.name"));
                // Populate other sponsor fields if needed

                Evenement evenement = new Evenement();
                evenement.setId(rs.getInt("e.id"));
                evenement.setSponsor_id(rs.getInt("e.sponsor_id"));
                evenement.setNameevent(rs.getString("e.nameevent"));

                evenement.setType(rs.getString("e.type"));

                evenement.setDatedebut(rs.getString("e.datedebut"));
                evenement.setDatefin(rs.getString("e.datefin"));
                evenement.setDescription(rs.getString("e.description"));
                evenement.setNbparticipant(rs.getInt("e.nbparticipant"));
                evenement.setLieu(rs.getString("e.lieu"));
                evenement.setImage(rs.getString("e.image"));

                evenement.setSponsor(sponsor);

                evenements.add(evenement);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evenements;
    }


    public String getSponsorNameById(int sponsorId) {
        String sponsorName = "";
        try {
            String requete = "SELECT name FROM sponsor WHERE id = ?";
            PreparedStatement pst = MyDatabase.getInstance().getConnection().prepareStatement(requete);
            pst.setInt(1, sponsorId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                sponsorName = rs.getString("name");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return sponsorName;
    }

    //for sponsor
    public int getIdSponsor(String nom) {
        int id = 0;
        try (PreparedStatement pstmt = MyDatabase.getInstance().getConnection().prepareStatement("SELECT id FROM sponsor WHERE name= ?")) {
            pstmt.setString(1, nom);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return id;
    }


    // Method to add a user to an event
  /*  public void addUserToEvent(int userId, int eventId) {
        String sql = "INSERT INTO evenement_user (evenement_id, user_id) VALUES (?, ?)";

        try (Connection conn = MyDatabase.getInstance().getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, eventId);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Method to remove a user from an event
    public void removeUserFromEvent(int userId, int eventId) {
        String sql = "DELETE FROM evenement_user WHERE user_id = ? AND evenement_id = ?";

        try (Connection conn = MyDatabase.getInstance().getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, eventId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Method to get all events for a user
    public List<Evenement> getEventsForUser(int userId) {
        List<Evenement> events = new ArrayList<>();
        String sql = "SELECT e.* FROM evenement e JOIN evenement_user eu ON e.id = eu.evenement_id WHERE eu.user_id = ?";

        try (Connection conn = MyDatabase.getInstance().getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                // Populate Evenement object and add to the list
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return events;
    }
}





*/




    public void addUserToEvent(int userId, int eventId) {
        String sql = "INSERT INTO evenement_user (evenement_id, user_id) VALUES (?, ?)";
        try (Connection conn = MyDatabase.getInstance().getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, eventId);
            statement.setInt(2, userId);
            statement.executeUpdate();
            System.out.println("User " + userId + " added to event " + eventId);
        } catch (SQLException ex) {
            System.err.println("Error adding user to event: " + ex.getMessage());
            throw new RuntimeException("Failed to add user to event", ex);
        }
    }
    public void updateParticipantsCount(int eventId) {
        Connection connection = null;
        PreparedStatement countStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet countResult = null;

        try {
            // Get a connection from the connection pool
            connection = MyDatabase.getInstance().getConnection();

            // Get the current number of participants for the event
            String countQuery = "SELECT nbparticipant FROM evenement WHERE id = ?";
            countStmt = connection.prepareStatement(countQuery);
            countStmt.setInt(1, eventId);
            countResult = countStmt.executeQuery();

            int currentParticipants = 0;
            if (countResult.next()) {
                currentParticipants = countResult.getInt("nbparticipant");
            }

            // Decrement the number of participants for the event
            String updateQuery = "UPDATE evenement SET nbparticipant = ? WHERE id = ?";
            updateStmt = connection.prepareStatement(updateQuery);
            updateStmt.setInt(1, currentParticipants - 1);
            updateStmt.setInt(2, eventId);
            updateStmt.executeUpdate();

            System.out.println("Participants count updated successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // Close all resources in reverse order
            try {
                if (countResult != null) countResult.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (countStmt != null) countStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (updateStmt != null) updateStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    //SYSTEM RECC SELON DATE


    // Method to recommend events based on proximity to the current date
// Method to recommend the three closest upcoming events after today's date
    public List<Evenement> recommendThreeClosestEvents() {
        int numberOfRecommendations = 3; // Specify the number of recommendations
        return recommendEventsByProximity(numberOfRecommendations);
    } //This method is a public interface to recommend the three closest upcoming events after today's date.

    // Method to recommend events based on proximity to the current date
    public List<Evenement> recommendEventsByProximity(int numberOfRecommendations) {
        List<Evenement> allEvents = afficherEV(); // Get all events
        return recommendEvents(allEvents, numberOfRecommendations);
    }

    // Method to recommend events based on proximity to the current date
    private List<Evenement> recommendEvents(List<Evenement> events, int numberOfRecommendations) {
        return events.stream()
                .filter(event -> isFutureEvent(event.getDatedebut()))
                .sorted(Comparator.comparingInt(event -> calculateProximity(event.getDatedebut())))
                .limit(numberOfRecommendations)
                .collect(Collectors.toList());
    }

    // Method to calculate proximity of events to the current date
    private int calculateProximity(String eventDate) {
        LocalDate currentDate = LocalDate.now();
        LocalDate eventLocalDate = LocalDate.parse(eventDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return Math.abs(currentDate.compareTo(eventLocalDate));
    }

    // Method to check if the event date is in the future
    private boolean isFutureEvent(String eventDate) {
        LocalDate currentDate = LocalDate.now();
        LocalDate eventLocalDate = LocalDate.parse(eventDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return eventLocalDate.isAfter(currentDate);
    }
}

/*
    public void addUserToEvent(int userId, int eventId) {
        try {
            // Get the current number of participants for the event
            String countQuery = "SELECT nbparticipant FROM evenement WHERE id = ?";
            PreparedStatement countStmt = connection.prepareStatement(countQuery);
            countStmt.setInt(1, eventId);
            ResultSet countResult = countStmt.executeQuery();

            int currentParticipants = 0;
            if (countResult.next()) {
                currentParticipants = countResult.getInt("nbparticipant");
            }

            // Check if there are available slots
            if (currentParticipants > 0) {
                // Check if the user is already added to this event
                String checkQuery = "SELECT COUNT(*) AS count FROM evenement_user WHERE evenement_id = ? AND user_id = ?";
                PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
                checkStmt.setInt(1, eventId);
                checkStmt.setInt(2, userId);
                ResultSet checkResult = checkStmt.executeQuery();
                if (checkResult.next()) {
                    int count = checkResult.getInt("count");
                    if (count > 0) {
                        System.out.println("User is already added to this event.");
                        return; // Exit the method if the user is already added
                    }
                }

                // Insert the user into the event
                String insertQuery = "INSERT INTO evenement_user (evenement_id, user_id) VALUES (?, ?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                insertStmt.setInt(1, eventId);
                insertStmt.setInt(2, userId);
                insertStmt.executeUpdate();

                // Decrement the number of participants for the event
                String updateQuery = "UPDATE evenement SET nbparticipant = ? WHERE id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setInt(1, currentParticipants - 1);
                updateStmt.setInt(2, eventId);
                updateStmt.executeUpdate();

                System.out.println("User added to event successfully!");
            } else {
                System.out.println("No available slots for this event.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }}


*/


/*

    // Method to decrement available places for an event
    public void decrementAvailablePlaces(int eventId) {
        // SQL query to decrement available places for the given event
        String sql = "UPDATE evenement SET nbparticipant = nbparticipant - 1 WHERE id = ?";

        try (  Connection conn = MyDatabase.getInstance().getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, eventId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Method to add user and event to the join table
    public void addToCart(int userId, int eventId) {
        // SQL query to insert user and event IDs into the join table
        String sql = "INSERT INTO evenement_user (evenement_id, user_id) VALUES (?, ?)";

        try (  Connection conn = MyDatabase.getInstance().getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, eventId);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
*/
