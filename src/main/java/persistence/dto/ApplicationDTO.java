package persistence.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationDTO
{
    enum ApplicationStatus {미신청, 신청대기, 탈락;} // enum이라는 함수 선언
    enum CheckOutStatus {환불대기, 환불}

    int preference;
    ApplicationStatus applicationStatus;
    LocalDateTime applicationDate;
    LocalDateTime checkOutDate; // 이게 왜 INT로 되어 있을까?
    String bankName;
    int accountNum;
    CheckOutStatus checkOutStatus;
}
