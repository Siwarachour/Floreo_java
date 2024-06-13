package entities;


public class FactureDon {

    private int id;
    private String nomDonateur;
    private String prenomDonateur;
    private String email;
    private String adresses;
    private int numeroTelephone;
    private String description;

    private Don don;


    public FactureDon() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomDonateur() {
        return nomDonateur;
    }

    public void setNomDonateur(String nomDonateur) {
        this.nomDonateur = nomDonateur;
    }

    public String getPrenomDonateur() {
        return prenomDonateur;
    }

    public void setPrenomDonateur(String prenomDonateur) {
        this.prenomDonateur = prenomDonateur;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresses() {
        return adresses;
    }

    public void setAdresses(String adresses) {
        this.adresses = adresses;
    }

    public int getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(int numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Don getDon() {
        return don;
    }

    public void setDon(Don don) {
        this.don = don;
    }


    public String toString() {
        return nomDonateur;
    }
}