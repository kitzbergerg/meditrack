package ase.meditrack.model.dto;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


public record PreferencesDto(
        @NotNull(groups = CreateValidator.class) UUID id,
        @NotNull(groups = { CreateValidator.class, UpdateValidator.class }) List<LocalDate> offDays
) {
}
