package ase.meditrack.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.PositiveOrZero;

public record HardConstraintsDto(
        @Nullable @PositiveOrZero Integer daytimeRequiredPeople,
        @Nullable @PositiveOrZero Integer nighttimeRequiredPeople
) {
}

