package services;

import entities.User;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public interface UserCrud<T> {

    public void add(T user) throws SQLException;

    public void update (T user);

    public void delete(T user);
    public List<User> show();

    void signUp(User user);



    void updateEmail(int userId, String newEmail);

    void updateName(User user);

    void updateRole(User user);

    void updateLastName(User user);

    List<User> sortByEmail();

    List<User> sortByName();

    List<User> sortById();

    List<User> searchByName(String name);

    User searchById(int id);


    List<User> searchByDateOfBirth(Date dateOfBirth);

    boolean isEmailUsed(String email);

    boolean signup(User user);

    boolean login(String email, String password);

    String loginQr(User user);

    User getUserByEmail(String email);


    String getRole(String email);

    void testEamil(String email);


    boolean banUser(User user) throws SQLException;

    void unbanUser(User user) throws SQLException;


}
