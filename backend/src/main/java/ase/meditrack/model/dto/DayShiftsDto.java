package ase.meditrack.model.dto;

import java.util.List;

public record DayShiftsDto(int day, List<DailyUserShiftDto> userShifts) {
}
