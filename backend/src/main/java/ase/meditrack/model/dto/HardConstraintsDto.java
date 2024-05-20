package ase.meditrack.model.dto;

import ase.meditrack.model.CreateValidator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Map;
import java.util.UUID;

public record HardConstraintsDto(
        @NotNull UUID id,
        @NotNull(groups = CreateValidator.class) Map<UUID, UUID> shiftOffShift,
        @NotNull(groups = CreateValidator.class) Map<UUID, Integer> daytimeRequiredRoles,
        @NotNull(groups = CreateValidator.class) Map<UUID, Integer> nighttimeRequiredRoles,
        @NotNull(groups = CreateValidator.class) @PositiveOrZero Integer daytimeRequiredPeople,
        @NotNull(groups = CreateValidator.class) @PositiveOrZero Integer nighttimeRequiredPeople,
        @NotNull(groups = CreateValidator.class) @PositiveOrZero Integer allowedFlextimeTotal,
        @NotNull(groups = CreateValidator.class) @PositiveOrZero Integer allowedFlextimePerMonth
) {
}

