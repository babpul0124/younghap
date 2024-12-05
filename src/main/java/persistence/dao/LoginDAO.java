package persistence.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginDAO {

    // 모든 사용자의 로그인 아이디 목록 추출 함수
    public List<String> getAllUserIds() {
        
        List<String> userIds = new ArrayList<>();

        // userTABLE에서 login_id만 선택
        String sql = "SELECT login_id FROM userTABLE"; 

        try (Connection conn = DBConnection.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) 
        {  // 쿼리 실행
            
            // 결과 처리 (모든 id를 리스트에 추가)
            while (rs.next()) { userIds.add(rs.getString("login_id"));  }
        } 
        catch (SQLException e) { e.printStackTrace();}

        // 모든 사용자 id를 담은 리스트 반환
        return userIds; 
    }

    // 사용자 아이디를 가지고 패스워드도 맞는지 확인하는 용도.
    public String getPasswordById(String inputId) {
        
        String pw = null;

        // 입력받은 id에 해당하는 pw를 조회하는 SQL 쿼리
        String sql = "SELECT password FROM userTABLE WHERE login_id = ?";  

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, inputId);

            // 쿼리 실행
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    pw = rs.getString("password");  // 결과에서 pw 값을 가져옴
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 해당 login_ID의 패스워드 반환, 없으면 null
        return pw;  
    }
}
