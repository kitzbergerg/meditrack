package ase.meditrack.service.algorithm;

import java.util.Map;

public record HardConstraintInfo(
        // TODO #37: Maybe add required roles per specific day
        // Key is index of role, value is required amount
        Map<Integer, Integer> daytimeRequiredRoles,
        Map<Integer, Integer> nighttimeRequiredRoles,
        // TODO #37: Maybe add staffing level for specific day
        // Staffing level
        int allowedFlexTimeTotal,
        int allowedFlexTimePerMonth,
        int mandatoryOffDays,
        int minRestPeriod,
        int maximumShiftLengths
) {
}
