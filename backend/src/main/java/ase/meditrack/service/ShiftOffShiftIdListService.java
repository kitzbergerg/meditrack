package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.ShiftOffShiftIdList;
import ase.meditrack.repository.ShiftOffShiftIdListRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ShiftOffShiftIdListService {
    private final ShiftOffShiftIdListRepository repository;

    public ShiftOffShiftIdListService(ShiftOffShiftIdListRepository repository) {
        this.repository = repository;
    }

    /**
     * Fetches all shift of shift id lists from the database.
     *
     * @return List of all shift of shift id lists
     */
    public List<ShiftOffShiftIdList> findAll() {
        return repository.findAll();
    }

    /**
     * Fetches a shift of shift id list by id from the database.
     *
     * @param id the id of the shift of shift id list
     * @return the shift of shift id list
     */
    public ShiftOffShiftIdList findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("shiftOffShiftIdList not found"));
    }

    /**
     * Creates a shift of shift id list in the database.
     *
     * @param shiftOffShiftIdList the shift of shift id list to create
     * @return the created shift of shift id list
     */
    public ShiftOffShiftIdList create(ShiftOffShiftIdList shiftOffShiftIdList) {
        return repository.save(shiftOffShiftIdList);
    }

    /**
     * Updates a shift of shift id list in the database.
     *
     * @param shiftOffShiftIdList the shift of shift id list to update
     * @return the updated shift of shift id list
     */
    public ShiftOffShiftIdList update(ShiftOffShiftIdList shiftOffShiftIdList) {
        ShiftOffShiftIdList existing = repository.findById(shiftOffShiftIdList.getId())
                .orElseThrow(() -> new NotFoundException("shiftOffShiftIdList not found"));

        if (shiftOffShiftIdList.getShiftOffShiftIdList() != null) {
            existing.setShiftOffShiftIdList(shiftOffShiftIdList.getShiftOffShiftIdList());
        }

        return repository.save(existing);
    }

    /**
     * Deletes a shift of shift id list from the database.
     *
     * @param id the id of the shift of shift id list to delete
     */
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
