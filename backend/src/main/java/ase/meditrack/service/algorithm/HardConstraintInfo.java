package ase.meditrack.service.algorithm;

import java.util.Map;

public record HardConstraintInfo(
        // TODO: Maybe add required roles per specific day
        // Key is index of role, value is required amount
        Map<Integer, Integer> daytimeRequiredRoles,
        Map<Integer, Integer> nighttimeRequiredRoles,
        // TODO: Maybe add staffing level for specific day
        // Staffing level
        int daytimeRequiredPeople,
        int nighttimeRequiredPeople,
        int allowedFlexTimeTotal,
        int allowedFlexTimePerMonth
) {
}
