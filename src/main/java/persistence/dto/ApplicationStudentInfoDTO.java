package persistence.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ApplicationStudentInfoDTO {
    private int studentId;       // application.student_id
    private double grade;        // student.grade
    private int preference;      // application.preference
    private int dormitoryId;     // application.dormitory_id
}
