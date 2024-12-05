package persistence.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginDAO {

    // 모든 사용자의 로그인 아이디 목록 추출
    public List<String> getAllUserIds() {
        List<String> userIds = new ArrayList<>();
        String sql = "SELECT id FROM userTABLE";  // userTABLE에서 login_id만 선택

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {  // 쿼리 실행

            // 결과 처리 (모든 id를 리스트에 추가)
            while (rs.next()) {
                userIds.add(rs.getString("login_id"));  // id 컬럼의 값을 리스트에 추가
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userIds;  // 모든 사용자 id를 담은 리스트 반환
    }

    // 사용자 아이디를 가지고 패스워드도 맞는지 확인하는 용도.
    public String getPasswordById(String inputId) {
        String password = null;
        String sql = "SELECT pw FROM userTABLE WHERE login_id = ?";  // 입력받은 id에 해당하는 pw를 조회하는 SQL 쿼리

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 입력받은 ID를 쿼리의 파라미터로 설정
            stmt.setString(1, inputId);

            // 쿼리 실행
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    password = rs.getString("pw");  // 결과에서 pw 값을 가져옴
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return password;  // 해당 login_ID의 패스워드 반환, 없으면 null
    }
}
