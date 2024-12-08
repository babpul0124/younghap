package persistence.dao;
import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDate;
import persistence.dto.CheckInDTO;

public class CheckInDAO {

    private final Connection connection;
    public CheckInDAO(Connection connection) {this.connection = connection;}


    // 입사 신청 정보 등록 함수
   public void submitApplication(int dormitoryId, int userId, int preference, int mealFrequency, int isSnoring, String applicationStatus) {
       CheckInDTO checkInDTO = new CheckInDTO();

       checkInDTO.setDormitoryId(dormitoryId);
       checkInDTO.setUserId(userId);
       checkInDTO.setPreference(preference);
       checkInDTO.setMealFrequency(mealFrequency);
       checkInDTO.setIsSnoring(isSnoring);
       checkInDTO.setApplicationStatus(applicationStatus);
       checkInDTO.setApplicationDate(LocalDate.now());


       int id = checkInDTO.getUserId();
        String insertQuery = "INSERT INTO application (dormitory_id, student_id, preference, application_status ,meal_frequency, application_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, checkInDTO.getDormitoryId());
            stmt.setInt(2, id);
            stmt.setInt(3, checkInDTO.getPreference());
            stmt.setString(4, checkInDTO.getApplicationStatus());
            stmt.setInt(5, checkInDTO.getMealFrequency());
            stmt.setObject(6, checkInDTO.getApplicationDate());  // LocalDateTime.now()로 현재 날짜와 시간 삽입

            stmt.executeUpdate();

            String updateQuery = "UPDATE student SET is_snoring = ? WHERE id = ?";

            try (PreparedStatement stmt2 = connection.prepareStatement(updateQuery)) {
                stmt2.setInt(1, checkInDTO.getIsSnoring());
                stmt2.setInt(2, id);

                stmt2.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("error: " + e);
        }
    }

    // 입사신청자 정보 목록 전송 함수
    public ArrayList<CheckInDTO> getApplicationList() {

        ArrayList<CheckInDTO> applicationList = new ArrayList<>();

        String query = "SELECT a.dormitory_id, a.student_id, a.preference, a.application_status, " +
                "a.meal_frequency, a.application_date, a.image, u.is_snoring, u.name " +
                "FROM application a " +
                "JOIN user u ON a.student_id = u.id";

        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CheckInDTO checkInDTO = new CheckInDTO();

                checkInDTO.setDormitoryId(rs.getInt("dormitory_id"));
                checkInDTO.setUserId(rs.getInt("student_id"));
                checkInDTO.setPreference(rs.getInt("preference"));
                checkInDTO.setApplicationStatus(rs.getString("application_status"));
                checkInDTO.setMealFrequency(rs.getInt("meal_frequency"));
                checkInDTO.setApplicationDate(rs.getDate("application_date").toLocalDate());

                // BLOB 데이터를 byte[]로 변환
                Blob imageBlob = rs.getBlob("image");
                if (imageBlob != null) {
                    int length = (int) imageBlob.length();
                    checkInDTO.setImage(imageBlob.getBytes(1, length));  // BLOB -> byte[] 변환
                }

                checkInDTO.setIsSnoring(rs.getInt("is_snoring"));
                checkInDTO.setUserName(rs.getString("name"));

                applicationList.add(checkInDTO);
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return applicationList;
    }

    // 입사자 선발에 필요한 데이터 전송
    public ArrayList<CheckInDTO> getApplicationStudentInfo() {
        CheckInDTO checkInDTO = new CheckInDTO();


        ArrayList<CheckInDTO> resultList = new ArrayList<>();

        String query = "SELECT a.student_id, s.grade, a.preference, a.dormitory_id " +
                "FROM application a " +
                "JOIN student s ON a.student_id = s.id " +
                "JOIN user u ON a.student_id = u.id";

        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery())
        {
            while (rs.next()) {

                checkInDTO.setUserId(rs.getInt("student_id"));
                checkInDTO.setGrade(rs.getDouble("grade"));
                checkInDTO.setPreference(rs.getInt("preference"));
                checkInDTO.setDormitoryId(rs.getInt("dormitory_id"));

                resultList.add(checkInDTO);
            }

        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}

        return resultList;
    }

    //입사 상태 바꾸기
    public void updateApplicationStatus(int userId, String applicationStatus) {
        CheckInDTO checkInDTO = new CheckInDTO();

        checkInDTO.setUserId(userId);
        checkInDTO.setApplicationStatus(applicationStatus);

        String query = "UPDATE application SET application_status = ? WHERE student_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, checkInDTO.getApplicationStatus()); // 예: "대기", "승인", "탈락"
            stmt.setInt(2, checkInDTO.getUserId());    // 입력받은 student_id 값 설정

            stmt.executeUpdate(); // 업데이트 실행
        }
        catch (SQLException e) {System.out.println("Error: " + e.getMessage());}
    }

    //호실 배정하기.
    public void updatePassedAndDormitory(int userId, int personalRoomId) {
        CheckInDTO checkInDTO = new CheckInDTO();

        // 1. application 테이블에서 id에 해당하는 student_id의 application_id를 추출

        checkInDTO.setUserId(userId);

        String applicationIdQuery = "SELECT application_id FROM application WHERE student_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(applicationIdQuery); ResultSet rs = stmt.executeQuery())
        {
            stmt.setInt(1, checkInDTO.getUserId());

            if (rs.next()) {

                checkInDTO.setApplicationId(rs.getInt("application_id"));

            } else {
                System.out.println("실패: id 없음");
                return;
            }
        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}

        // 2. personal_room 테이블에서 personalRoomId에 해당하는 dormitory_id를 추출
        String dormitoryIdQuery = "SELECT dormitory_id FROM personal_room WHERE personal_room_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(dormitoryIdQuery)) {

            checkInDTO.setPersonalRoomId(personalRoomId);

            stmt.setInt(1, checkInDTO.getPersonalRoomId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                checkInDTO.setDormitoryId(rs.getInt("dormitory_Id"));

            } else {
                System.out.println("No dormitory found for the given personal room ID.");
                return;
            }
        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}

        // 3. passed 테이블에 application_id, personal_room_id, dormitory_id, isPayment == "미납부"로 데이터를 삽입
        String insertQuery = "INSERT INTO passed (application_id, personal_room_id, dormitory_id, isPayment) VALUES (?, ?, ?, '미납부')";

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, checkInDTO.getApplicationId());
            stmt.setInt(2, checkInDTO.getPersonalRoomId());
            stmt.setInt(3, checkInDTO.getDormitoryId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Passed table updated successfully.");
            } else {
                System.out.println("Failed to insert data into passed table.");
            }
        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}

    }


    // 그 학생의 생활관 비용 조회하여 추출
    public ArrayList<CheckInDTO> getDormitoryAndMealInfoByStudentId(int userId) {
        CheckInDTO checkInDTO = new CheckInDTO();

        checkInDTO.setUserId(userId);

        ArrayList<CheckInDTO> dormitoryInfoList = new ArrayList<>();

        String query = "SELECT a.student_id, a.dormitory_id, a.meal_frequency, d.dormitory_fee, m.money " +
                "FROM application a " +
                "INNER JOIN passed p ON a.application_id = p.application_id " +
                "INNER JOIN dormitory d ON a.dormitory_id = d.dormitory_id " +
                "INNER JOIN meal m ON a.dormitory_id = m.dormitory_id AND a.meal_frequency = m.meal_frequency " +
                "WHERE a.student_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, checkInDTO.getUserId()); // studentId를 쿼리 파라미터로 설정

            try (ResultSet rs = preparedStatement.executeQuery()) {
                // 쿼리 결과에서 여러 행을 처리
                while (rs.next()) {

                    checkInDTO.setUserId(rs.getInt("student_id"));
                    checkInDTO.setDormitoryId(rs.getInt("dormitory_id"));
                    checkInDTO.setMealFrequency(rs.getInt("meal_frequency"));
                    checkInDTO.setDormitoryFee(rs.getInt("dormitory_fee"));
                    checkInDTO.setMealMoney(rs.getInt("money"));

                    dormitoryInfoList.add(checkInDTO); // 리스트에 DTO 추가
                }
            }
        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}

        return dormitoryInfoList; // 결과 리스트 반환
    }

    //납부상태로 바꾸기
    public void updatePaymentStatus(int userId, int dormitoryId) {
        CheckInDTO checkInDTO = new CheckInDTO();

       checkInDTO.setUserId(userId);
       checkInDTO.setDormitoryId(dormitoryId);

        String getApplicationIdQuery = "SELECT application_id FROM application WHERE student_id = ? AND dormitory_id = ?";
        String updatePaymentStatusQuery = "UPDATE passed SET isPayment = '납부' WHERE application_id = ?";

        try (PreparedStatement getAppStmt = connection.prepareStatement(getApplicationIdQuery)) {
            // 1. 사용자 ID와 dormitory_id를 이용해 application_id 추출
            getAppStmt.setInt(1, checkInDTO.getUserId());
            getAppStmt.setInt(2, checkInDTO.getDormitoryId());

            try (ResultSet rs = getAppStmt.executeQuery()) {
                if (rs.next()) {
                    // application_id를 추출
                    checkInDTO.setApplicationId(rs.getInt("application_id"));

                    // 2. 해당 application_id에 대해 isPayment를 '납부'로 업데이트
                    try (PreparedStatement updateStmt = connection.prepareStatement(updatePaymentStatusQuery)) {

                        updateStmt.setInt(1, checkInDTO.getApplicationId());
                        updateStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}
    }


    // 결핵진단서 이미지 파일 제출
    public void saveStudentImage(int userId, byte[] image) {
        CheckInDTO checkInDTO = new CheckInDTO();

        checkInDTO.setUserId(userId);
        checkInDTO.setImage(image);

        String query = "UPDATE application SET image = ? WHERE student_id = ?"; // students 테이블의 image 필드에 저장

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            byte[] imageBytes = checkInDTO.getImage();
            if (imageBytes != null) {
                statement.setBytes(1, checkInDTO.getImage()); // BLOB 필드에 이미지 저장. 자동으로 됨.
            }
            statement.setInt(2, checkInDTO.getUserId()); // 학생 ID 설정

            statement.executeUpdate();

        } catch (SQLException e) {System.out.println("Error: " + e.getMessage());}
    }

    //결핵 진단서 제출 확인
    public String checkStudentImageSubmitted(int userId) {
        String result = "No";  // 기본값을 "No"로 설정

        String query = "SELECT image FROM application WHERE student_id = ?"; // application 테이블에서 image 조회

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);  // userId를 쿼리에 세팅

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Blob imageBlob = resultSet.getBlob("image"); // image 컬럼 조회

                    if (imageBlob != null && imageBlob.length() > 0) {
                        System.out.println("결핵진단서 제출됨.");
                        result = "Yes";
                    } else {
                        System.out.println("결핵진단서 미제출.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return result;
    }



    // 생활관 비용 납부자&미납부자 생활관 별로 조회
    public ArrayList<CheckInDTO> getDormitoryPaymentStatusList(int dormitoryId, String isPayment) {
        CheckInDTO checkInDTO = new CheckInDTO();

       checkInDTO.setDormitoryId(dormitoryId);
       checkInDTO.setIsPayment(isPayment);

        ArrayList <CheckInDTO> resultList = new ArrayList<>();

        String query = "SELECT d.dormitory_name, u.id, u.name, d.dormitory_fee, p.isPayment " +
                "FROM dormitory d " +
                "INNER JOIN passed p ON d.dormitory_id = p.dormitory_id " +
                "INNER JOIN application a ON p.application_id = a.application_id " +
                "INNER JOIN users u ON a.student_id = u.id " +
                "WHERE p.isPayment = ? AND d.dormitory_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // 쿼리 파라미터 설정
            statement.setString(1, checkInDTO.getIsPayment()); // 지불 상태 (납부, 미납부)
            statement.setInt(2, checkInDTO.getDormitoryId()); // 생활관 아이디

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {

                    checkInDTO.setDormitoryName(resultSet.getString("dormitory_name"));
                    checkInDTO.setUserId(resultSet.getInt("id"));
                    checkInDTO.setUserName(resultSet.getString("name"));
                    checkInDTO.setDormitoryFee(resultSet.getInt("dormitory_fee"));
                    checkInDTO.setIsPayment(resultSet.getString("isPayment"));

                    resultList.add(checkInDTO);
                }
            }
        } catch (SQLException e){System.out.println("Error: " + e.getMessage());}

        return resultList;
    }
}
