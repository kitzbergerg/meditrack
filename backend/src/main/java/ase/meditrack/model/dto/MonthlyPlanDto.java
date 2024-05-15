package ase.meditrack.model.dto;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.hibernate.validator.constraints.Range;

import java.util.List;
import java.util.UUID;

public record MonthlyPlanDto(
        @Null(groups = CreateValidator.class) @NotNull(groups = UpdateValidator.class) UUID id,
        @NotNull(groups = CreateValidator.class) @Range(min = MIN_MONTH, max = MAX_MONTH,
                groups = { CreateValidator.class, UpdateValidator.class }) Integer month,
        @NotNull(groups = CreateValidator.class) @Min(MIN_YEAR) Integer year,
        @NotNull(groups = CreateValidator.class) Boolean published,
        @NotNull(groups = CreateValidator.class) UUID team,
        List<UUID> shifts
) {
    private static final int MIN_MONTH = 1;
    private static final int MAX_MONTH = 12;
    private static final int MIN_YEAR = 2024;
}

