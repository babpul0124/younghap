package persistence.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class paymentListDTO {
    private String dormitoryName;
    private int studentId;
    private String studentName;
    private int dormitoryFee;
    private String isPayment;
}
