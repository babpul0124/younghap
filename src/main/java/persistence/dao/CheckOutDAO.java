package persistence.dao;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import persistence.dto.CheckOutDTO;


public class CheckOutDAO {

    private final Connection connection;
    public CheckOutDAO(Connection connection) {this.connection = connection;}

    CheckOutDTO checkOutDTO = new CheckOutDTO();

    // 퇴실 신청 권한 확인 (1) 상태가 승인인 학생아이디 리스트 전송
    public ArrayList<CheckOutDTO> getApprovedStudentIds() {

        ArrayList<CheckOutDTO> approvedStudentIds = new ArrayList<>();

        String query = "SELECT student_id FROM application WHERE application_status = '승인'";

        try (PreparedStatement statement = connection.prepareStatement(query); ResultSet rs = statement.executeQuery())
        {
            while (rs.next()) {

                 checkOutDTO.setUserId(rs.getInt("student_id"));

                 approvedStudentIds.add(checkOutDTO); // 승인된 student_id 추가
            }
        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}

        return approvedStudentIds; // 결과 리스트 반환
    }

    //  퇴실 신청 권한 확인 (2) check_out 테이블에 있는 학생아이디 리스트 전송.
    public ArrayList<CheckOutDTO> getStudentIdsFromCheckOut() {

        ArrayList<CheckOutDTO> studentIds = new ArrayList<>();

        String query = "SELECT student_id FROM check_out";

        try (PreparedStatement statement = connection.prepareStatement(query); ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {

                checkOutDTO.setUserId(rs.getInt("student_id"));

                studentIds.add(checkOutDTO); // student_id 추가
            }
        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}

        return studentIds; // 결과 리스트 반환
    }

    // 퇴사 신청 받아서 db에 저장.
    public void processStudentCheckOut(int userId, LocalDateTime checkOutDate, String bankName, String accountNum) {

        checkOutDTO.setUserId(userId);
        checkOutDTO.setCheckOutDate(checkOutDate);
        checkOutDTO.setBankName(bankName);
        checkOutDTO.setAccountNum(accountNum);

        // dormitory_id를 찾는 쿼리
        String findDormitoryIdQuery = "SELECT a.dormitory_id " +
                "FROM application a " +
                "INNER JOIN passed p ON a.application_id = p.application_id " +
                "WHERE a.student_id = ? AND p.isPayment = '납부'";

        try (PreparedStatement statement = connection.prepareStatement(findDormitoryIdQuery)) {

            statement.setInt(1, checkOutDTO.getUserId()); // student_id 설정

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {

                    checkOutDTO.setDormitoryId(rs.getInt("dormitory_id"));
                }
            }
        } catch (SQLException e) {

            System.out.println("Error while finding dormitory_id: " + e.getMessage());
            return; // dormitory_id를 찾지 못했으므로 함수 종료
        }

            // check_out 테이블에 데이터 삽입
            String insertCheckOutQuery = "INSERT INTO check_out (dormitory_id, student_id, check_out_date, bank_name, account_num) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(insertCheckOutQuery)) {

                statement.setInt(1, checkOutDTO.getDormitoryId());  // dormitory_id 설정
                statement.setInt(2, checkOutDTO.getUserId());             // student_id 설정
                statement.setTimestamp(3, Timestamp.valueOf(checkOutDTO.getCheckOutDate())); // check_out_date 설정
                statement.setString(4, checkOutDTO.getBankName());    // bank_name 설정
                statement.setString(5, checkOutDTO.getAccountNum());  // account_num 설정

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("퇴사 정보가 성공적으로 저장되었습니다.");
                }
            } catch (SQLException e) {
                System.out.println("Error while inserting check-out data: " + e.getMessage());

        }
    }


    // 학생 아이디로 dormitory_id를 찾는 메서드
    private int getDormitoryIdByStudentId(int userId) {

        int findDormitoryId = 0;
        checkOutDTO.setUserId(userId);

        String query = "SELECT a.dormitory_id " +
                "FROM application a " +
                "INNER JOIN passed p ON a.application_id = p.application_id " +
                "WHERE a.student_id = ? AND p.isPayment = '납부'";

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, checkOutDTO.getUserId()); // student_id 설정

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {

                    checkOutDTO.setDormitoryId(rs.getInt("dormitory_id"));

                    findDormitoryId = checkOutDTO.getDormitoryId();
                }
            } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}
        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}

        return findDormitoryId; // dormitory_id 반환 (없으면 null)
    }

    // 퇴사 신청자 조회
    public ArrayList<CheckOutDTO> getCheckOutListByDormitoryId(int dormitoryId) {

        checkOutDTO.setDormitoryId(dormitoryId);

        ArrayList<CheckOutDTO> checkOutList = new ArrayList<>();

        String query = "SELECT dormitory_id, student_id, check_out_date, bank_name, account_num, check_out_status " +
                "FROM check_out WHERE dormitory_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, checkOutDTO.getDormitoryId());  // dormitory_id 설정

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {

                    checkOutDTO.setUserId(rs.getInt("student_id"));
                    checkOutDTO.setCheckOutDate(rs.getTimestamp("check_out_date").toLocalDateTime());
                    checkOutDTO.setBankName(rs.getString("bank_name"));
                    checkOutDTO.setAccountNum(rs.getString("account_num"));
                    checkOutDTO.setCheckOutStatus(rs.getString("check_out_status"));

                    // 리스트에 추가
                    checkOutList.add(checkOutDTO);
                }
            }
        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}

        return checkOutList;
    }

    // 환불 상태 바꾸는 함수
    public void updateCheckOutStatus(int userId, String checkOutStatus) {

        checkOutDTO.setUserId(userId);
        checkOutDTO.setCheckOutStatus(checkOutStatus);

        String query = "UPDATE check_out SET check_out_status = ? WHERE student_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, checkOutDTO.getCheckOutStatus());  // 새로운 상태 설정
            statement.setInt(2, checkOutDTO.getUserId());     // 학생 아이디 설정

            statement.executeUpdate(); // 쿼리 실행

        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}
    }

    // 사용자의 환불 확인
    public ArrayList<CheckOutDTO> getCheckOutInfoByStudentId(int userId) {

        checkOutDTO.setUserId(userId);

        String query = "SELECT co.check_out_date, co.bank_name, co.account_num, co.check_out_status, d.dormitory_name " +
                "FROM check_out co " +
                "JOIN dormitory d ON co.dormitory_id = d.dormitory_id " +
                "WHERE co.student_id = ?";

        ArrayList<CheckOutDTO> checkOutInfoList = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query) ) {

            statement.setInt(1, checkOutDTO.getUserId()); // student_id 설정

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    // DTO 객체 생성

                    checkOutDTO.setUserId(checkOutDTO.getUserId()); // studentId를 DTO에 설정
                    checkOutDTO.setCheckOutDate(rs.getTimestamp("check_out_date").toLocalDateTime());
                    checkOutDTO.setBankName(rs.getString("bank_name"));
                    checkOutDTO.setAccountNum(rs.getString("account_num"));
                    checkOutDTO.setCheckOutStatus(rs.getString("check_out_status"));
                    checkOutDTO.setDormitoryName(rs.getString("dormitory_name"));

                    // 리스트에 DTO 추가
                    checkOutInfoList.add(checkOutDTO);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return checkOutInfoList; // DTO 리스트 반환
    }
}
