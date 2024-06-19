package ase.meditrack.model.dto;

import ase.meditrack.model.CreateValidator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record RoleHardConstraintsDto(
        @NotNull UUID roleId,
        @NotNull(groups = CreateValidator.class) @PositiveOrZero Integer daytimeRequiredPeople,
        @NotNull(groups = CreateValidator.class) @PositiveOrZero Integer nighttimeRequiredPeople,
        @NotNull(groups = CreateValidator.class) @PositiveOrZero Integer allowedFlextimeTotal,
        @NotNull(groups = CreateValidator.class) @PositiveOrZero Integer allowedFlextimePerMonth
) {
}
