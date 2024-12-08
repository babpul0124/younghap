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
                + " | " + dto.getDormitoryDTO().getRoomCapacityNum() + "인실"
                + " | 식사 유형: " + dto.getDormitoryDTO().getMealFrequency()
                + " | 학번: " + dto.getUserDTO().getId()
                + " | 이름: " + dto.getUserDTO().getName() + "\n";
    }

    public static String PassedApplicationListDTOToString(ApplicationListDTO dto) {
        return "기숙사명 : " + dto.getDormitoryDTO().getDormitoryName()
                + " | " + dto.getDormitoryDTO().getRoomCapacityNum() + "인실"
                + " | " + dto.getDormitoryDTO().getRoomNum() + "호"
                + " | 침대 번호" + dto.getDormitoryDTO().getBedName()
                + " | 학번: " + dto.getUserDTO().getId()
                + " | 이름: " + dto.getUserDTO().getName() + "\n";
    }

    public static String PaymentListToString(ApplicationListDTO dto) {
        return "기숙사명 : " + dto.getDormitoryDTO().getDormitoryName()
                + " | 학번: " + dto.getUserDTO().getId()
                + " | 이름: " + dto.getUserDTO().getName()
                + " | 기숙사비: " + dto.getDormitoryDTO().getDormitoryFee()
                + " | 결제 여부: " + dto.getApplicationDTO().getIsPayment()
                + "\n";
    }

    public static String TuberculosisCertificaterListToString(ApplicationListDTO dto) {
        return "기숙사명 : " + dto.getDormitoryDTO().getDormitoryName()
                + " | 학번: " + dto.getUserDTO().getId()
                + " | 결핵 진단서: " + dto.getApplicationDTO().getImage()
                + "\n";
    }

    public static String viewDormitoryFeeDIOToString(ApplicationListDTO dto) {
        return  "| 합계: " + dto.getDormitoryDTO().getDormitoryFee() + dto.getDormitoryDTO().getMoney() + "\n";
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
