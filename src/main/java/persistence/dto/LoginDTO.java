package persistence.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class LoginDTO extends DTO{
    private String loginId;
    private String password;

    public LoginDTO() { };
}
