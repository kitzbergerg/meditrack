package ase.meditrack.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.PositiveOrZero;

public record HardConstraintsDto (
        @Nullable @PositiveOrZero Integer workingHours,
        @Nullable @PositiveOrZero Integer maxWeeklyHours,
        @Nullable @PositiveOrZero Integer maxConsecutiveShifts,
        @Nullable @PositiveOrZero Integer daytimeRequiredPeople,
        @Nullable @PositiveOrZero Integer nighttimeRequiredPeople
) {
}

