package persistence.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDTO extends UserDTO
{
    enum position {학부생, 대학원생}

    int score;
    String yearAndSemester;
    String lastTwoSemesters;
    double grade;
    String address;
    String gender;
}
