package ase.meditrack.service.algorithm;

import java.util.List;
import java.util.TreeMap;

public record AlgorithmInput(
        Integer numberOfDays,

        List<EmployeeInfo> employees,
        List<ShiftTypeInfo> shiftTypes,
        List<RoleInfo> roles,

        Integer daytimeRequiredPeople,
        Integer nighttimeRequiredPeople,

        TreeMap<Integer, TreeMap<Integer, Integer>> dayToEmployeeToShiftTypeMapping
) {
    boolean workedAtDayPrevMonth(Integer day, Integer employee) {
        var dayVals = dayToEmployeeToShiftTypeMapping.get(day);
        if (dayVals == null) return false;
        return dayVals.containsKey(employee);
    }
}
