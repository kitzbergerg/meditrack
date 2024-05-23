package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.ShiftTypeValidator;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.repository.ShiftTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ShiftTypeService {
    private final ShiftTypeRepository repository;
    private final ShiftTypeValidator validator;

    public ShiftTypeService(ShiftTypeRepository repository, ShiftTypeValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    /**
     * Fetches all shift types from the database.
     *
     * @return List of all shift types
     */
    public List<ShiftType> findAll() {
        return repository.findAll();
    }

    /**
     * Fetches a shift type by id from the database.
     *
     * @param id the id of the shift type
     * @return the shift type
     */
    public ShiftType findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("shiftType not found"));
    }

    /**
     * Creates a shift type in the database.
     *
     * @param shiftType the shift type to create
     * @return the created shift type
     */
    public ShiftType create(ShiftType shiftType) {
        validator.shiftTypeCreateValidation(shiftType);
        return repository.save(shiftType);
    }

    /**
     * Updates a shift type in the database.
     *
     * @param shiftType the shift type to update
     * @return the updated shift type
     */
    public ShiftType update(ShiftType shiftType) {
        ShiftType dbShiftType = findById(shiftType.getId());

        if (shiftType.getName() != null) dbShiftType.setName(shiftType.getName());
        if (shiftType.getStartTime() != null) dbShiftType.setStartTime(shiftType.getStartTime());
        if (shiftType.getEndTime() != null) dbShiftType.setEndTime(shiftType.getEndTime());
        if (shiftType.getBreakStartTime() != null) dbShiftType.setBreakStartTime(shiftType.getBreakStartTime());
        if (shiftType.getBreakEndTime() != null) dbShiftType.setBreakEndTime(shiftType.getBreakEndTime());
        if (shiftType.getType() != null) dbShiftType.setType(shiftType.getType());
        if (shiftType.getColor() != null) dbShiftType.setColor(shiftType.getColor());
        if (shiftType.getAbbreviation() != null) dbShiftType.setAbbreviation(shiftType.getAbbreviation());
        if (shiftType.getShifts() != null) dbShiftType.setShifts(shiftType.getShifts());
        if (shiftType.getWorkUsers() != null) dbShiftType.setWorkUsers(shiftType.getWorkUsers());
        if (shiftType.getPreferUsers() != null) dbShiftType.setPreferUsers(shiftType.getPreferUsers());

        validator.shiftTypeUpdateValidation(dbShiftType);

        return repository.save(dbShiftType);
    }

    /**
     * Deletes a shift type from the database.
     *
     * @param id the id of the shift type to delete
     */
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
