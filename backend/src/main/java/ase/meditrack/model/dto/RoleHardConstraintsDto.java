package ase.meditrack.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record RoleHardConstraintsDto (
    @NotNull UUID roleId,
    @Nullable @PositiveOrZero Integer daytimeRequiredPeople,
    @Nullable @PositiveOrZero Integer nighttimeRequiredPeople,
    @Nullable @PositiveOrZero Integer allowedFlexitimeTotal,
    @Nullable @PositiveOrZero Integer allowedFlexitimeMonthly
) {
}
