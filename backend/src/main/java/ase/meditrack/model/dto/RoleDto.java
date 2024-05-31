package ase.meditrack.model.dto;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.UUID;

public record RoleDto(
        @Null(groups = CreateValidator.class) @NotNull(groups = UpdateValidator.class) UUID id,
        @NotBlank(groups = CreateValidator.class)
        @NotBlank(groups = UpdateValidator.class)
        @Length(max = 40, groups = {CreateValidator.class, UpdateValidator.class})
        String name,
        @NotBlank(groups = CreateValidator.class)
        @NotBlank(groups = UpdateValidator.class)
        @Size(min = 7, max = 7, groups = {CreateValidator.class, UpdateValidator.class})
        @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", groups = {CreateValidator.class, UpdateValidator.class})
        String color,
        @NotBlank(groups = CreateValidator.class)
        @NotBlank(groups = UpdateValidator.class)
        @Length(max = 4, groups = {CreateValidator.class, UpdateValidator.class})
        String abbreviation,
        List<UUID> users,
        UUID team,
        List<UUID> shiftTypes
) {
}
