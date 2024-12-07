package persistence.dto;
import java.time.LocalDateTime;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationDTO
{

    public enum ApplicationStatus {대기, 승인, 탈락} // enum이라는 함수 선언
    enum CheckOutStatus {환불대기, 퇴실}
    enum IsPayment {미납부, 납부}

    int applicationId;

    int dormitoryId;
    int id;
    int mealFrequency;

    int preference;
    ApplicationStatus applicationStatus;
    LocalDate applicationDate;
    boolean is_snoring; // TINYINT(1)타입임.

    byte[] image; // 결핵진단서이미지 바이너리 파일임.

    LocalDateTime checkOutDate;
    String bankName;
    int accountNum;
    CheckOutStatus checkOutStatus;
    IsPayment isPayment;

    String result;
}
