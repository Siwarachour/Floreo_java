
package entities;


import java.util.Date;

public class User {
    private int id;
    private String name;
    private String lastname;
    private String password;
    private String email;
    private String roles;
    private String image;
    private int number;
    private Date datenaissance;

    public String getQr_code() {
        return qr_code;
    }

    public boolean isIs_verified() {
        return is_verified;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    private String qr_code;

    public void setIs_banned(boolean is_banned) {
        this.is_banned = is_banned;
    }

    private boolean is_verified;

    public boolean isIs_banned() {
        return is_banned;
    }

    private  boolean is_banned;

    public User() {

    }





    public User(int id, String name, String lastname, String password, String email, String roles, String image, int number, boolean is_verified, Date datenaissance) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.image = image;
        this.number = number;
        this.is_verified = is_verified;
        this.datenaissance= datenaissance;
    }



    public User(String name, String lastname, String password, String email, String roles, String image, int number, boolean is_verified, Date datenaissance) {

        this.name = name;
        this.lastname = lastname;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.image = image;
        this.number = number;
        this.is_verified = is_verified;
        this.datenaissance=datenaissance;
    }
    public User(String name, String lastname, String password, String email, String roles, String image, int number, boolean is_verified, Date datenaissance,String qr_code,Boolean is_banned) {

        this.name = name;
        this.lastname = lastname;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.image = image;
        this.number = number;
        this.is_verified = is_verified;
        this.datenaissance=datenaissance;
        this.qr_code=qr_code;
        this.is_banned=is_banned;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public void setIs_verified(boolean is_verified) {this.is_verified = is_verified;}
    public void setDatenaissance(Date datenaissance) {this.datenaissance = datenaissance;}


    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name=" + name + ", lastname=" + lastname + ", password=" + password + ", email=" + email + ", roles=" + roles + ", image=" + image + ", Number="+number+" , is_verified="+is_verified+" , datenaissance"+datenaissance+'}';
    }


    public Date getDatenaissance() {return  datenaissance;}

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getRoles() {
        return roles;
    }

    public String getImage() {
        return image;
    }
    public int getNumber() {
        return number;
    }

    public boolean getIs_verified() {return is_verified;}
    public User(String name, String lastname, String password, String email, String roles, String image, int number, boolean is_verified, Date datenaissance,boolean is_banned) {
        this.name = name;
        this.lastname = lastname;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.image = image;
        this.number = number;
        this.is_verified = is_verified;

        this.datenaissance = datenaissance;
        this.is_banned = is_banned;
    }



}
