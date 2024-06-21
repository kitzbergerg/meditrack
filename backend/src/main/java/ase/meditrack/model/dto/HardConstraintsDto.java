package ase.meditrack.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;

public record HardConstraintsDto(
        @Nullable @PositiveOrZero Integer workingHours,
        @Nullable @PositiveOrZero Integer maxWeeklyHours,
        @Nullable @Min(0) @Max(31) Integer maxConsecutiveShifts,
        @Nullable @PositiveOrZero Integer daytimeRequiredPeople,
        @Nullable @PositiveOrZero Integer nighttimeRequiredPeople
) {
}

