package ase.meditrack.model.dto;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.UUID;

public record RoleDto(
        @Null(groups = CreateValidator.class) @NotNull(groups = UpdateValidator.class) UUID id,
        @NotBlank(groups = CreateValidator.class) @NotBlank(groups = UpdateValidator.class) String name,
        @NotBlank(groups = CreateValidator.class) String color,
        @NotBlank(groups = CreateValidator.class) String abbreviation,
        List<UUID> users
) {
}
