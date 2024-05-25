package ase.meditrack.model;

import ase.meditrack.model.entity.ShiftType;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ShiftTypeValidator {


    public void shiftTypeValidation(ShiftType shiftType) throws ValidationException {
        log.trace("Makes validation for {}", shiftType);

        boolean isOvernightShift = shiftType.getEndTime().isBefore(shiftType.getStartTime());
        if (isOvernightShift) {
            if (!(shiftType.getBreakStartTime().isAfter(shiftType.getStartTime())
                    || shiftType.getBreakStartTime().isBefore(shiftType.getEndTime()))) {
                throw new ValidationException(
                        "Break Starting Time has to be within the working hours for overnight shifts");
            }
            if (!(shiftType.getBreakEndTime().isAfter(shiftType.getStartTime())
                    || shiftType.getBreakEndTime().isBefore(shiftType.getEndTime()))) {
                throw new ValidationException(
                        "Break Ending Time has to be within the working hours for overnight shifts");
            }
        } else {
            if (!(shiftType.getBreakStartTime().isAfter(shiftType.getStartTime())
                    && shiftType.getBreakStartTime().isBefore(shiftType.getEndTime()))) {
                throw new ValidationException("Break Starting Time has to be within the working hours");
            }
            if (!(shiftType.getBreakEndTime().isAfter(shiftType.getStartTime())
                    && shiftType.getBreakEndTime().isBefore(shiftType.getEndTime()))) {
                throw new ValidationException("Break Ending Time has to be within the working hours");
            }
        }
        if (shiftType.getBreakStartTime().isAfter(shiftType.getBreakEndTime())) {
            throw new ValidationException("Break Starting Time has to be before Break Ending Time");
        }
    }
}
