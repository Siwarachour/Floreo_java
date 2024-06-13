package services;

import java.sql.SQLException;
import java.util.List;

public interface CRUDspon<T> {
    public void ajouter(T t) throws SQLException;
   public void modifierSpon(T t) throws SQLException;
   public void supprimerSP(int id) throws SQLException;
  public List<T> affichersponsor() throws SQLException;
}
