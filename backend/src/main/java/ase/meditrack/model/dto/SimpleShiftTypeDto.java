package ase.meditrack.model.dto;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record SimpleShiftTypeDto(@Null(groups = CreateValidator.class) @NotNull(groups = UpdateValidator.class) UUID id,
                                 @NotBlank(groups = CreateValidator.class) String name,
                                 @NotNull(groups = CreateValidator.class) LocalTime startTime,
                                 @NotNull(groups = CreateValidator.class) LocalTime endTime,
                                 @NotNull(groups = CreateValidator.class) LocalTime breakStartTime,
                                 @NotNull(groups = CreateValidator.class) LocalTime breakEndTime,
                                 @NotBlank(groups = CreateValidator.class)
                                 @NotBlank(groups = UpdateValidator.class)
                                 String type,
                                 @NotBlank(groups = CreateValidator.class)
                                 @NotBlank(groups = UpdateValidator.class)
                                 @Size(min = 7, max = 7)
                                 @Pattern(regexp = "^#([A-Fa-f0-9]{6})$")
                                 String color,
                                 @NotBlank(groups = CreateValidator.class)
                                 @NotBlank(groups = UpdateValidator.class)
                                 @Length(max = 4)
                                 String abbreviation,
                                 @NotNull(groups = CreateValidator.class)
                                 @NotNull(groups = UpdateValidator.class)
                                 UUID team

) {
}
