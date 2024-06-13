package entities;

import java.util.List;
import java.util.Set;
import javax.persistence.*;

public class Evenement {

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvenementUser> evenementUsers;
    private int id, sponsor_id;
    private String nameevent,lieu,type,description;
    private String datedebut, datefin;
    private String image;
    private int nbparticipant;
    private Sponsor sponsor;

    public Evenement() {
    }

    public List<EvenementUser> getEvenementUsers() {
        return evenementUsers;
    }

    public void setEvenementUsers(List<EvenementUser> evenementUsers) {
        this.evenementUsers = evenementUsers;
    }

    public Evenement(List<EvenementUser> evenementUsers, int id, int sponsor_id, String nameevent, String lieu, String type, String description, String datedebut, String datefin, String image, int nbparticipant, Sponsor sponsor) {
        this.evenementUsers = evenementUsers;
        this.id = id;
        this.sponsor_id = sponsor_id;
        this.nameevent = nameevent;
        this.lieu = lieu;
        this.type = type;
        this.description = description;
        this.datedebut = datedebut;
        this.datefin = datefin;
        this.image = image;
        this.nbparticipant = nbparticipant;
        this.sponsor = sponsor;
    }

    //constructeur meghyr id meghyr sponsor obj
    public Evenement(int sponsor_id , String nameevent, String type, String datedebut , String datefin, String description, int nbparticipant, String lieu, String image ) {
        this.sponsor_id = sponsor_id;
        this.nameevent = nameevent;
        this.lieu = lieu;
        this.type = type;

        this.datedebut = datedebut;
        this.datefin = datefin;
        this.description = description;
        this.image = image;
        this.nbparticipant = nbparticipant;

    }

    public Evenement(int id, int sponsor_id, String nameevent, String lieu, String type, String datedebut, String datefin, String description,  String image, int nbparticipant, Sponsor sponsor) {
        this.id = id;
        this.sponsor_id = sponsor_id;
        this.nameevent = nameevent;
        this.lieu = lieu;
        this.type = type;

        this.datedebut = datedebut;
        this.datefin = datefin;
        this.description = description;
        this.image = image;
        this.nbparticipant = nbparticipant;
        this.sponsor = sponsor;
    }

    public Evenement(int id, int sponsor_id , String nameevent, String type, String datedebut, String datefin,String description, int nbparticipant, String lieu , String image) {
        this.id = id;
        this.sponsor_id = sponsor_id;
        this.nameevent = nameevent;
        this.lieu = lieu;
        this.type = type;
        this.description = description;
        this.datedebut = datedebut;
        this.datefin = datefin;
        this.image = image;
        this.nbparticipant = nbparticipant;

    }
    public int getId() {
        return id;
    }

    public int getSponsor_id() {
        return sponsor_id;
    }

    public void setSponsor_id(int sponsor_id) {
        this.sponsor_id = sponsor_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameevent() {
        return nameevent;
    }

    public void setNameevent(String nameevent) {
        this.nameevent = nameevent;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatedebut() {
        return datedebut;
    }

    public void setDatedebut(String datedebut) {
        this.datedebut = datedebut;
    }

    public String getDatefin() {
        return datefin;
    }

    public void setDatefin(String datefin) {
        this.datefin = datefin;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getNbparticipant() {
        return nbparticipant;
    }

    public void setNbparticipant(int nbparticipant) {
        this.nbparticipant = nbparticipant;
    }

    public Sponsor getSponsor() {
        return sponsor;
    }

    public void setSponsor(Sponsor sponsor) {
        this.sponsor = sponsor;
    }




    @Override
    public String toString() {
        return "Evenement{" +
                "id=" + id +
                ", sponsor_id=" + sponsor_id +
                ", nameevent='" + nameevent + '\'' +
                ", lieu='" + lieu + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", datedebut='" + datedebut + '\'' +
                ", datefin='" + datefin + '\'' +
                ", image='" + image + '\'' +
                ", nbparticipant=" + nbparticipant +
                ", sponsor=" + sponsor +


                '}';
    }
}
