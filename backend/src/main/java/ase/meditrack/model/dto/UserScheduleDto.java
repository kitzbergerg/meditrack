package ase.meditrack.model.dto;

import java.util.UUID;

public record UserScheduleDto(UUID id,
                              String firstName,
                              String lastName,
                              Float workingHoursPercentage,
                              SimpleRoleDto role) {
}
