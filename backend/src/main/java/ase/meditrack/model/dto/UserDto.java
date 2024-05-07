package ase.meditrack.model.dto;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;

public record UserDto(
        @Null(groups = CreateValidator.class) @NotNull(groups = UpdateValidator.class) UUID id,
        @NotBlank(groups = CreateValidator.class) @Null(groups = UpdateValidator.class) String username,
        @NotBlank(groups = CreateValidator.class) String password,
        @NotBlank(groups = CreateValidator.class) @Email String email,
        @NotBlank(groups = CreateValidator.class) String firstName,
        @NotBlank(groups = CreateValidator.class) String lastName,
        List<String> roles,
        UUID role,
        @NotNull(groups = CreateValidator.class) @Positive(groups = CreateValidator.class) Float workingHoursPercentage,
        @Null(groups = CreateValidator.class) Integer currentOverTime,
        List<String> specialSkills,
        UUID team,
        List<UUID> holidays,
        UUID preferences,
        List<UUID> requestedShiftSwaps,
        List<UUID> suggestedShiftSwaps,
        List<UUID> shifts,
        List<UUID> canWorkShiftTypes,
        List<UUID> preferredShiftTypes
) {
}
