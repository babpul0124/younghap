package persistence.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDTO
{
    LocalDateTime startDate;
    LocalDateTime endDate;
    String eventName;
    LocalDateTime originalCheckOutDate;
    LocalDateTime checkInDate;
}
