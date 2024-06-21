package ase.meditrack.service.algorithm;

/**
 * @param name                    the role's name
 * @param daytimeRequiredPeople   the number of people having this role required to be present at day
 * @param nighttimeRequiredPeople the number of people having this role required to be present at night
 * @param maxHoursPerWeek         the max number of hours an employee can work per week
 * @param maxConsecutiveShifts    the max number of consecutive shifts an employee can work
 */
public record RoleInfo(
        String name,
        Integer daytimeRequiredPeople,
        Integer nighttimeRequiredPeople,
        Integer maxHoursPerWeek,
        Integer maxConsecutiveShifts
) {
}
