package persistence.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserDTO extends DTO{
    private int userId;
    private String name;
    private String userRoll;
    private String loginId;
    private String password;

    private double grade;
    private String address;
    private String gender;
    private Boolean is_snoring;
    private String position;

    private int office_tel;

    public UserDTO() { };
}
