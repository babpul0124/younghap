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
   public void submitApplication(int dormitoryId, int id, int preference, int mealFrequency, boolean isSnoring) {

        String insertQuery = "INSERT INTO application (dormitory_id, student_id, preference, application_status ,meal_frequency, application_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        ApplicationDTO.ApplicationStatus status = ApplicationDTO.ApplicationStatus.대기;

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, dormitoryId);
            stmt.setInt(2, id);
            stmt.setInt(3, preference);
            stmt.setString(4, status.name());
            stmt.setInt(5, mealFrequency);

            // 현재 시간 삽입
            stmt.setObject(6, LocalDate.now());  // LocalDateTime.now()로 현재 날짜와 시간 삽입

            stmt.executeUpdate();

            String updateQuery = "UPDATE student SET is_snoring = ? WHERE id = ?";

            try (PreparedStatement stmt2 = connection.prepareStatement(updateQuery)) {
                stmt2.setBoolean(1, isSnoring);
                stmt2.setInt(2, id);

                stmt2.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("error: " + e);
        }
    }
    
    // 입사신청자 정보 목록 전송 함수
    public ArrayList<ApplicationListDTO> getApplicationList() {

        ArrayList<ApplicationListDTO> applicationList = new ArrayList<>();

        String query = "SELECT a.application_id, a.student_id, u.name, d.dormitory_name, d.room_capacity_num, d.dormitory_id, a.applicationStatus, " +
                "pr.room_num, pr.bed_name " +
                "FROM application a " +
                "JOIN user u ON a.student_id = u.id " +
                "JOIN dormitory d ON a.dormitory_id = d.dormitory_id " +
                "JOIN personal_room pr ON d.dormitory_id = pr.dormitory_id";

        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                int applicationId = rs.getInt("application_id");
                int studentId = rs.getInt("student_id");
                String userName = rs.getString("name");
                int dormitoryId = rs.getInt("dormitory_id");
                String dormitoryName = rs.getString("dormitory_name");
                int roomCapacityNum = rs.getInt("room_capacity_num");
                String applicationStatusStr = rs.getString("applicationStatus");
                ApplicationDTO.ApplicationStatus applicationStatus = ApplicationDTO.ApplicationStatus.valueOf(applicationStatusStr);

                int roomNum = rs.getInt("room_num");
                int bedName = rs.getInt("bed_name");

                // 관련 DTO 객체 생성 및 데이터 세팅
                DormitoryDTO dormitoryDTO = new DormitoryDTO();
                dormitoryDTO.setDormitoryId(dormitoryId);
                dormitoryDTO.setDormitoryName(dormitoryName);
                dormitoryDTO.setCapacityNum(roomCapacityNum);
                dormitoryDTO.setRoomNum(roomNum);
                dormitoryDTO.setBedName(bedName);

                UserDTO userDTO = new UserDTO();
                userDTO.setId(studentId);
                userDTO.setName(userName);

                ApplicationDTO applicationDTO = new ApplicationDTO();
                applicationDTO.setApplicationId(applicationId);
                applicationDTO.setApplicationStatus(applicationStatus); // Enum 값 설정

                // ApplicationListDTO 생성
                ApplicationListDTO applicationListDTO = new ApplicationListDTO(dormitoryDTO, userDTO, applicationDTO);

                applicationList.add(applicationListDTO);
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

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
    public void updateApplicationStatus(int id, String applicationStatus) {

        String query = "UPDATE application SET application_status = ? WHERE student_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // 새 상태 설정 (enum 값으로 설정해야 함)
            stmt.setString(1, applicationStatus); // 예: "대기", "승인", "탈락"
            stmt.setInt(2, id);    // 입력받은 student_id 값 설정

            stmt.executeUpdate(); // 업데이트 실행
        }
        catch (SQLException e) {System.out.println("Error: " + e.getMessage());}
    }

    //호실 배정하기. passed 테이블 업데이트
    public void updatePassedAndDormitory(int id, int personalRoomId) {

        // 1. application 테이블에서 id에 해당하는 student_id의 application_id를 추출
        String applicationIdQuery = "SELECT application_id FROM application WHERE student_id = ?";

        int applicationId = 0;
        try (PreparedStatement stmt = connection.prepareStatement(applicationIdQuery)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                applicationId = rs.getInt("application_id");
            } else {
                System.out.println("No application found for the given student ID.");
                return;
            }
        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}

        // 2. personal_room 테이블에서 personalRoomId에 해당하는 dormitory_id를 추출
        String dormitoryIdQuery = "SELECT dormitory_id FROM personal_room WHERE personal_room_id = ?";

        int dormitoryId = 0;
        try (PreparedStatement stmt = connection.prepareStatement(dormitoryIdQuery)) {
            stmt.setInt(1, personalRoomId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                dormitoryId = rs.getInt("dormitory_id");
            } else {
                System.out.println("No dormitory found for the given personal room ID.");
                return;
            }
        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}

        // 3. passed 테이블에 application_id, personal_room_id, dormitory_id, isPayment == "미납부"로 데이터를 삽입
        String insertQuery = "INSERT INTO passed (application_id, personal_room_id, dormitory_id, isPayment) VALUES (?, ?, ?, '미납부')";

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, applicationId);
            stmt.setInt(2, personalRoomId);
            stmt.setInt(3, dormitoryId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Passed table updated successfully.");
            } else {
                System.out.println("Failed to insert data into passed table.");
            }
        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}

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
    public void updatePaymentStatus(int id, int dormitoryId) {
        String getApplicationIdQuery = "SELECT application_id FROM application WHERE student_id = ? AND dormitory_id = ?";
        String updatePaymentStatusQuery = "UPDATE passed SET isPayment = '납부' WHERE application_id = ?";

        try (PreparedStatement getAppStmt = connection.prepareStatement(getApplicationIdQuery)) {
            // 1. 사용자 ID와 dormitory_id를 이용해 application_id 추출
            getAppStmt.setInt(1, id);
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


    ApplicationDTO applicationDTO = new ApplicationDTO();
    // 결핵진단서 제출
    public void saveStudentImage(int id, byte[] image) {

        String query = "UPDATE application SET image = ? WHERE student_id = ?"; // students 테이블의 image 필드에 저장

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            byte[] imageBytes = applicationDTO.getImage();
            if (imageBytes != null) {
                statement.setBytes(1, imageBytes); // BLOB 필드에 이미지 저장
            }
            statement.setInt(2, id); // 학생 ID 설정

            statement.executeUpdate();

        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}
    }

    //결핵 진단서 제출 확인
    public String checkStudentImageSubmitted(int id) {
        String query = "SELECT image FROM application WHERE student_id = ?"; // application 테이블에서 image 조회

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Blob imageBlob = resultSet.getBlob("image"); // image 컬럼 조회

                    if (imageBlob != null && imageBlob.length() > 0) {

                        System.out.println("결핵진단서 제출됨.");
                        applicationDTO.setResult("Yes");
                    } else {

                        System.out.println("결핵진단서 미제출.");
                        applicationDTO.setResult("No");
                    }
                }
            }
        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}
        return applicationDTO.getResult();
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
