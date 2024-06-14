package ase.meditrack.service.algorithm;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @param worksShiftTypes             shift types the employee is allowed to work
 * @param minWorkingHoursPerMonth     the minimum working time an employee should work in a given month
 * @param maxWorkingHoursPerMonth     the maximum working time an employee should work in a given month
 * @param optimalWorkingHoursPerMonth the optimal working time an employee should work in a given month.
 *                                    Should already consider things like number of days in a month,
 *                                    working percentage, holidays, ...
 * @param holidays                    days of the month when the employee is on holiday
 * @param offDays                     days of the month when the user doesn't want to work
 * @param role                        the role of the employee
 */
public record EmployeeInfo(
        List<Integer> worksShiftTypes,
        int minWorkingHoursPerMonth,
        int maxWorkingHoursPerMonth,
        int optimalWorkingHoursPerMonth,
        Set<Integer> holidays,
        Set<Integer> offDays,
        Optional<Integer> role
) {
}
