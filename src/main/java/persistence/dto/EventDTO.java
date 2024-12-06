package persistence.dto;

import java.time.LocalDateTime;
import java.time.LocalDate;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDTO
{
    LocalDateTime startDate;
    LocalDateTime endDate;
    String eventName;
    LocalDate writedDate;
    UserDTO userDTO;

    LocalDate checkInDate;
    LocalDate originalCheckOutDate;
}
