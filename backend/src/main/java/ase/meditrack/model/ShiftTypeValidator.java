package ase.meditrack.model;

import ase.meditrack.exception.ValidationException;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.Team;
import ase.meditrack.repository.ShiftTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
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

        validateTimes(shiftType);
    }

    public void shiftTypeUpdateValidation(ShiftType shiftTypeToUpdate) throws ValidationException {
        LOGGER.trace("Makes update validation for {}", shiftTypeToUpdate);

        UUID id = shiftTypeToUpdate.getId();

        if (id == null) {
            throw new ValidationException("Id should be set");
        }
        if (!repository.existsById(id)){
            throw new ValidationException("Id is not correct");
        }

        ShiftType mergedShiftType = repository.getById(id);

        if (shiftTypeToUpdate.getName() != null) {
            if (!(mergedShiftType.getName().equals(shiftTypeToUpdate.getName()))) {
                validateStringInput(shiftTypeToUpdate.getName(), "name");
            }
            mergedShiftType.setName(shiftTypeToUpdate.getName());
        }
        if (shiftTypeToUpdate.getStartTime() != null) {
            mergedShiftType.setStartTime(shiftTypeToUpdate.getStartTime());
        }
        if (shiftTypeToUpdate.getEndTime() != null) {
            mergedShiftType.setEndTime(shiftTypeToUpdate.getEndTime());
        }
        if (shiftTypeToUpdate.getBreakStartTime() != null) {
            mergedShiftType.setBreakStartTime(shiftTypeToUpdate.getBreakStartTime());
        }
        if (shiftTypeToUpdate.getBreakEndTime() != null) {
            mergedShiftType.setBreakEndTime(shiftTypeToUpdate.getBreakEndTime());
        }
        if (shiftTypeToUpdate.getType() != null) {
            if (!(mergedShiftType.getType().equals(shiftTypeToUpdate.getType()))) {
                validateStringInput(shiftTypeToUpdate.getType(), "type");
            }
            mergedShiftType.setType(shiftTypeToUpdate.getType());
        }
        if (shiftTypeToUpdate.getColor() != null) {
            if (!(mergedShiftType.getColor().equals(shiftTypeToUpdate.getColor()))) {
                validateStringInput(shiftTypeToUpdate.getColor(), "color");
            }
            mergedShiftType.setColor(shiftTypeToUpdate.getColor());
        }
        if (shiftTypeToUpdate.getAbbreviation() != null) {
            if (!(mergedShiftType.getAbbreviation().equals(shiftTypeToUpdate.getAbbreviation()))) {
                validateStringInput(shiftTypeToUpdate.getAbbreviation(), "abbreviation");
            }
            mergedShiftType.setAbbreviation(shiftTypeToUpdate.getAbbreviation());
        }

        validateTimes(mergedShiftType);
    }

    public void validateTimes(ShiftType shiftType) {
        // TODO: validate duration of shift
        // TODO: validate duration of break

        // TODO: validation for overnight break times?
        if (shiftType.getBreakStartTime().isAfter(shiftType.getBreakEndTime())) {
            throw new ValidationException("Break Starting Time has to be before Break Ending Time");
        }

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

    public boolean isUnique(String input, String attribute) {
        List<ShiftType> allShiftTypes = repository.findAll();
        for (ShiftType shiftType : allShiftTypes) {
            switch (attribute) {
                case "name" -> {
                    if (shiftType.getName().equals(input)) {
                        return false;
                    }
                }
                case "abbreviation" -> {
                    if (shiftType.getAbbreviation().equals(input)) {
                        return false;
                    }
                }
                case "color" -> {
                    if (shiftType.getColor().equals(input)) {
                        return false;
                    }
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
            if (input.length() > 4) {
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

        // TODO: validate color (?)
    }

}