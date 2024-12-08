package persistence.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckOutDTO extends DTO {
    private int UserId;
    private String applicationStatus;
    private int dormitoryId;
    private String dormitoryName;
    private int applicationId;
    private String isPayment;
    private LocalDateTime checkOutDate;
    private String bankName;
    private String accountNum;
    private String checkOutStatus;

    public CheckOutDTO() {};
}
