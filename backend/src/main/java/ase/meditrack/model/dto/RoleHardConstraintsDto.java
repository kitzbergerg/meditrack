package ase.meditrack.model.dto;

import ase.meditrack.model.CreateValidator;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record RoleHardConstraintsDto(
        @NotNull UUID roleId,
        @NotNull(groups = CreateValidator.class) @PositiveOrZero Integer daytimeRequiredPeople,
        @NotNull(groups = CreateValidator.class) @PositiveOrZero Integer nighttimeRequiredPeople,
        @NotNull(groups = CreateValidator.class) @PositiveOrZero Integer allowedFlextimeTotal,
        @NotNull(groups = CreateValidator.class) @PositiveOrZero Integer allowedFlextimePerMonth,
        @NotNull(groups = CreateValidator.class) @PositiveOrZero Integer workingHours,
        @NotNull(groups = CreateValidator.class) @PositiveOrZero Integer maxWeeklyHours,
        @NotNull(groups = CreateValidator.class) @PositiveOrZero  @Min(0) @Max(31) Integer maxConsecutiveShifts
) {
}
