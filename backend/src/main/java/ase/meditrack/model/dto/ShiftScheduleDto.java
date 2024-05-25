package ase.meditrack.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record ShiftScheduleDto(UUID id,
                               LocalDate date,
                               ShiftTypeScheduleDto type,
                               List<UserScheduleDto> users) {
}
