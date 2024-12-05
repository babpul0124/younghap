package persistence.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationDTO
{
    enum ApplicationStatus {대기, 승인, 탈락;} // enum이라는 함수 선언
    enum CheckOutStatus {환불대기, 퇴실;}
    enum IsPayment {미납부, 납부;}

    int applicationId;
    int preference;
    ApplicationStatus applicationStatus;
    LocalDateTime applicationDate;
    LocalDateTime checkOutDate;
    String bankName;
    int accountNum;
    CheckOutStatus checkOutStatus;
    IsPayment isPayment;
}