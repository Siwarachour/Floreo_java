package entities;


import java.time.LocalDate;

public class Commande implements Comparable<Commande> {

    private int id;
    private float montant;
    private LocalDate datecmd;
    private String lieucmd;
    private int quantite;

    private User user;
    private Produit produit;


    public Commande() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getMontant() {
        return montant;
    }

    public void setMontant(float montant) {
        this.montant = montant;
    }

    public LocalDate getDatecmd() {
        return datecmd;
    }

    public void setDatecmd(LocalDate datecmd) {
        this.datecmd = datecmd;
    }

    public String getLieucmd() {
        return lieucmd;
    }

    public void setLieucmd(String lieucmd) {
        this.lieucmd = lieucmd;
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

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }


    public String toString() {
        return lieucmd;
    }

    public static String compareVar = "";

    @Override
    public int compareTo(Commande commande) {
        return switch (compareVar) {
            case "Tri par montant" -> Float.compare(commande.montant, this.montant);
            case "Tri par date" -> commande.datecmd.compareTo(this.datecmd);
            case "Tri par lieu" -> commande.lieucmd.compareTo(this.lieucmd);
            case "Tri par quantite" -> Integer.compare(commande.quantite, this.quantite);
            case "Tri par produit" -> {
                if (this.produit == null || commande.produit == null) {
                    yield 0;
                }
                yield this.produit.getNomp().compareTo(commande.produit.getNomp());
            }
            default -> 0;
        };
    }
}