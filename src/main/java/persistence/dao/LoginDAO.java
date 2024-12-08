package persistence.dao;

import java.sql.*;
import persistence.dto.LoginDTO;
import persistence.dto.UserDTO;

public class LoginDAO {

    private final Connection connection;

    public LoginDAO(Connection connection) {
        this.connection = connection;
    }

    public UserDTO selectLogInUser(LoginDTO loginDTO) {
        UserDTO userDto = null;
        String query = "SELECT user_id, user_role FROM user WHERE user_id = ? AND user_pwd = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, loginDTO.getLoginId());
            stmt.setString(2, loginDTO.getPassword());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) { // 결과가 있으면
                    userDto = new UserDTO();
                    userDto.setUserRole(rs.getString("user_role"));
                }
                else
                    return userDto;
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return userDto;
    }
}
