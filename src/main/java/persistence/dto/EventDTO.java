package persistence.dto;

import java.time.LocalDateTime;
import java.time.LocalDate;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDTO
{
    int id;
    String name;
    LocalDateTime startDate;
    LocalDateTime endDate;
    String eventName;
    LocalDate writedDate;
    LocalDate checkInDate;
    LocalDate originalCheckOutDate;
}
