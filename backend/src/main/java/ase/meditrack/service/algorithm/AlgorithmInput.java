package ase.meditrack.service.algorithm;

import java.util.List;

public record AlgorithmInput(
        List<EmployeeInfo> employees,
        List<ShiftTypeInfo> shiftTypes,
        List<DayInfo> days,

        List<RoleInfo> roles,

        HardConstraintInfo hardConstraints

        // TODO: add more fields for other constraints
) {
}