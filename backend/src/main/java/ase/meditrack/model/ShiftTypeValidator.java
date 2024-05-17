package ase.meditrack.model;

import ase.meditrack.exception.ValidationException;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.repository.ShiftTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.UUID;

@Component
public class ShiftTypeValidator {

    private final ShiftTypeRepository repository;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public ShiftTypeValidator(ShiftTypeRepository repository) {
        this.repository = repository;
    }

    public void shiftTypeCreateValidation(ShiftType shiftType) throws ValidationException {
        LOGGER.trace("Makes create validation for {}", shiftType);

        if (shiftType.getId() != null) {
            throw new ValidationException("Id should not be set");
        }
        validateStringInput(shiftType.getName(), "name");
        validateStringInput(shiftType.getType(), "type");
        validateStringInput(shiftType.getColor(), "color");
        validateStringInput(shiftType.getAbbreviation(), "abbreviation");

        // TODO: validate duration of shift
        // TODO: validate color (?)

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
    }

    public void shiftTypeUpdateValidation(ShiftType shiftTypeToUpdate) throws ValidationException {
        LOGGER.trace("Makes update validation for {}", shiftTypeToUpdate);

        String name = shiftTypeToUpdate.getName();
        UUID id = shiftTypeToUpdate.getId();

        if (!repository.existsById(id)){
            throw new ValidationException("Id is not correct");
        }
        if (name == null || name.isBlank()) {
            throw new ValidationException("Role has to have a name");
        }
        if (!isUnique(shiftTypeToUpdate.getName(), "name")) {
            throw new ValidationException("Role with this name already exists");
        }
        if (name.length() >= 40) {
            throw new ValidationException("Name is too long");
        }
    }

    public boolean isUnique(String input, String attribute) {
        List<ShiftType> allShiftTypes = repository.findAll();
        for (ShiftType shiftType : allShiftTypes) {
            if (attribute.equals("name")) {
                if (shiftType.getName().equals(input)) {
                    return false;
                }
            } else if (attribute.equals("abbreviation")) {
                if (shiftType.getAbbreviation().equals(input)) {
                    return false;
                }
            } else if (attribute.equals("color")) {
                if (shiftType.getColor().equals(input)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void validateStringInput(String input, String attribute) {
        if (input == null || input.isBlank()) {
            throw new ValidationException("Shift Type has to have a " + attribute);
        }
        if (!attribute.equals("abbreviation") && input.length() >= 40) {
            throw new ValidationException("Shift Type " + attribute + " is too long");
        }
        if (attribute.equals("abbreviation")) {
            if (input.length() >= 4) {
                throw new ValidationException("Shift Type " + attribute + " is too long");
            }
            if (input.matches(".*\\d.*")) {
                throw new ValidationException("Shift Type " + attribute + " cannot contain digits");
            }
            if (!isUnique(input, "abbreviation")) {
                throw new ValidationException("Shift Type with this abbreviation already exists");
            }
        }
        if (attribute.equals("type") && !(input.equals("Day") || input.equals("Night"))) {
            throw new ValidationException("Shift Type " + attribute + " has to be either Day or Night");
        }
        if (attribute.equals("name") && !isUnique(input, "name")) {
            throw new ValidationException("Shift Type with this name already exists");
        }
        if (attribute.equals("color") && !isUnique(input, "color")) {
            throw new ValidationException("Shift Type with this color already exists");
        }
    }

}