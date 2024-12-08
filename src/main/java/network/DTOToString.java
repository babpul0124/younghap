package network;

import persistence.dto.*;

public class DTOToString {

    public static String event_scheduleDTOToString(EventDTO dto) {
        return "일정 : " + dto.getEventName()
                + " | 시작일: " + dto.getStartDate()
                + " | 종료일: " + dto.getEndDate() + "\n";
    }

    public static String DormitoryDTOToString(DormitoryDTO dto) {
        return "기숙사명 : " + dto.getDormitoryName()
                + " | 기숙사비: " + dto.getDormitoryFee()
                + " | 식사 유형: " + dto.getMealFrequency()
                + " | 식사비: " + dto.getMoney() + "\n";
    }

    public static String ApplicationListDTOToString(ApplicationListDTO dto) {
        return "기숙사명 : " + dto.getDormitoryDTO().getDormitoryName()
                + " | 기숙사비: " + dto.getDormitoryDTO().getRoomCapacityNum()
                + " | 식사 유형: " + dto.getMealFrequency()
                + " | 식사비: " + dto.getMoney() + "\n";
    }

}
