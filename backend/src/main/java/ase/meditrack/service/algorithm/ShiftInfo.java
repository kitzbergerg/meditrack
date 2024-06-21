package ase.meditrack.service.algorithm;

public record ShiftInfo(
        Integer employee,
        Integer dayOfPrevMonth,
        Integer shiftType
) {
}
