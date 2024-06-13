package services;

import entities.Produit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface Iservice<T> {
    public List<T> getAll() ;


    public boolean add(T t);

    public boolean edit(T t);

    public boolean delete(int id);
}
