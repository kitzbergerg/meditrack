package ase.meditrack.model;

import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.repository.ShiftTypeRepository;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Component
public class ShiftTypeValidator {

    private ShiftTypeRepository shiftTypeRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public ShiftTypeValidator(ShiftTypeRepository shiftTypeRepository) {
        this.shiftTypeRepository = shiftTypeRepository;
    }

    public void shiftTypeValidation(ShiftType shiftType) throws ValidationException {
        LOGGER.trace("Makes validation for {}", shiftType);

        boolean isOvernightShift = shiftType.getEndTime().isBefore(shiftType.getStartTime());
        if (isOvernightShift) {
            if (!(shiftType.getBreakStartTime().isAfter(shiftType.getStartTime()) || shiftType.getBreakStartTime().isBefore(shiftType.getEndTime()))) {
                throw new ValidationException("Break Starting Time has to be within the working hours for overnight shifts");
            }
            if (!(shiftType.getBreakEndTime().isAfter(shiftType.getStartTime()) || shiftType.getBreakEndTime().isBefore(shiftType.getEndTime()))) {
                throw new ValidationException("Break Ending Time has to be within the working hours for overnight shifts");
            }
        } else {
            if (!(shiftType.getBreakStartTime().isAfter(shiftType.getStartTime()) && shiftType.getBreakStartTime().isBefore(shiftType.getEndTime()))) {
                throw new ValidationException("Break Starting Time has to be within the working hours");
            }
            if (!(shiftType.getBreakEndTime().isAfter(shiftType.getStartTime()) && shiftType.getBreakEndTime().isBefore(shiftType.getEndTime()))) {
                throw new ValidationException("Break Ending Time has to be within the working hours");
            }
        }
        if (shiftType.getBreakStartTime().isAfter(shiftType.getBreakEndTime())) {
            throw new ValidationException("Break Starting Time has to be before Break Ending Time");
        }
    }
}