package persistence.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter

public class DormitoryDTO extends DTO{
    private int userId;
    private String userName;
    private String eventName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDate writedDate;
    private int dormitoryId;
    private String dormitoryName;
    private int dormitoryFee;
    private int roomCapacityNum;
    private int mealFrequency;
    private int mealMoney;

    public DormitoryDTO() { };
}
