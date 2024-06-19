package ase.meditrack.service.algorithm;

import java.util.List;

public record AlgorithmInput(
        Integer numberOfDays,

        List<EmployeeInfo> employees,
        List<ShiftTypeInfo> shiftTypes,
        List<RoleInfo> roles,

        Integer daytimeRequiredPeople,
        Integer nighttimeRequiredPeople,
        Integer maxConsecutiveShifts
) {
}
