package ase.meditrack.service.algorithm;

/**
 * @param name                     the role's name
 * @param daytimeRequiredPeople    the number of people having this role required to be present at day
 * @param nighttimeRequiredPeople  the number of people having this role required to be present at night
 */
public record RoleInfo(
        String name,
        Integer daytimeRequiredPeople,
        Integer nighttimeRequiredPeople
) {
}
