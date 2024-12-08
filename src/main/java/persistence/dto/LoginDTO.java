package persistence.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class LoginDTO extends DTO{
    private String loginId;
    private String password;
    private String position; // 학부생, 대학원생

    public LoginDTO() { };
}
