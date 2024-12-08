package persistence.dao;
import java.sql.*;
import java.util.ArrayList;
import persistence.dto.LoginDTO;
import persistence.dto.UserDTO;

public class LoginDAO {

    private final Connection connection;
    public LoginDAO(Connection connection) { this.connection = connection;}

    public UserDTO selectLogInUser(LoginDTO loginDTO) {
        UserDTO userDto = null;
        String query = "SELECT * FROM user WHERE user_id = ?"; // SQL 쿼리
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, loginDTO.getLoginId()); // 첫 번째 ?에 userId 설정

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userDto = new UserDTO();
                    userDto.setUserRole(rs.getString("user_role")); // user_role 저장
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return userDto;
    }
}

