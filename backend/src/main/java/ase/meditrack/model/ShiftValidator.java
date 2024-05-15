package ase.meditrack.model;

import ase.meditrack.exception.ValidationException;
import ase.meditrack.model.entity.Shift;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
@NoArgsConstructor
public class ShiftValidator {

    public void validateShiftOnCreate(Shift shift) {
        log.trace("Validating shift on create: {}", shift);

        if (shift.getId() != null) {
            throw new ValidationException("Id must be null");
        }
        if (shift.getDate() == null || shift.getDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Date must not be null and not in the past");
        }
    }

    public void validateShiftOnUpdate(Shift shift) {
        log.trace("Validating shift on update: {}", shift);

        if (shift.getId() == null) {
            throw new ValidationException("Id must not be null");
        }
        if (shift.getDate() != null && shift.getDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Date must not be in the past");
        }
    }
}
