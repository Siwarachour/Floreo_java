package services;

import java.sql.SQLException;
import java.util.List;

public interface CRUDEvent<T>{

    public void ajouterEvenement(T t) throws SQLException;
  public void modifierEvenement(T t) throws SQLException;
     public void supprimerEvenement(int id) throws SQLException;
     public List<T> afficherEV() throws SQLException;
}
