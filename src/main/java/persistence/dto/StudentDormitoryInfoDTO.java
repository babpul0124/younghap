package persistence.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDormitoryInfoDTO {
    private int studentId;
    private int dormitoryId;
    private int mealFrequency;
    private int dormitoryFee;
    private int money;
}
