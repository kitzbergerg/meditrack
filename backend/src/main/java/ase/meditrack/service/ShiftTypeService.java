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

    public ShiftType findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<ShiftType> findAll() {
        return repository.findAll();
    }

    public ShiftType create(ShiftType shiftType) {
        return repository.save(shiftType);
    }

    public ShiftType update(ShiftType shiftType) {
        ShiftType existing = repository.findById(shiftType.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (shiftType.getName() != null) {
            existing.setName(shiftType.getName());
        }
        if (shiftType.getStartTime() != null) {
            existing.setStartTime(shiftType.getStartTime());
        }
        if (shiftType.getEndTime() != null) {
            existing.setEndTime(shiftType.getEndTime());
        }
        if (shiftType.getTeam() != null) {
            existing.setTeam(shiftType.getTeam());
        }
        if (shiftType.getShifts() != null) {
            existing.setShifts(shiftType.getShifts());
        }
        if (shiftType.getWorkUsers() != null) {
            existing.setWorkUsers(shiftType.getWorkUsers());
        }
        if (shiftType.getPreferUsers() != null) {
            existing.setPreferUsers(shiftType.getPreferUsers());
        }

        return repository.save(existing);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
