package persistence.dao;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import persistence.dto.DormitoryDTO;

public class DormitoryDAO {

    private final Connection connection;
    public DormitoryDAO(Connection connection) { this.connection = connection;}

    DormitoryDTO dormitoryDTO = new DormitoryDTO();

    // 이벤트 등록 함수
    public void eventRegistration(int userId, LocalDateTime startDate, LocalDateTime endDate, String eventName, LocalDate writedDate) {

        dormitoryDTO.setUserId(userId);
        dormitoryDTO.setStartDate(startDate);
        dormitoryDTO.setEndDate(endDate);
        dormitoryDTO.setEventName(eventName);
        dormitoryDTO.setWritedDate(writedDate);

        String query = "INSERT INTO event_schedule (user_id, start_date, end_date, event_name, writed_date) " + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query))
        {
            stmt.setInt(1, dormitoryDTO.getUserId()); // 실제 관리자 id
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(dormitoryDTO.getStartDate()));
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(dormitoryDTO.getEndDate()));
            stmt.setString(4, dormitoryDTO.getEventName());
            stmt.setDate(5, java.sql.Date.valueOf(dormitoryDTO.getWritedDate()));

            stmt.executeUpdate();
        }
        catch (SQLException e) {System.out.println("error: " + e);}
    }

    // 선발 일정 목록 전송 함수
    public ArrayList<DormitoryDTO> eventList() {

        ArrayList<DormitoryDTO> eventList = new ArrayList<>();

        String query = "SELECT e.start_date, e.end_date, e.event_name, e.writed_date, u.name " +
                       "FROM event_schedule e " +
                       "JOIN user u ON e.user_id = u.id";


        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery())
        {
            while (rs.next()) {

                dormitoryDTO.setStartDate(rs.getTimestamp("start_date").toLocalDateTime());
                dormitoryDTO.setEndDate(rs.getTimestamp("end_date").toLocalDateTime());
                dormitoryDTO.setEventName(rs.getString("event_name"));
                dormitoryDTO.setWritedDate(rs.getDate("writed_date").toLocalDate());
                dormitoryDTO.setUserName(rs.getString("name"));

                eventList.add(dormitoryDTO);  // 조회된 EventDTO를 리스트에 추가
            }
        }
        catch (SQLException e) { System.out.println("error: " + e);}

        return eventList;
    }

    // 기숙사별 생활관 사용료, 급식비 등록 함수
    public void dormitoryFeeRegistration(String dormitoryName, int roomCapacityNum, int dormitoryFee, int mealFrequency, int mealMoney) {

        dormitoryDTO.setDormitoryName(dormitoryName);
        dormitoryDTO.setRoomCapacityNum(roomCapacityNum);
        dormitoryDTO.setDormitoryFee(dormitoryFee);
        dormitoryDTO.setMealFrequency(mealFrequency);
        dormitoryDTO.setMealMoney(mealMoney);

        String query1 = "INSERT INTO dormitory (dormitory_name, room_capacity_num, dormitory_fee) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query1))
        {
            stmt.setString(1, dormitoryDTO.getDormitoryName());
            stmt.setInt(2,dormitoryDTO.getRoomCapacityNum());
            stmt.setInt(3,dormitoryDTO.getDormitoryFee());

            stmt.executeUpdate();
        }
        catch (SQLException e) {System.out.println("error: " + e);}

        String query2 = "INSET INTO meal (dormitory_id, meal_frequency, money)" +
                        "SELECT dormitory_id, ?, ?" +
                        "FROM dormitory" +
                        "WHERE dormitory_name = ? AND room_capacity_num = ?";

        try (PreparedStatement stmt2 = connection.prepareStatement(query2))
        {
            stmt2.setInt(1, dormitoryDTO.getMealFrequency());
            stmt2.setInt(2, dormitoryDTO.getMealMoney());
            stmt2.setString(3, dormitoryDTO.getDormitoryName());
            stmt2.setInt(4, dormitoryDTO.getRoomCapacityNum());

            stmt2.executeUpdate();
        }
        catch (SQLException e) {System.out.println("error: " + e);}
    }

    // 생활관 사용료 및 급식비 목록 전송 함수
    public ArrayList<DormitoryDTO> getAllDormitoryFee() {

        ArrayList<DormitoryDTO> dormitoryFeeList = new ArrayList<>();

        String query = "SELECT m.dormitory_id, m.meal_frequency, m.money, d.dormitory_name, d.room_capacity_num, d.dormitory_fee" +
                       "FROM meal m " +
                       "JOIN dormitory d ON m.dormitory_id = d.dormitory_id";

        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery())
        {
            while (rs.next()) {

                dormitoryDTO.setDormitoryId(rs.getInt("dormitory_id"));
                dormitoryDTO.setMealFrequency(rs.getInt("meal_frequency"));
                dormitoryDTO.setMealMoney(rs.getInt("money"));
                dormitoryDTO.setDormitoryName(rs.getString("dormitory_name"));
                dormitoryDTO.setRoomCapacityNum(rs.getInt("room_capacity_num"));
                dormitoryDTO.setDormitoryFee(rs.getInt("dormitory_fee"));

                dormitoryFeeList.add(dormitoryDTO);
            }
        }
        catch (SQLException e) { System.out.println("error: " + e);}

        // dormitory_id, dormitory_name, meal_frequency, money, room_capacity_num, dormitory_fee가 한 행인 리스트를 반환.
        return dormitoryFeeList;
    }

    // 생활관 목록 전송 함수
    public ArrayList<DormitoryDTO> getDormitoryInfo() {

        ArrayList<DormitoryDTO> dormitoryInfo = new ArrayList<>();

        String query = "SELECT dormitory_id, dormitory_name, room_capacity_num FROM dormitory";

        try(PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery())
        {
            while (rs.next()) {
                DormitoryDTO dormitoryDTO = new DormitoryDTO();

                dormitoryDTO.setDormitoryId(rs.getInt("dormitory_id"));
                dormitoryDTO.setDormitoryName(rs.getString("dormitory_name"));
                dormitoryDTO.setRoomCapacityNum(rs.getInt("room_capacity_num"));

                dormitoryInfo.add(dormitoryDTO);
            }
        }
        catch (SQLException e) { System.out.println("error: " + e);}

        return dormitoryInfo;
    }
}