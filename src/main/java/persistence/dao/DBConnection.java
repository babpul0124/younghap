// 검토 완

package persistence.dao;

import java.sql.*;

public class DBConnection {
    private static Connection conn = null;

    private static final String URL = "jdbc:mysql://127.0.0.1/domitory";
    private static final String USER = "root";  // 사용자명
    private static final String PASSWORD = "jinchen0807!";  // 비밀번호

    private DBConnection() { }

    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {  // 연결이 없거나 닫혀 있으면 새로 연결
            try {
                // JDBC 드라이버 로드 (최초 1회만 필요)
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println("Error: " + e.getMessage());
                throw new SQLException("Failed to connect to the database", e);
            }
        }
        return conn;  // 연결이 이미 있으면 기존 연결을 반환
    }

    public static void closeConnection() {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}
        }
    }
}
