package ase.meditrack.service.algorithm;

/**
 * @param name                     the role's name
 * @param daytimeRequiredPeople    the number of people having this role required to be present at day
 * @param nighttimeRequiredPeople  the number of people having this role required to be present at night
 * @param allowedFlexitimeTotal    the total allowed flexitime for this role
 * @param allowedFlexitimePerMonth the allowed flexitime change allowed per month
 */
public record RoleInfo(
        String name,
        Integer daytimeRequiredPeople,
        Integer nighttimeRequiredPeople,
        Integer allowedFlexitimeTotal,
        Integer allowedFlexitimePerMonth
) {
}
