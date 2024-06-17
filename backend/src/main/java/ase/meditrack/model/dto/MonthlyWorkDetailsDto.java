package ase.meditrack.model.dto;

import java.util.UUID;

public record MonthlyWorkDetailsDto(UUID userId,
        float hoursShouldWork,
        float hoursActuallyWorked,
        Integer overtime) {
}
