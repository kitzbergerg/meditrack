package ase.meditrack.model.dto;

import java.util.List;
import java.util.UUID;

public record UserEntityDto(
        UUID id,
        String role,
        Float workingHoursPercentage,
        Integer currentOverTime,
        List<String> specialSkills,
        TeamDto team,
        List<HolidayDto> holidays,
        PreferencesDto preferences,
        List<ShiftSwapDto> requestedShiftSwaps,
        List<ShiftSwapDto> suggestedShiftSwaps,
        List<ShiftDto> shifts,
        List<ShiftTypeDto> canWorkShiftTypes,
        List<ShiftTypeDto> preferredShiftTypes
) {
}
