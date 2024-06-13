package entities;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

public class ReservationDechets {

    private int id;
    private LocalDate date;
    private LocalDate dateRamassage;
    private String nomFournisseur;
    private String numeroTell;
    private int quantite;

    private User user;

    private Dechets dechets;


    public ReservationDechets() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDateRamassage() {
        return dateRamassage;
    }

    public void setDateRamassage(LocalDate dateRamassage) {
        this.dateRamassage = dateRamassage;
    }

    public String getNomFournisseur() {
        return nomFournisseur;
    }

    public void setNomFournisseur(String nomFournisseur) {
        this.nomFournisseur = nomFournisseur;
    }

    public String getNumeroTell() {
        return numeroTell;
    }

    public void setNumeroTell(String numeroTell) {
        this.numeroTell = numeroTell;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public String toString() {
        return nomFournisseur;
    }

    public Dechets getDechets() {
        return dechets;
    }

    public void setDechets(Dechets dechets) {
        this.dechets = dechets;
    }
    public String getFormattedReservation() {
        // Define a date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Format the date into a string
        String formattedDate = date.format(formatter);

        return "Date:"+formattedDate;
    }


    public String ToDate() {
        return  dechets.getType()+"\n"+quantite;
    }

}