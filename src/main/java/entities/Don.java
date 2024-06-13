package entities;


import java.time.LocalDate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class Don {

    private int id;
    private String type;
    private String description;
    private LocalDate dateDon;

    public String selectedType;

    private User user;

    public Don don;


    public Don() {
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

    public void setSelectedType(String type) {
        this.selectedType = type;
    }

    public String getSelectedType() {
        return selectedType;
    }




    public void eraseSelectedType(String type){this.selectedType=null;}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateDon() {
        return dateDon;
    }

    public void setDateDon(LocalDate dateDon) {
        this.dateDon = dateDon;
    }

    public User getUser() {
        return user;
    }

    public void set(User user) {
        this.user = user;
    }


    public Don getDon() {
        return don;
    }

    public void setDon(Don don) {
        this.don = don;
    }


    public String toString() {
        return type;
    }


}