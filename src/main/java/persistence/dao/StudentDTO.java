package persistence.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDTO extends UserDTO
{
    int score;
    String yearAndSemester;
    String lastTwoSemesters;
    double grade;
    String address;
    String gender;
    boolean is_snoring; // TINYINT(1)타입임.
    byte[] image; // 결핵진단서이미지 바이너리 파일임.
}
