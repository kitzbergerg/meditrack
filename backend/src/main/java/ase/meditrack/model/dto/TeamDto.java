package ase.meditrack.model.dto;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;
import java.util.UUID;

public record TeamDto(
        @Null(groups = CreateValidator.class) @NotNull(groups = UpdateValidator.class) UUID id,
        @NotBlank(groups = CreateValidator.class) String name,
        @PositiveOrZero Integer workingHours,
        List<UUID> roles,
        List<UUID> users,
        UUID hardConstraints,
        List<UUID> monthlyPlans,
        List<UUID> shiftTypes
) {
    /**
     * Use to generate a default value when deserializing in case workingHours is null.
     *
     * @param workingHoursFromJson the value deserialized from json
     * @return workingHours from the json or a default
     */
    @JsonSetter(value = "workingHours", nulls = Nulls.AS_EMPTY)
    public Integer setWorkingHours(Integer workingHoursFromJson) {
        return workingHoursFromJson == null ? 40 : workingHoursFromJson;
    }
}
