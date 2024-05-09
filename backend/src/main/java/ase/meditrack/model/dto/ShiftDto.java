package ase.meditrack.model.dto;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ShiftDto(
        @Null(groups = CreateValidator.class) @NotNull(groups = UpdateValidator.class) UUID id,
        @FutureOrPresent(groups = { CreateValidator.class, UpdateValidator.class }) LocalDate date,
        @NotNull(groups = CreateValidator.class) MonthlyPlanDto monthlyPlan,
        @NotNull(groups = CreateValidator.class) ShiftTypeDto shiftType,
        @NotNull(groups = CreateValidator.class) List<UserEntityDto> users,
        List<ShiftSwapDto> suggestedShiftSwaps,
        List<ShiftSwapDto> requestedShiftSwaps
) {
}
