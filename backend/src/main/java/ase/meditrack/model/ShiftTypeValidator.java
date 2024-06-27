package ase.meditrack.model;

import ase.meditrack.model.entity.ShiftType;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ShiftTypeValidator {


    /**
     * Check time constraints.
     *
     * @param shiftType the shiftType to check
     * @throws ValidationException if the shiftType has invalid data
     */
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

        // Calculate total working hours
        long totalWorkMinutes;
        if (isOvernightShift) {
            totalWorkMinutes = java.time.Duration.between(shiftType.getStartTime(), shiftType.getEndTime())
                    .plus(java.time.Duration.ofDays(1)).toMinutes();
        } else {
            totalWorkMinutes = java.time.Duration.between(shiftType.getStartTime(), shiftType.getEndTime()).toMinutes();
        }

        // Check for maximum allowable working hours
        long workMinutesWithoutBreak = totalWorkMinutes - java.time.Duration.between(shiftType.getBreakStartTime(),
                shiftType.getBreakEndTime()).toMinutes();
        if (workMinutesWithoutBreak > 13 * 60) {
            throw new ValidationException("The maximum allowable working hours are 13 hours");
        }

        // Check for minimum break time if workday exceeds six hours
        if (totalWorkMinutes > 6 * 60) {
            long breakMinutes = java.time.Duration.between(shiftType.getBreakStartTime(),
                    shiftType.getBreakEndTime()).toMinutes();
            if (breakMinutes < 30) {
                throw new ValidationException("A minimum of 30 minutes break is required"
                        + "for shift longer than six hours");
            }
        }
    }
}
