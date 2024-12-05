package persistence.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckOutDTO {
    private String dormitoryId;
    private int studentId;
    private LocalDateTime checkOutDate;
    private String bankName;
    private String accountNum;
    private String checkOutStatus;
}
