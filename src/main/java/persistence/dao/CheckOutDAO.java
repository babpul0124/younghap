package persistence.dao;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import persistence.dto.CheckOutDTO;

public class CheckOutDAO {
    private final Connection connection;

    public CheckOutDAO(Connection connection) {
        this.connection = connection;
    }

    // 퇴실 신청 권한 확인 (1) 상태가 승인인 학생아이디 리스트 전송
    public List<Integer> getApprovedStudentIds() {
        List<Integer> approvedStudentIds = new ArrayList<>();
        String query = "SELECT student_id FROM application WHERE application_status = '승인'";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int studentId = resultSet.getInt("student_id");
                approvedStudentIds.add(studentId); // 승인된 student_id 추가
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return approvedStudentIds; // 결과 리스트 반환
    }

    //  퇴실 신청 권한 확인 (2) check_out 테이블에 있는 학생아이디 리스트 전송.
    public List<Integer> getStudentIdsFromCheckOut() {
        List<Integer> studentIds = new ArrayList<>();
        String query = "SELECT student_id FROM check_out";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int studentId = resultSet.getInt("student_id");
                studentIds.add(studentId); // student_id 추가
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return studentIds; // 결과 리스트 반환
    }

    // 퇴사 신청 받아서 db에 저장.
    public void processStudentCheckOut(int studentId, LocalDateTime checkOutDate, String bankName, String accountNum) {
        String dormitoryId = getDormitoryIdByStudentId(studentId);

        if (dormitoryId != null) {
            // check_out 테이블에 저장
            String query = "INSERT INTO check_out (dormitory_id, student_id, check_out_date, bank_name, account_num) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, dormitoryId);  // dormitory_id
                statement.setInt(2, studentId);       // student_id
                statement.setTimestamp(3, Timestamp.valueOf(checkOutDate));   // check_out_date
                statement.setString(4, bankName);     // bank_name
                statement.setString(5, accountNum);   // account_num

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("퇴사 정보가 성공적으로 저장되었습니다.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 학생 아이디로 dormitory_id를 찾는 메서드
    private String getDormitoryIdByStudentId(int studentId) {
        String dormitoryId = null;

        String query = "SELECT a.dormitory_id " +
                "FROM application a " +
                "INNER JOIN passed p ON a.application_id = p.application_id " +
                "WHERE a.student_id = ? AND p.isPayment = '납부'";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, studentId); // student_id 설정

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    dormitoryId = resultSet.getString("dormitory_id"); // dormitory_id 추출
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dormitoryId; // dormitory_id 반환 (없으면 null)
    }

    // 퇴사 신청자 조회
    public List<CheckOutDTO> getCheckOutListByDormitoryId(String dormitoryId) {
        List<CheckOutDTO> checkOutList = new ArrayList<>();
        String query = "SELECT dormitory_id, student_id, check_out_date, bank_name, account_num, check_out_status " +
                "FROM check_out WHERE dormitory_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, dormitoryId);  // dormitory_id 설정

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String dormitoryIdResult = resultSet.getString("dormitory_id");
                    int studentId = resultSet.getInt("student_id");

                    // check_out_date를 Timestamp로 받아 LocalDateTime으로 변환
                    Timestamp checkOutDateTimestamp = resultSet.getTimestamp("check_out_date");
                    LocalDateTime checkOutDate = checkOutDateTimestamp.toLocalDateTime();

                    String bankName = resultSet.getString("bank_name");
                    String accountNum = resultSet.getString("account_num");
                    String checkOutStatus = resultSet.getString("check_out_status");

                    // DTO 객체 생성
                    CheckOutDTO checkOutDTO = new CheckOutDTO();
                    checkOutDTO.setDormitoryId(dormitoryIdResult);
                    checkOutDTO.setStudentId(studentId);
                    checkOutDTO.setCheckOutDate(checkOutDate);  // checkOutDate는 null이 아니어야 함
                    checkOutDTO.setBankName(bankName);
                    checkOutDTO.setAccountNum(accountNum);
                    checkOutDTO.setCheckOutStatus(checkOutStatus);

                    // 리스트에 추가
                    checkOutList.add(checkOutDTO);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return checkOutList;
    }

    // 환불 상태 바꾸는 함수
    public void updateCheckOutStatus(int studentId, String newStatus) {
        String query = "UPDATE check_out SET check_out_status = ? WHERE student_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, newStatus);  // 새로운 상태 설정
            statement.setInt(2, studentId);     // 학생 아이디 설정
            statement.executeUpdate(); // 쿼리 실행

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 사용자의 환불 확인
    public List<String> getCheckOutInfoByStudentId(int studentId) {
        String query = "SELECT co.check_out_date, co.bank_name, co.account_num, co.check_out_status, d.dormitory_name " +
                "FROM check_out co " +
                "JOIN dormitory d ON co.dormitory_id = d.dormitory_id " +
                "WHERE co.student_id = ?";

        List<String> checkOutInfo = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, studentId);  // student_id 설정

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // 결과를 리스트에 저장
                    String checkOutDate = resultSet.getString("check_out_date");
                    String bankName = resultSet.getString("bank_name");
                    String accountNum = resultSet.getString("account_num");
                    String checkOutStatus = resultSet.getString("check_out_status");
                    String dormitoryName = resultSet.getString("dormitory_name");

                    checkOutInfo.add("Check-out Date: " + checkOutDate);
                    checkOutInfo.add("Bank Name: " + bankName);
                    checkOutInfo.add("Account Number: " + accountNum);
                    checkOutInfo.add("Check-out Status: " + checkOutStatus);
                    checkOutInfo.add("Dormitory Name: " + dormitoryName);
                } else {
                    checkOutInfo.add("해당 학생의 퇴사 정보가 없습니다.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return checkOutInfo;  // 결과 반환
    }

}