package entities;

import java.util.Date;

public class Client extends User {
    public Client( String name, String lastname, String password,String roles, String email, String image, int number,boolean is_verified,Date datenaissance) {
        super(name, lastname, password, email,roles, image, number, is_verified,datenaissance);
        setRoles("ROLE_CLIENT");
    }

    public Client(int id, String name, String lastname, String password, String email, String image, int number, boolean is_verified, Date datenaissance) {
        super(id,name, lastname, password, email, "ROLE_CLIENT", image, number, is_verified,datenaissance);
    }

}
