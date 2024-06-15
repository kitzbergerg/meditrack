package ase.meditrack.model.dto;

import java.util.UUID;

public record UserMonthlyHoursDto(UUID userId,
                                  Float targetWorkingHours,
                                  Float actualWorkingHours,
                                  Float totalOverTime
) {
}
