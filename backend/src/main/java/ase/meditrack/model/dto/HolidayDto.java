package ase.meditrack.model.dto;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalDate;
import java.util.UUID;

public record HolidayDto(
        @Null(groups = CreateValidator.class) @NotNull(groups = UpdateValidator.class) UUID id,
        @NotNull(groups = CreateValidator.class) @FutureOrPresent(groups = CreateValidator.class) LocalDate startDate,
        @NotNull(groups = CreateValidator.class) @FutureOrPresent(groups = CreateValidator.class) LocalDate endDate,
        @NotNull(groups = CreateValidator.class) Boolean isApproved,
        UUID user
) {
}
