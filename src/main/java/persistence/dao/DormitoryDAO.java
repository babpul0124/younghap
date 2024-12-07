package persistence.dao;
import persistence.dto.UserDTO;
import persistence.dto.EventDTO;
import persistence.dto.DormitoryDTO;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DormitoryDAO {

    private final Connection connection;
    public DormitoryDAO(Connection connection) { this.connection = connection;}

    // 이벤트 등록 함수
    public void eventRegistration(int id, LocalDateTime startDate, LocalDateTime endDate, String eventName, LocalDate writedDate) {

        String query = "INSERT INTO event_schedule (user_id, start_date, end_date, event_name, writed_date) " + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query))
        {
            stmt.setInt(1, id); // 실제 관리자 id
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(startDate));
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(endDate));
            stmt.setString(4, eventName);
            stmt.setDate(5, java.sql.Date.valueOf(writedDate));

            stmt.executeUpdate();
        }
        catch (SQLException e) {System.out.println("error: " + e);}
    }

    // 선발 일정 목록 전송 함수
    public ArrayList<EventDTO> getEvents() {

        ArrayList<EventDTO> eventList = new ArrayList<>();

        String query = "SELECT e.start_date, e.end_date, e.event_name, e.writed_date, u.name " +
                       "FROM event_schedule e " +
                       "JOIN user u ON e.user_id = u.id";


        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery())
        {
            while (rs.next()) {

                EventDTO eventDTO = new EventDTO();

                eventDTO.setStartDate(rs.getTimestamp("start_date").toLocalDateTime());
                eventDTO.setEndDate(rs.getTimestamp("end_date").toLocalDateTime());
                eventDTO.setEventName(rs.getString("event_name"));
                eventDTO.setWritedDate(rs.getDate("writed_date").toLocalDate());
                eventDTO.setName(rs.getString("name"));

                eventList.add(eventDTO);  // 조회된 EventDTO를 리스트에 추가
            }
        }
        catch (SQLException e) { System.out.println("error: " + e);}

        return eventList;
    }

    // 기숙사별 생활관 사용료, 급식비 등록 함수
    public void dormitoryFeeRegistration(String dormitoryName, int roomCapacityNum, int dormitoryFee, int mealFrequency, int money) {

        String query1 = "INSERT INTO dormitory (dormitory_name, room_capacity_num, dormitory_fee) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query1))
        {
            stmt.setString(1, dormitoryName);
            stmt.setInt(2,roomCapacityNum);
            stmt.setInt(3,dormitoryFee);

            stmt.executeUpdate();
        }
        catch (SQLException e) {System.out.println("error: " + e);}

        String query2 = "INSET INTO meal (dormitory_id, meal_frequency, money)" +
                        "SELECT dormitory_id, ?, ?" +
                        "FROM dormitory" +
                        "WHERE dormitory_name = ? AND room_capacity_num = ?";

        try (PreparedStatement stmt2 = connection.prepareStatement(query2))
        {
            stmt2.setInt(1, mealFrequency);
            stmt2.setInt(2, money);
            stmt2.setString(3, dormitoryName);
            stmt2.setInt(4, roomCapacityNum);

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

                DormitoryDTO dormitoryDTO = new DormitoryDTO();

                dormitoryDTO.setDormitoryId(rs.getInt("dormitory_id"));
                dormitoryDTO.setMealFrequency(rs.getInt("meal_frequency"));
                dormitoryDTO.setMoney(rs.getInt("money"));
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
                dormitoryDTO.setCapacityNum(rs.getInt("room_capacity_num"));

                dormitoryInfo.add(dormitoryDTO);
            }
        }
        catch (SQLException e) { System.out.println("error: " + e);}

        return dormitoryInfo;
    }
}