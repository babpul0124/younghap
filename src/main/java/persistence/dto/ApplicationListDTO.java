package persistence.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationListDTO { // 신청자 목록 전체 출력을 위한 DTO
    private DormitoryDTO dormitoryDTO;
    private UserDTO userDTO;
    private ApplicationDTO applicationDTO;

    public ApplicationListDTO(DormitoryDTO dormitoryDTO, UserDTO userDTO, ApplicationDTO applicationDTO) {
        this.dormitoryDTO = dormitoryDTO;
        this.userDTO = userDTO;
        this.applicationDTO = applicationDTO;
    }
}
