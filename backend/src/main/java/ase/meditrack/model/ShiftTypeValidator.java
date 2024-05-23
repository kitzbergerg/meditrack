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

    public void shiftTypeCreateValidation(ShiftType shiftType) throws ValidationException {
        LOGGER.trace("Makes create validation for {}", shiftType);

        if (!(shiftType.getType().equals("Day") || shiftType.getType().equals("Night"))) {
            throw new ValidationException("Shift Type type has to be either Day or Night");
        }

        validateTimes(shiftType);
        validateUnique(shiftType);
    }

    public void shiftTypeUpdateValidation(ShiftType shiftType) throws ValidationException {
        LOGGER.trace("Makes update validation for {}", shiftType);

        if (!(shiftType.getType().equals("Day") || shiftType.getType().equals("Night"))) {
            throw new ValidationException("Shift Type type has to be either Day or Night");
        }

        validateTimes(shiftType);
        validateUnique(shiftType);
    }

    public void validateTimes(ShiftType shiftType) {
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

    public void validateUnique(ShiftType shiftType) {
        List<ShiftType> shiftTypes = shiftTypeRepository.findAll();

        for (ShiftType st:shiftTypes) {
            if (shiftType.getName().equals(st.getName())) {
                throw new ValidationException("Name has to be unique.");
            }
            if (shiftType.getColor().equals(st.getColor())) {
                throw new ValidationException("Color has to be unique.");
            }
            if (shiftType.getAbbreviation().equals(st.getAbbreviation())) {
                throw new ValidationException("Abbreviation has to be unique.");
            }
        }
    }
}