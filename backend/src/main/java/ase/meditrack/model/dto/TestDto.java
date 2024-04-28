package ase.meditrack.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TestDto(@NotNull Integer id, @NotBlank String value) {
}
