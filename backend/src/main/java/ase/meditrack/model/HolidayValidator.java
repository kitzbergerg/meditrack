package ase.meditrack.model;

import ase.meditrack.exception.ValidationException;
import ase.meditrack.model.entity.Holiday;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@NoArgsConstructor
@Slf4j
public class HolidayValidator {

    public void validateHolidayOnCreate(Holiday holiday) {
        log.trace("Validating holiday on create: {}", holiday);

        if (holiday.getId() != null) {
            throw new ValidationException("Id must not be set");
        }
        if (holiday.getStartDate() == null) {
            throw new ValidationException("Start date must not be null");
        }
        if (holiday.getEndDate() == null) {
            throw new ValidationException("End date must not be null");
        }
        if (holiday.getStartDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Start date must not be in the past");
        }
        if (holiday.getStartDate().isAfter(holiday.getEndDate())) {
            throw new ValidationException("Start date must not be after end date");
        }
        if (holiday.getIsApproved() == null) {
            throw new ValidationException("Is approved must not be null");
        }
        if (holiday.getUser() == null) {
            throw new ValidationException("User must not be null");
        }
    }

    public void validateHolidayOnUpdate(Holiday holiday) {
        log.trace("Validating holiday on update: {}", holiday);

        if (holiday.getId() == null) {
            throw new ValidationException("Id must be set");
        }
        if (holiday.getStartDate() != null && holiday.getStartDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Start date should not be in the past");
        }
        if (holiday.getStartDate() != null && holiday.getEndDate() != null &&
                holiday.getStartDate().isAfter(holiday.getEndDate())) {
            throw new ValidationException("Start date should not be after end date");
        }
    }
}
