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

    public List<ShiftOffShiftIdList> findAll() {
        return repository.findAll();
    }

    public ShiftOffShiftIdList findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("shiftOffShiftIdList not found"));
    }

    public ShiftOffShiftIdList create(ShiftOffShiftIdList shiftOffShiftIdList) {
        return repository.save(shiftOffShiftIdList);
    }

    public ShiftOffShiftIdList update(ShiftOffShiftIdList shiftOffShiftIdList) {
        ShiftOffShiftIdList existing = repository.findById(shiftOffShiftIdList.getId())
                .orElseThrow(() -> new NotFoundException("shiftOffShiftIdList not found"));

        if (shiftOffShiftIdList.getShiftOffShiftIdList() != null) {
            existing.setShiftOffShiftIdList(shiftOffShiftIdList.getShiftOffShiftIdList());
        }

        return repository.save(existing);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
