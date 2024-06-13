package entities;


import java.time.LocalDate;

public class Dechets {

    private int id;
    private String type;
    private LocalDate dateEntre;
    private String description;
    private int quantite;
    private String image;


    public Dechets() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDateEntre() {
        return dateEntre;
    }

    public void setDateEntre(LocalDate dateEntre) {
        this.dateEntre = dateEntre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String toString1() {
        return "Dechets{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", quantite=" + quantite +
                ", image='" + image + '\'' +
                '}';
    }

    public String toString() {
        return
                 type ;
    }

}