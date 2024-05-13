package ase.meditrack.service;

import ase.meditrack.model.entity.Shift;
import ase.meditrack.repository.ShiftRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ShiftService {
    private final ShiftRepository repository;

    public ShiftService(ShiftRepository repository) {
        this.repository = repository;
    }

    public Shift findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<Shift> findAll() {
        return repository.findAll();
    }

    public Shift create(Shift shift) {
        return repository.save(shift);
    }

    public Shift update(Shift shift) {
        Shift existing = repository.findById(shift.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (shift.getDate() != null) {
            existing.setDate(shift.getDate());
        }
        if (shift.getMonthlyPlan() != null) {
            existing.setMonthlyPlan(shift.getMonthlyPlan());
        }
        if (shift.getShiftType() != null) {
            existing.setShiftType(shift.getShiftType());
        }
        if (shift.getUsers() != null) {
            existing.setUsers(shift.getUsers());
        }
        if (shift.getSuggestedShiftSwaps() != null) {
            existing.setSuggestedShiftSwaps(shift.getSuggestedShiftSwaps());
        }
        if (shift.getRequestedShiftSwap() != null) {
            existing.setRequestedShiftSwap(shift.getRequestedShiftSwap());
        }

        return repository.save(existing);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
