package ase.meditrack.service;

import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.repository.ShiftTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ShiftTypeService {
    private final ShiftTypeRepository repository;

    public ShiftTypeService(ShiftTypeRepository repository) {
        this.repository = repository;
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
     * @param id, the id of the shift type
     * @return the shift type
     */
    public ShiftType findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * Creates a shift type in the database.
     *
     * @param shiftType, the shift type to create
     * @return the created shift type
     */
    public ShiftType create(ShiftType shiftType) {
        return repository.save(shiftType);
    }

    /**
     * Updates a shift type in the database.
     *
     * @param shiftType, the shift type to update
     * @return the updated shift type
     */
    public ShiftType update(ShiftType shiftType) {
        ShiftType updatedShiftType = new ShiftType();
        updatedShiftType.setId(shiftType.getId());
        updatedShiftType.setName(shiftType.getName());
        updatedShiftType.setStartTime(shiftType.getStartTime());
        updatedShiftType.setEndTime(shiftType.getEndTime());
        updatedShiftType.setTeam(shiftType.getTeam());
        updatedShiftType.setShifts(shiftType.getShifts());
        updatedShiftType.setWorkUsers(shiftType.getWorkUsers());
        updatedShiftType.setPreferUsers(shiftType.getPreferUsers());

        repository.save(updatedShiftType);

        return updatedShiftType;
    }

    /**
     * Deletes a shift type from the database.
     *
     * @param id, the id of the shift type to delete
     */
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
