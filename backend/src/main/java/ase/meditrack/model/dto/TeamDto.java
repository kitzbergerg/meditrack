package ase.meditrack.model.dto;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;
import java.util.UUID;

public record TeamDto(
        @Null(groups = CreateValidator.class) @NotNull(groups = UpdateValidator.class) UUID id,
        @NotBlank(groups = CreateValidator.class) String name,
        @PositiveOrZero(groups = { CreateValidator.class, UpdateValidator.class }) Integer workingHours,
        List<UserEntityDto> users,
        HardConstraintsDto hardConstraints,
        List<MonthlyPlanDto> monthlyPlans,
        List<ShiftTypeDto> shiftTypes
) {
}
