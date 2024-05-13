package ase.meditrack.model.dto;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import jakarta.validation.constraints.*;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record ShiftTypeDto(
        @Null(groups = CreateValidator.class) @NotNull(groups = UpdateValidator.class) UUID id,
        @NotBlank(groups = CreateValidator.class) String name,
        @NotNull(groups = CreateValidator.class) LocalTime startTime,
        @NotNull(groups = CreateValidator.class) LocalTime endTime,
        UUID team,
        List<UUID> shifts,
        List<UUID> workUsers,
        List<UUID> preferUsers
) {
}
