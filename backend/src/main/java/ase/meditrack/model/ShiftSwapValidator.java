package ase.meditrack.model;

import ase.meditrack.exception.ValidationException;
import ase.meditrack.model.entity.ShiftSwap;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@NoArgsConstructor
public class ShiftSwapValidator {

    public void validateShiftSwapOnCreate(ShiftSwap shiftSwap) {
        log.trace("Validating shift swap on create: {}", shiftSwap);

        if (shiftSwap.getId() != null) {
            throw new ValidationException("Id must be null");
        }
        if (shiftSwap.getSwapRequestingUser() == null) {
            throw new ValidationException("Requesting user must not be null");
        }
        if (shiftSwap.getRequestedShift() == null) {
            throw new ValidationException("Requested shift must not be null");
        }
    }

    public void validateShiftSwapOnUpdate(ShiftSwap shiftSwap) {
        log.trace("Validating shift swap on update: {}", shiftSwap);

        if (shiftSwap.getId() != null) {
            throw new ValidationException("Id must be null");
        }
    }
}
