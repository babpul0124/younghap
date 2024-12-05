package persistence.dao;

import persistence.dto.DormitoryDTO;
import persistence.dto.DormitoryStudentInfoDTO;
import persistence.dto.UserDTO;
import persistence.dto.ApplicationStudentInfoDTO;
import persistence.dto.StudentDormitoryInfoDTO;
import persistence.dto.StudentDTO;
import persistence.dto.paymentListDTO;

import java.util.ArrayList;
import java.util.List;

import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class CheckInDAO {
    private final Connection connection;
    public CheckInDAO(Connection connection) {
        this.connection = connection;
    }

    // 입사신청
    public void submitApplication(int dormitoryID, int studentID, int preference, String mealFrequency, boolean isSnoring) {
        // 1. application 테이블에 데이터 삽입
        String insertApplicationQuery = "INSERT INTO application (dormitory_id, student_id, preference, meal_frequency, application_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertApplicationQuery)) {
            pstmt.setInt(1, dormitoryID);  // dormitory_id
            pstmt.setInt(2, studentID);    // student_id
            pstmt.setInt(3, preference);   // preference
            pstmt.setString(4, mealFrequency); // meal_frequency
            pstmt.setObject(5, LocalDateTime.now()); // application_date (현재 시간)

            pstmt.executeUpdate();  // application 테이블에 삽입

            // 2. student 테이블에서 해당 student의 is_snoring 값 업데이트
            String updateStudentQuery = "UPDATE student SET is_snoring = ? WHERE id = ?";

            try (PreparedStatement pstmt2 = connection.prepareStatement(updateStudentQuery)) {
                pstmt2.setBoolean(1, isSnoring);  // is_snoring 값 설정
                pstmt2.setInt(2, studentID);      // 해당 student_id 설정

                pstmt2.executeUpdate();  // student 테이블에서 is_snoring 값 업데이트
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 모든 생활관ID, 생활관명 추출
    public List<DormitoryDTO> getUniqueDormitories() {
        List<DormitoryDTO> dormitoryList = new ArrayList<>();
        String query = "SELECT dormitory_id, dormitory_name FROM dormitory";  // 중복을 제외한 dormitory_id, dormitory_name 추출

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                DormitoryDTO dormitoryDTO = new DormitoryDTO();

                dormitoryDTO.setDormitoryID(rs.getInt("dormitory_id"));
                dormitoryDTO.setDormitoryName(rs.getString("dormitory_name"));

                dormitoryList.add(dormitoryDTO);  // 리스트에 추가
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dormitoryList;  // 결과 리스트 반환
    }

    // 관리자의 생활관별 신청자 조회
    public List<DormitoryStudentInfoDTO> getDormitoryStudentInfo(int dormitoryID) {
        List<DormitoryStudentInfoDTO> resultList = new ArrayList<>();
        String query = "SELECT a.student_id, u.name, d.dormitory_name, d.room_capacity_num, a.meal_frequency " +
                "FROM application a " +
                "JOIN dormitory d ON a.dormitory_id = d.dormitory_id " +
                "JOIN user u ON a.student_id = u.id " +
                "WHERE a.dormitory_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, dormitoryID);  // 입력받은 dormitoryID 값을 설정

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {

                    DormitoryDTO dormitoryDTO = new DormitoryDTO();
                    dormitoryDTO.setDormitoryName(rs.getString("dormitory_name"));
                    dormitoryDTO.setRoomCapacityNum(rs.getInt("room_capacity_num"));
                    dormitoryDTO.setMealFrequency(rs.getInt("meal_frequency"));

                    // StudentDTO 생성 및 설정
                    UserDTO userDTO = new UserDTO();
                    userDTO.setId(rs.getInt("student_id"));
                    userDTO.setName(rs.getString("name"));

                    // DormitoryStudentInfoDTO 생성 및 각 DTO 설정
                    DormitoryStudentInfoDTO dto = new DormitoryStudentInfoDTO();
                    dto.setDormitoryDTO(dormitoryDTO);
                    dto.setUserDTO(userDTO);

                    resultList.add(dto);  // DTO를 리스트에 추가
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultList;  // 결과 리스트 반환
    }

    // 입사자 선발에 필요한 데이터 전송
    public List<ApplicationStudentInfoDTO> getApplicationStudentInfo() {
        List<ApplicationStudentInfoDTO> resultList = new ArrayList<>();

        String query = "SELECT a.student_id, s.grade, a.preference, a.dormitory_id " +
                "FROM application a " +
                "JOIN student s ON a.student_id = s.id " +
                "JOIN user u ON a.student_id = u.id";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ApplicationStudentInfoDTO dto = new ApplicationStudentInfoDTO();
                dto.setStudentId(rs.getInt("student_id"));   // student_id
                dto.setGrade(rs.getDouble("grade"));         // grade
                dto.setPreference(rs.getInt("preference"));  // preference
                dto.setDormitoryId(rs.getInt("dormitory_id"));  // dormitory_id

                resultList.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultList;  // 최종 리스트 반환
    }

    //입사 상태 바꾸기
    public void updateApplicationStatus(int studentId, String newStatus) {
        String query = "UPDATE application SET application_status = ? WHERE student_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // 새 상태 설정 (enum 값으로 설정해야 함)
            pstmt.setString(1, newStatus); // 예: "대기", "승인", "탈락"
            pstmt.setInt(2, studentId);    // 입력받은 student_id 값 설정

            pstmt.executeUpdate(); // 업데이트 실행
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //호실 배정하기. passed 테이블 업데이트
    public void updatePassedAndDormitory(int studentId, int personalRoomId) {
        String querySelect = "SELECT application_id, dormitory_id " +
                "FROM application " +
                "WHERE student_id = ? AND application_status = '승인'";
        String queryUpdatePassed = "UPDATE passed SET personal_room_id = ?, dormitory_id = ? WHERE application_id = ?";
        String queryUpdateDormitory = "UPDATE dormitory SET capacity_num = capacity_num + 1 WHERE dormitory_id = ?";

        try (PreparedStatement pstmtSelect = connection.prepareStatement(querySelect)) {
            // 1. application 테이블에서 데이터 추출
            pstmtSelect.setInt(1, studentId);
            try (ResultSet rs = pstmtSelect.executeQuery()) {
                if (rs.next()) {
                    int applicationId = rs.getInt("application_id");
                    int dormitoryId = rs.getInt("dormitory_id");

                    // 2. passed 테이블 업데이트
                    try (PreparedStatement pstmtUpdatePassed = connection.prepareStatement(queryUpdatePassed)) {
                        pstmtUpdatePassed.setInt(1, personalRoomId); // 입력받은 personal_room_id
                        pstmtUpdatePassed.setInt(2, dormitoryId);    // 추출한 dormitory_id
                        pstmtUpdatePassed.setInt(3, applicationId); // 추출한 application_id
                        pstmtUpdatePassed.executeUpdate();
                    }

                    // 3. dormitory 테이블 업데이트
                    try (PreparedStatement pstmtUpdateDormitory = connection.prepareStatement(queryUpdateDormitory)) {
                        pstmtUpdateDormitory.setInt(1, dormitoryId); // 추출한 dormitory_id
                        pstmtUpdateDormitory.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 생활관비 납부 권한 확인
    public List<UserDTO> checkDormitoryPaymentAuthority() {
        List<UserDTO> studentIds = new ArrayList<>();
        String query = "SELECT u.id " +
                "FROM application a " +
                "INNER JOIN passed p ON a.application_id = p.application_id " +
                "INNER JOIN users u ON a.application_id = u.id";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // 쿼리 결과에서 id를 추출하여 UserDTO 객체에 담고 리스트에 추가
            while (resultSet.next()) {
                int studentId = resultSet.getInt("id");
                UserDTO userDTO = new UserDTO();
                userDTO.setId(studentId);  // UserDTO의 id 필드에 studentId 값을 설정
                studentIds.add(userDTO);    // UserDTO 객체를 리스트에 추가
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return studentIds;
    }

    // 그 학생의 생활관 비용 조회하여 추출
    public List<StudentDormitoryInfoDTO> getDormitoryAndMealInfoByStudentId(int studentId) {
        List<StudentDormitoryInfoDTO> dormitoryInfoList = new ArrayList<>();
        String query = "SELECT a.student_id, a.dormitory_id, a.meal_frequency, d.dormitory_fee, m.money " +
                "FROM application a " +
                "INNER JOIN passed p ON a.application_id = p.application_id " +
                "INNER JOIN dormitory d ON a.dormitory_id = d.dormitory_id " +
                "INNER JOIN meal m ON a.dormitory_id = m.dormitory_id AND a.meal_frequency = m.meal_frequency " +
                "WHERE a.student_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, studentId); // studentId를 쿼리 파라미터로 설정

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // 쿼리 결과에서 여러 행을 처리
                while (resultSet.next()) {
                    StudentDormitoryInfoDTO infoDTO = new StudentDormitoryInfoDTO();
                    infoDTO.setStudentId(resultSet.getInt("student_id"));
                    infoDTO.setDormitoryId(resultSet.getInt("dormitory_id"));
                    infoDTO.setMealFrequency(resultSet.getInt("meal_frequency"));
                    infoDTO.setDormitoryFee(resultSet.getInt("dormitory_fee"));
                    infoDTO.setMoney(resultSet.getInt("money"));
                    dormitoryInfoList.add(infoDTO); // 리스트에 DTO 추가
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dormitoryInfoList; // 결과 리스트 반환
    }

    //납부상태로 바꾸기 .. 아니면 상태도 입력받아서 그냥 상태를 변경하는 함수로 바꿔도 됨.
    public void updatePaymentStatus(int studentId, int dormitoryId) {
        String getApplicationIdQuery = "SELECT application_id FROM application WHERE student_id = ? AND dormitory_id = ?";
        String updatePaymentStatusQuery = "UPDATE passed SET isPayment = '납부' WHERE application_id = ?";

        try (PreparedStatement getAppStmt = connection.prepareStatement(getApplicationIdQuery)) {
            // 1. 사용자 ID와 dormitory_id를 이용해 application_id 추출
            getAppStmt.setInt(1, studentId);
            getAppStmt.setInt(2, dormitoryId);

            try (ResultSet rs = getAppStmt.executeQuery()) {
                if (rs.next()) {
                    // application_id를 추출
                    int applicationId = rs.getInt("application_id");

                    // 2. 해당 application_id에 대해 isPayment를 '납부'로 업데이트
                    try (PreparedStatement updateStmt = connection.prepareStatement(updatePaymentStatusQuery)) {
                        updateStmt.setInt(1, applicationId);
                        int rowsAffected = updateStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 결핵진단서 제출 !!! image_path를 impage로 바꾸고 타입도 블롭으로 바꿨어요!!!!!!
    public void saveStudentImage(int studentId, StudentDTO studentDTO) {
        String query = "UPDATE application SET image = ? WHERE student_id = ?"; // students 테이블의 image 필드에 저장

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            byte[] imageBytes = studentDTO.getImage(); // StudentDTO에서 이미지 가져오기
            if (imageBytes != null) {
                statement.setBytes(1, imageBytes); // BLOB 필드에 이미지 저장
            }
            statement.setInt(2, studentId); // 학생 ID 설정

            int rowsUpdated = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //결핵 진단서 제출 확인
    public boolean checkStudentImageSubmitted(int studentId) {
        String query = "SELECT image FROM application WHERE student_id = ?"; // application 테이블에서 image 조회

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, studentId); // student_id 설정

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Blob imageBlob = resultSet.getBlob("image"); // image 컬럼 조회

                    if (imageBlob != null && imageBlob.length() > 0) {
                        // image가 null이 아니고 크기가 0보다 크면 제출한 것으로 간주
                        System.out.println("결핵진단서 제출됨.");
                        return true;
                    } else {
                        // image가 null이거나 크기가 0이면 제출하지 않은 것으로 간주
                        System.out.println("결핵진단서 미제출.");
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // 조회된 데이터가 없다면 미제출로 간주
    }

    // 생활관 비용 납부자&미납부자 생활관 별로 조회
    public List<paymentListDTO> getDormitoryPaymentStatusList(int dormitoryId, String paymentStatus) {
        List<paymentListDTO> resultList = new ArrayList<>();
        String query = "SELECT d.dormitory_name, u.id, u.name, d.dormitory_fee, p.isPayment " +
                "FROM dormitory d " +
                "INNER JOIN passed p ON d.dormitory_id = p.dormitory_id " +
                "INNER JOIN application a ON p.application_id = a.application_id " +
                "INNER JOIN users u ON a.student_id = u.id " +
                "WHERE p.isPayment = ? AND d.dormitory_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // 쿼리 파라미터 설정
            statement.setString(1, paymentStatus); // 지불 상태 (납부, 미납부)
            statement.setInt(2, dormitoryId); // 생활관 아이디

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // 결과에서 필요한 값들을 DTO에 저장
                    paymentListDTO dto = new paymentListDTO();
                    dto.setDormitoryName(resultSet.getString("dormitory_name"));
                    dto.setStudentId(resultSet.getInt("id"));
                    dto.setStudentName(resultSet.getString("name"));
                    dto.setDormitoryFee(resultSet.getInt("dormitory_fee"));
                    dto.setIsPayment(resultSet.getString("isPayment"));

                    resultList.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultList;
    }
}
