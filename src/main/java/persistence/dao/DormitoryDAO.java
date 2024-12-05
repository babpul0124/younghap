package persistence.dao;

import persistence.dto.UserDTO; // UserDTO 클래스를 가져오기
import persistence.dto.EventDTO; // EventDTO 클래스도 가져오기
import persistence.dto.DormitoryDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;

public class DormitoryDAO {
    private final Connection connection;
    public DormitoryDAO(Connection connection) {
        this.connection = connection;
    }

    // 선발일정등록
    public void EventRegistration(EventDTO eventDTO, UserDTO userDTO) {
        String query = "INSERT INTO event_schedule (id, start_date, end_date, event_name, writed_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userDTO.getId());  // UserDTO에서 id 값 설정
            pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(eventDTO.getStartDate()));  // startDate 값 설정
            pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(eventDTO.getEndDate()));    // endDate 값 설정
            pstmt.setString(4, eventDTO.getEventName());  // eventName 값 설정
            pstmt.setDate(5, java.sql.Date.valueOf(eventDTO.getWrited_date()));  // writed_date 값 설정
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 기숙사별 생활관 사용료 등록
    public void updateDormitoryFee(int dormitoryID, int roomCapacityNum ,int newDormitoryFee) {

        String query = "UPDATE dormitory SET dormitory_fee = ? WHERE dormitory_id = ? AND room_capacity_num = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, newDormitoryFee);  // 새로운 dormitory_fee 값 설정
            pstmt.setInt(2, dormitoryID);  // 입력받은 dormitoryID 값 설정
            pstmt.setInt(3, roomCapacityNum);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 급식비 등록
    public void updateMoney(int dormitoryID, int mealFrequency, int newMoney) {
        String query = "UPDATE dormitory SET money = ? WHERE dormitory_id = ? AND meal_frequency = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, newMoney);  // 업데이트할 money 값
            pstmt.setInt(2, dormitoryID);  // dormitory_id 값
            pstmt.setInt(3, mealFrequency);  // meal_frequency 값
            pstmt.executeUpdate();  // 쿼리 실행
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 선발 일정 확인
    public List<EventDTO> getEvents() {
        List<EventDTO> eventList = new ArrayList<>();
        String query = "SELECT start_date, end_date, event_name FROM event_schedule";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                EventDTO eventDTO = new EventDTO();

                // start_date와 end_date를 null 가능하게 처리
                eventDTO.setStartDate(rs.getObject("start_date", LocalDateTime.class));  // timestamp -> LocalDateTime
                eventDTO.setEndDate(rs.getObject("end_date", LocalDateTime.class));      // timestamp -> LocalDateTime
                eventDTO.setEventName(rs.getString("event_name"));  // String으로 처리

                eventList.add(eventDTO);  // 조회된 EventDTO를 리스트에 추가
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return eventList;  // EventDTO 객체 리스트 반환
    }

    // 생활관비, 식사비 확인
    public List<DormitoryDTO> getDormitoryMealInfo() {
        List<DormitoryDTO> resultList = new ArrayList<>();
        String query = "SELECT m.dormitory_id, m.meal_frequency, m.money, " +
                "d.dormitory_name, d.room_capacity_num, d.dormitory_fee " +
                "FROM meal m " +
                "JOIN dormitory d ON m.dormitory_id = d.dormitory_id";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

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

                resultList.add(dto);  // DTO를 리스트에 추가
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultList;  // 결과 리스트 반환
    }
}
