package persistence.dao;
import java.sql.*;
import java.util.ArrayList;
import persistence.dto.LoginDTO;

public class LoginDAO {

    private final Connection connection;
    public LoginDAO(Connection connection) { this.connection = connection;}

    LoginDTO loginDTO = new LoginDTO();

    //아이디 패스워드 목록 전송 함수
    public ArrayList<LoginDTO> getIdPwList() {

        ArrayList<LoginDTO> userIdPws = new ArrayList<>();

        String query = "SELECT login_id, password FROM user";

        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery())
        {
            while (rs.next()) {

                loginDTO.setLoginId(rs.getString("login_id"));
                loginDTO.setPassword(rs.getString("password"));

                userIdPws.add(loginDTO);
            }
        }
        catch (SQLException e) {System.out.println("error: " + e);}

        return userIdPws;
    }

    public void saveIdPw(String loginId, String password) {
        String query = "INSERT INTO user (login_id, password) VALUES (?, ?)";

        loginDTO.setLoginId(loginId);
        loginDTO.setPassword(password);

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, loginDTO.getLoginId()); // login_id 설정
            stmt.setString(2, loginDTO.getPassword()); // password 설정

            int rowsAffected = stmt.executeUpdate(); // 쿼리 실행
            if (rowsAffected > 0) {
                System.out.println("성공적으로 등록함.");
            } else {
                System.out.println("등록 못함.");
            }
        } catch (SQLException e) {
            System.out.println("문제 생겼음: " + e.getMessage());
        }
    }
}

