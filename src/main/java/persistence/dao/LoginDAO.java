package persistence.dao;
import persistence.dto.UserDTO;
import java.sql.*;
import java.util.ArrayList;

public class LoginDAO {

    private final Connection connection;
    public LoginDAO(Connection connection) { this.connection = connection;}

    //아이디 패스워드 목록 전송 함수
    public ArrayList<UserDTO> getIdPwList () {

        ArrayList<UserDTO> userIdPws = new ArrayList<>();

        String query = "SELECT login_id, password FROM user";

        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery())
        {
            while (rs.next()) {

                UserDTO userDTO = new UserDTO();

                userDTO.setLogin_id(rs.getString("login_id"));
                userDTO.setPassword(rs.getString("password"));

                userIdPws.add(userDTO);
            }
        }
        catch (SQLException e) {System.out.println("error: " + e);}

        return userIdPws;
    }
}
