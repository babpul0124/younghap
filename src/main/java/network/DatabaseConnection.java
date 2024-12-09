package network;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/domitory";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "jinchen0807!";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Database 연결 실패: " + e.getMessage(), e);
        }
    }
}
