package ase.meditrack.model.dto;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.util.List;
import java.util.UUID;

public record ShiftSwapDto(
        @Null(groups = CreateValidator.class) @NotNull(groups = UpdateValidator.class) UUID id,
        UUID swapRequestingUser,
        UUID requestedShift,
        List<UUID> swapSuggestingUsers,
        List<UUID> suggestedShifts
) {
}
