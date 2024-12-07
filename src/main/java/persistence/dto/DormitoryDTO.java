package persistence.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DormitoryDTO
{

    int dormitoryId;
    String dormitoryName;
    int capacityNum;
    int roomCapacityNum;
    int dormitoryFee;

    int roomNum;
    int bedName;
    int personalRoomId;

    int id;

    int mealFrequency;
    int money;
}
