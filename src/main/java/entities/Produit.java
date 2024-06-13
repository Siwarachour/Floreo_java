package entities;


public class Produit {

    private int id;
    private String nomp;
    private String descp;
    private String catg;
    private float prix;
    private String image;


    public Produit() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomp() {
        return nomp;
    }

    public void setNomp(String nomp) {
        this.nomp = nomp;
    }

    public String getDescp() {
        return descp;
    }

    public void setDescp(String descp) {
        this.descp = descp;
    }

    public String getCatg() {
        return catg;
    }

    public void setCatg(String catg) {
        this.catg = catg;
    }

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String toString() {
        return nomp;
    }

    public String allAttrToString() {
        return " - Produit - " +
                "\nNom : " + nomp +
                "\nDescription : " + descp +
                "\nCategorie : " + catg +
                "\nPrix : " + prix;
    }
}