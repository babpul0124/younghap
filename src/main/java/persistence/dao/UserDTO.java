package persistence.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO
{
    int id;
    String name;
    String login_id;
    String password;
}
