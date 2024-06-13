package entities;

public class Sponsor {
    private int id, number;
    private String name, email;

    public Sponsor(String name, String email , int number ) {

        this.number = number;
        this.name = name;
        this.email = email;
    }

    public Sponsor(int id, String name, String email , int number) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.email = email;
    }

    public Sponsor() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Sponsor{" +
                "id=" + id +
                ", number=" + number +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
