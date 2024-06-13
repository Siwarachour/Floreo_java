package entities;

import java.util.Date;

public class Fournisseur extends User {
    public Fournisseur(String name, String lastname, String password, String roles, String email, String image, int number, boolean is_verified, Date datenaissance) {
        super(name, lastname, password, email,roles, image, number, is_verified,datenaissance);
        setRoles("ROLE_FOURNISSEUR");
    }

    public Fournisseur(int id,String name, String lastname, String password, String email, String image, int number , boolean is_verified, Date datenaissance) {
        super(id,name, lastname, password, email, "ROLE_FOURNISSEUR", image, number, is_verified,datenaissance);
    }

}
