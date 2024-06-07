package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.ShiftTypeValidator;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.ShiftTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ShiftTypeService {
    private final ShiftTypeRepository repository;
    private final ShiftTypeValidator validator;
    private final UserService userService;

    public ShiftTypeService(ShiftTypeRepository repository, ShiftTypeValidator validator, UserService userService) {
        this.repository = repository;
        this.validator = validator;
        this.userService = userService;
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
     * Fetches all shift type from a team from the database.
     *
     * @param principal the current user
     * @return List of all shift type
     */
    public List<ShiftType> findAllByTeam(Principal principal) {
        User dm = userService.getPrincipalWithTeam(principal);
        return repository.findAllByTeam(dm.getTeam());
    }

    /**
     * Creates a shift type in the database.
     *
     * @param shiftType the shift type to create
     * @param principal the principal
     * @return the created shift type
     */
    public ShiftType create(ShiftType shiftType, Principal principal) {
        validator.shiftTypeValidation(shiftType);
        User dm = userService.getPrincipalWithTeam(principal);
        List<ShiftType> shiftTypes = new ArrayList<>();
        if (dm.getTeam().getShiftTypes() != null) {
            shiftTypes = dm.getTeam().getShiftTypes();
        }
        shiftTypes.add(shiftType);
        dm.getTeam().setShiftTypes(shiftTypes);
        shiftType.setTeam(dm.getTeam());
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
        if (shiftType.getRequiredRoles() != null) dbShiftType.setRequiredRoles(shiftType.getRequiredRoles());

        validator.shiftTypeValidation(dbShiftType);

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
