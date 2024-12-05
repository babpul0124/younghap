package persistence.dao;

import persistence.dto.EventDTO;
import persistence.dto.DormitoryDTO;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DormitoryDAO {

    private final Connection connection;
    public DormitoryDAO(Connection connection) { this.connection = connection;}

    // 선발일정등록
    public void EventRegistration(int user_id, LocalDateTime start_date, LocalDateTime end_date, String evnet_name, LocalDate writed_date) {
        String query = "INSERT INTO event_schedule (user_id, start_date, end_date, event_name, writed_date) " + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query))
        {
            stmt.setInt(1, user_id); // 실제 관리자 id
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(start_date));
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(end_date));
            stmt.setString(4, evnet_name);
            stmt.setDate(5, java.sql.Date.valueOf(writed_date));

            stmt.executeUpdate();
        }
        catch (SQLException e) { e.printStackTrace();}
    }

    // 기숙사별 생활관 사용료 등록
    public void updateDormitoryFee(int dormitoryID, int roomCapacityNum ,int dormitoryFee) {

        String query = "UPDATE dormitory SET dormitory_fee = ? WHERE dormitory_id = ? AND room_capacity_num = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, dormitoryFee);
            stmt.setInt(2, dormitoryID);
            stmt.setInt(3, roomCapacityNum);

            stmt.executeUpdate();
        }

        catch (SQLException e) { e.printStackTrace();}
    }

    // 급식비 등록
    public void updateMoney(int dormitoryID, int mealFrequency, int money) {

        String query = "UPDATE meal SET money = ? WHERE dormitory_id = ? AND meal_frequency = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, money);
            stmt.setInt(2, dormitoryID);
            stmt.setInt(3, mealFrequency);

            stmt.executeUpdate();  // 쿼리 실행

        }
        catch (SQLException e) { e.printStackTrace();}
    }

    // 선발 일정 확인 (비용은 따로)
    public List<EventDTO> getEvents() {

        List<EventDTO> eventList = new ArrayList<>();

        String query = "SELECT start_date, end_date, event_name FROM event_schedule";

        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery())
        {
            while (rs.next()) {

                EventDTO eventDTO = new EventDTO();

                eventDTO.setStart_Date(rs.getObject("start_date", LocalDateTime.class));  // timestamp -> LocalDateTime
                eventDTO.setEnd_Date(rs.getObject("end_date", LocalDateTime.class));      // timestamp -> LocalDateTime
                eventDTO.setEvent_Name(rs.getString("event_name"));  // String으로 처리

                eventList.add(eventDTO);  // 조회된 EventDTO를 리스트에 추가
            }
        }
        catch (SQLException e) { e.printStackTrace();}

        // 일정 리스트 반환 (start_date, end_date, event_name로 구성된)
        return eventList;
    }

    // 생활관비, 식사비 확인
    public List<DormitoryDTO> getDormitoryMealInfo() {
        List<DormitoryDTO> resultList = new ArrayList<>();
        String query = "SELECT m.dormitory_id, m.meal_frequency, m.money, d.dormitory_name, d.room_capacity_num, d.dormitory_fee" +
                       "FROM meal m " +
                       "JOIN dormitory d ON m.dormitory_id = d.dormitory_id";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) 
        {
            while (rs.next()) {
                DormitoryDTO dto = new DormitoryDTO();

                // meal 테이블의 데이터 설정
                dto.setDormitoryID(rs.getInt("dormitory_id"));
                dto.setMealFrequency(rs.getInt("meal_frequency"));
                dto.setMoney(rs.getInt("money"));

                // dormitory 테이블의 데이터 설정
                dto.setDormitoryName(rs.getString("dormitory_name"));
                dto.setRoomCapacityNum(rs.getInt("room_capacity_num"));
                dto.setDormitoryFee(rs.getInt("dormitory_fee"));

                resultList.add(dto); 
            }
        } 
        catch (SQLException e) { e.printStackTrace();}

        // dormitory_id, dormitory_name, meal_frequency, money, room_capacity_num, dormitory_fee가 한 행인 리스트를 반환
        return resultList;  
    }
}
