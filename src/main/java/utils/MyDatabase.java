package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {

    final String URL = "jdbc:mysql://localhost:3306/floreo";

    final String USERNAME = "root";
    final String PASSWORD = "";
    Connection connection;

    static MyDatabase instance;

    private MyDatabase() {
        try {
            // Optionally load the driver class
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC driver not found.");
        }
    }

    public static synchronized MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("New connection established.");
            return connection;
        } catch (SQLException e) {
            System.out.println("Failed to create a new connection: " + e.getMessage());
            throw new RuntimeException("Failed to create a new connection.", e);
        }
    }
}
