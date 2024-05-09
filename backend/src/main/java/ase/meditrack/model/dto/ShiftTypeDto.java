package ase.meditrack.model.dto;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record ShiftTypeDto(
        @Null(groups = CreateValidator.class) @NotNull(groups = UpdateValidator.class) UUID id,
        @NotBlank(groups = CreateValidator.class) String name,
        @FutureOrPresent(groups = { CreateValidator.class, UpdateValidator.class }) LocalTime startTime,
        @FutureOrPresent(groups = { CreateValidator.class, UpdateValidator.class }) LocalTime endTime,
        @NotNull(groups = CreateValidator.class) TeamDto team,
        List<ShiftDto> shifts,
        List<UserEntityDto> workUsers,
        List<UserEntityDto> preferUsers
) {
}
