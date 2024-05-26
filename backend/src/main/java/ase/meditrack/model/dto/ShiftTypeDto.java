package ase.meditrack.model.dto;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record ShiftTypeDto(
        @Null(groups = CreateValidator.class) @NotNull(groups = UpdateValidator.class) UUID id,
        @NotBlank(groups = CreateValidator.class)
        @NotBlank(groups = UpdateValidator.class)
        @Length(max = 40, groups = {CreateValidator.class, UpdateValidator.class})
        String name,
        @NotNull(groups = CreateValidator.class) LocalTime startTime,
        @NotNull(groups = CreateValidator.class) LocalTime endTime,
        @NotNull(groups = CreateValidator.class) LocalTime breakStartTime,
        @NotNull(groups = CreateValidator.class) LocalTime breakEndTime,
        @NotBlank(groups = CreateValidator.class)
        @NotBlank(groups = UpdateValidator.class)
        @Pattern(regexp = "Day|Night", message = "Shift Type must be either 'Day' or 'Night'",
                groups = {CreateValidator.class, UpdateValidator.class})
        String type,
        @NotBlank(groups = CreateValidator.class)
        @NotBlank(groups = UpdateValidator.class)
        @Size(min = 7, max = 7, groups = {CreateValidator.class, UpdateValidator.class})
        @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", groups = {CreateValidator.class, UpdateValidator.class})
        String color,
        @NotBlank(groups = CreateValidator.class)
        @NotBlank(groups = UpdateValidator.class)
        @Length(max = 4, groups = {CreateValidator.class, UpdateValidator.class})
        String abbreviation,
        UUID team,
        List<UUID> shifts,
        List<UUID> workUsers,
        List<UUID> preferUsers
) {
}
