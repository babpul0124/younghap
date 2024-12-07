package persistence.dao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import persistence.dto.ApplicationDTO;
import java.util.ArrayList;
import persistence.dto.ApplicationListDTO;


import persistence.dto.DormitoryDTO;
import persistence.dto.UserDTO;
import persistence.dto.ApplicationStudentInfoDTO;
import persistence.dto.StudentDormitoryInfoDTO;
import persistence.dto.paymentListDTO;

import java.sql.*;


public class CheckInDAO {

    private final Connection connection;
    public CheckInDAO(Connection connection) {this.connection = connection;}

    // 입사 신청 정보 등록 함수
    public void submitApplication(int dormitoryId, int id, int preference, int mealFrequency, LocalDate applicationDate , boolean isSnoring) {

        String insertQuery = "INSERT INTO application (dormitory_id, student_id, preference, application_status ,meal_frequency, application_date) " + "VALUES (?, ?, ?, ?, ?, ?)";

        ApplicationDTO.ApplicationStatus status = ApplicationDTO.ApplicationStatus.대기;

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, dormitoryId);
            stmt.setInt(2, id);
            stmt.setInt(3, preference);
            stmt.setString(4, status.name());
            stmt.setInt(5, mealFrequency);
            stmt.setObject(6, applicationDate);

            stmt.executeUpdate();


            String updateQuery = "UPDATE student SET is_snoring = ? WHERE id = ?";

            try (PreparedStatement stmt2 = connection.prepareStatement(updateQuery)) {
                stmt2.setBoolean(1, isSnoring);
                stmt2.setInt(2, id);

                stmt2.executeUpdate();
            }

        } catch (SQLException e) {System.out.println("error: " + e);}
    }

    // 입사신청자 정보 목록 전송 함수
   public ArrayList<ApplicationListDTO> getApplicationList() {

        ArrayList<ApplicationListDTO> applicationList = new ArrayList<>();

        String query = "SELECT a.application_id, a.student_id, u.name, d.dormitory_name, d.room_capacity_num, d.dormitory_id, a.applicationStatus " +
                "FROM application a " +
                "JOIN user u ON a.student_id = u.id " +
                "JOIN dormitory d ON a.dormitory_id = d.dormitory_id";

        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery())
        {
            while (rs.next()) {

                int applicationId = rs.getInt("application_id");
                int studentId = rs.getInt("student_id");
                String userName = rs.getString("name");
                int dormitoryId = rs.getInt("dormitory_id");
                String dormitoryName = rs.getString("dormitory_name");
                int roomCapacityNum = rs.getInt("room_capacity_num");

                String applicationStatusStr = rs.getString("applicationStatus");
                ApplicationDTO.ApplicationStatus applicationStatus = ApplicationDTO.ApplicationStatus.valueOf(applicationStatusStr);


                // 관련 DTO 객체 생성 및 데이터 세팅
                DormitoryDTO dormitoryDTO = new DormitoryDTO();
                dormitoryDTO.setDormitoryId(dormitoryId);
                dormitoryDTO.setDormitoryName(dormitoryName);
                dormitoryDTO.setCapacityNum(roomCapacityNum);

                UserDTO userDTO = new UserDTO();
                userDTO.setId(studentId);
                userDTO.setName(userName);

                ApplicationDTO applicationDTO = new ApplicationDTO();
                applicationDTO.setApplicationId(applicationId);
                applicationDTO.setApplicationStatus(applicationStatus); 

                ApplicationListDTO applicationListDTO = new ApplicationListDTO(dormitoryDTO, userDTO, applicationDTO);

                applicationList.add(applicationListDTO);
            }

        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}

        return applicationList;
    }

    // 입사자 선발에 필요한 데이터 전송
    public ArrayList<ApplicationStudentInfoDTO> getApplicationStudentInfo() {

        ArrayList<ApplicationStudentInfoDTO> resultList = new ArrayList<>();

        String query = "SELECT a.student_id, s.grade, a.preference, a.dormitory_id " +
                "FROM application a " +
                "JOIN student s ON a.student_id = s.id " +
                "JOIN user u ON a.student_id = u.id";

        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery())
        {
            while (rs.next()) {
                ApplicationStudentInfoDTO dto = new ApplicationStudentInfoDTO();
                dto.setStudentId(rs.getInt("student_id"));
                dto.setGrade(rs.getDouble("grade"));
                dto.setPreference(rs.getInt("preference"));
                dto.setDormitoryId(rs.getInt("dormitory_id"));

                resultList.add(dto);
            }

        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}

        return resultList;
    }

    //입사 상태 바꾸기
    public void updateApplicationStatus(int studentId, String newStatus) {

        String query = "UPDATE application SET application_status = ? WHERE student_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // 새 상태 설정 (enum 값으로 설정해야 함)
            pstmt.setString(1, newStatus); // 예: "대기", "승인", "탈락"
            pstmt.setInt(2, studentId);    // 입력받은 student_id 값 설정

            pstmt.executeUpdate(); // 업데이트 실행
        }
        catch (SQLException e) {System.out.println("Error: " + e.getMessage());}
    }

    //호실 배정하기. passed 테이블 업데이트
    public void updatePassedAndDormitory(int studentId, int personalRoomId) {

        String querySelect = "SELECT application_id, dormitory_id " +
                "FROM application " +
                "WHERE student_id = ? AND application_status = '승인'";
        String queryUpdatePassed = "UPDATE passed SET personal_room_id = ?, dormitory_id = ? WHERE application_id = ?";
        String queryUpdateDormitory = "UPDATE dormitory SET capacity_num = capacity_num + 1 WHERE dormitory_id = ?";

        try (PreparedStatement stmtSelect = connection.prepareStatement(querySelect)) {
            // 1. application 테이블에서 데이터 추출
            stmtSelect.setInt(1, studentId);
            try (ResultSet rs = stmtSelect.executeQuery()) {
                if (rs.next()) {
                    int applicationId = rs.getInt("application_id");
                    int dormitoryId = rs.getInt("dormitory_id");

                    // 2. passed 테이블 업데이트
                    try (PreparedStatement stmtUpdatePassed = connection.prepareStatement(queryUpdatePassed)) {
                        stmtUpdatePassed.setInt(1, personalRoomId); // 입력받은 personal_room_id
                        stmtUpdatePassed.setInt(2, dormitoryId);    // 추출한 dormitory_id
                        stmtUpdatePassed.setInt(3, applicationId); // 추출한 application_id
                        stmtUpdatePassed.executeUpdate();
                    }

                    // 3. dormitory 테이블 업데이트
                    try (PreparedStatement stmtUpdateDormitory = connection.prepareStatement(queryUpdateDormitory)) {
                        stmtUpdateDormitory.setInt(1, dormitoryId); // 추출한 dormitory_id
                        stmtUpdateDormitory.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}
    }

    // 생활관비 납부 권한 확인
    public ArrayList<UserDTO> checkDormitoryPaymentAuthority() {
        ArrayList<UserDTO> studentIds = new ArrayList<>();
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
        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}

        return studentIds;
    }

    // 그 학생의 생활관 비용 조회하여 추출
    public ArrayList<StudentDormitoryInfoDTO> getDormitoryAndMealInfoByStudentId(int studentId) {
        ArrayList<StudentDormitoryInfoDTO> dormitoryInfoList = new ArrayList<>();
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
        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}

        return dormitoryInfoList; // 결과 리스트 반환
    }

    //납부상태로 바꾸기
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
                        updateStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}
    }

    // 결핵진단서 제출 !!! image_path를 impage로 바꾸고 타입도 블롭으로 바꿨어요!!!!!!
    public void saveStudentImage(int studentId, ApplicationDTO applicationDTO) {
        String query = "UPDATE application SET image = ? WHERE student_id = ?"; // students 테이블의 image 필드에 저장

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            byte[] imageBytes = applicationDTO.getImage(); // StudentDTO에서 이미지 가져오기
            if (imageBytes != null) {
                statement.setBytes(1, imageBytes); // BLOB 필드에 이미지 저장
            }
            statement.setInt(2, studentId); // 학생 ID 설정

            statement.executeUpdate();
        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}
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
        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}

        return false; // 조회된 데이터가 없다면 미제출로 간주
    }

    // 생활관 비용 납부자&미납부자 생활관 별로 조회
    public ArrayList<paymentListDTO> getDormitoryPaymentStatusList(int dormitoryId, String paymentStatus) {

        ArrayList <paymentListDTO> resultList = new ArrayList<>();

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

                    paymentListDTO dto = new paymentListDTO();
                    dto.setDormitoryName(resultSet.getString("dormitory_name"));
                    dto.setStudentId(resultSet.getInt("id"));
                    dto.setStudentName(resultSet.getString("name"));
                    dto.setDormitoryFee(resultSet.getInt("dormitory_fee"));
                    dto.setIsPayment(resultSet.getString("isPayment"));

                    resultList.add(dto);
                }
            }
        } catch (SQLException e){System.out.println("Error: " + e.getMessage());}

        return resultList;
    }
}
