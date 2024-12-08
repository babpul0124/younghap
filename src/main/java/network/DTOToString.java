package network;

import persistence.dto.*;

public class DTOToString {

    public static String event_scheduleDTOToString(DormitoryDTO dto) {
        return "일정 : " + dto.getEventName()
                + " | 시작일: " + dto.getStartDate()
                + " | 종료일: " + dto.getEndDate() + "\n";
    }

    public static String DormitoryDTOToString(DormitoryDTO dto) {
        return "기숙사명 : " + dto.getDormitoryName()
                + " | 기숙사비: " + dto.getDormitoryFee()
                + " | 식사 유형: " + dto.getMealFrequency()
                + " | 식사비: " + dto.getMealMoney() + "\n";
    }

    public static String ApplicationToString(CheckInDTO dto) {
        return "기숙사명 : " + dto.getDormitoryName()
                + " | " + dto.getRoomCapacityNum() + "인실"
                + " | 식사 유형: " + dto.getMealFrequency()
                + " | 학번: " + dto.getUserId()
                + " | 이름: " + dto.getUserName() + "\n";
    }

    public static String PassedApplicationListDTOToString(CheckInDTO dto) {
        return "기숙사명 : " + dto.getDormitoryName()
                + " | " + dto.getRoomCapacityNum() + "인실"
                + " | " + dto.getRoomNum() + "호"
                + " | 침대 번호" + dto.getBedNum()
                + " | 학번: " + dto.getUserId()
                + " | 이름: " + dto.getUserName() + "\n";
    }

    public static String PaymentListToString(CheckInDTO dto) {
        return "기숙사명 : " + dto.getDormitoryName()
                + " | 학번: " + dto.getUserId()
                + " | 이름: " + dto.getUserName()
                + " | 기숙사비: " + dto.getDormitoryFee()
                + " | 결제 여부: " + dto.getIsPayment()
                + "\n";
    }

    public static String TuberculosisCertificaterListToString(CheckInDTO dto) {
        return "기숙사명 : " + dto.getDormitoryName()
                + " | 학번: " + dto.getUserId()
                + " | 결핵 진단서: " + dto.getImage()
                + "\n";
    }

    public static String viewDormitoryFeeDIOToString(CheckInDTO dto) {
        return  "| 합계: " + dto.getDormitoryFee() + dto.getMealMoney() + "\n";
    }

    public static String viewCheckOutDTOToString(CheckOutDTO dto) {
        return "기숙사명 : " + dto.getDormitoryId()
                + " | 퇴사일: " + dto.getCheckOutDate()
                + " | 은행 이름: " + dto.getBankName()
                + " | 계좌 번호: " + dto.getAccountNum()
                + " | 퇴사 여부: " + dto.getCheckOutStatus()
                + "\n";
    }
}
