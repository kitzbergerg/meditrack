package ase.meditrack.model.dto;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalTime;
import java.util.UUID;

public record SimpleShiftTypeDto(@Null(groups = CreateValidator.class) @NotNull(groups = UpdateValidator.class) UUID id,
                                 @NotBlank(groups = CreateValidator.class) String name,
                                 @NotNull(groups = CreateValidator.class) LocalTime startTime,
                                 @NotNull(groups = CreateValidator.class) LocalTime endTime
) {
}
