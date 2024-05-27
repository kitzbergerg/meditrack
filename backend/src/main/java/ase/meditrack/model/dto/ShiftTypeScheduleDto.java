package ase.meditrack.model.dto;

import java.time.LocalTime;

public record ShiftTypeScheduleDto(String name, String color, LocalTime startTime, LocalTime endTime,
                                   LocalTime breakStartTime, LocalTime breakEndTime, String abbreviation) {
}
