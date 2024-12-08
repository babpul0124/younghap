package persistence.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter

public class CheckInDTO extends DTO{
    private int dormitoryId;
    private String dormitoryName;
    private int dormitoryFee;
    private int userId;
    private String userName;
    private int preference;
    private int mealFrequency;
    private int mealMoney;
    private int isSnoring;
    private String applicationStatus;
    private LocalDate applicationDate;
    private int applicationId;
    private int roomCapacityNum;
    private int roomNum;
    private int bedNum;
    private double grade;
    private int personalRoomId;
    private String isPayment;
    private Byte[] image;


    public CheckInDTO() { };
}
