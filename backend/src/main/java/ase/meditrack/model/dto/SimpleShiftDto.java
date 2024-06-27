package ase.meditrack.model.dto;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record SimpleShiftDto(
        @Null(groups = CreateValidator.class) @NotNull(groups = UpdateValidator.class) UUID id,
        @FutureOrPresent(groups = CreateValidator.class) LocalDate date,
        boolean isSick,
        UUID monthlyPlan,
        SimpleShiftTypeDto shiftType,
        List<UUID> users
) {
}
