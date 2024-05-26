package ase.meditrack.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Map;
import java.util.UUID;

public record HardConstraintsDto(
        @NotNull UUID id,
        @Nullable Map<UUID, UUID> shiftOffShift,
        @Nullable Map<UUID, Integer> daytimeRequiredRoles,
        @Nullable Map<UUID, Integer> nighttimeRequiredRoles,
        @Nullable @PositiveOrZero Integer daytimeRequiredPeople,
        @Nullable @PositiveOrZero Integer nighttimeRequiredPeople,
        @Nullable @PositiveOrZero Integer allowedFlextimeTotal,
        @Nullable @PositiveOrZero Integer allowedFlextimePerMonth
) {
}

