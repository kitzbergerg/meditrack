package ase.meditrack.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ShiftScheduleDto(UUID id,
                               String name,
                               String color,
                               LocalTime startTime,
                               LocalTime endTime,
                               LocalDate date,
                               UserScheduleDto user) {
}
