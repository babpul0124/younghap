package persistence.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DormitoryStudentInfoDTO {
    private DormitoryDTO dormitoryDTO;
    private UserDTO userDTO;
}