package ase.meditrack.model.dto;

import java.util.List;
import java.util.UUID;

public record MonthlyPlanDto(int month, int year, UUID teamId, List<DayShiftsDto> dayShifts) {
}
