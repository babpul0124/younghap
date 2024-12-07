package persistence.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCheckOutResultDTO {

    int studentId;
    String checkOutDate;
    String bankName;
    String accountNum;
    String checkOutStatus;
    String dormitoryName;
}
