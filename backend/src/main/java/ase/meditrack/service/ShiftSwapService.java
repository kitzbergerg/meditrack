package ase.meditrack.service;

import ase.meditrack.model.entity.ShiftSwap;
import ase.meditrack.repository.ShiftSwapRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ShiftSwapService {
    private final ShiftSwapRepository repository;

    public ShiftSwapService(ShiftSwapRepository repository) {
        this.repository = repository;
    }

    public ShiftSwap findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<ShiftSwap> findAll() {
        return repository.findAll();
    }

    public ShiftSwap create(ShiftSwap shiftSwap) {
        return repository.save(shiftSwap);
    }

    public ShiftSwap update(ShiftSwap shiftSwap) {
        ShiftSwap existing = repository.findById(shiftSwap.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (shiftSwap.getSwapRequestingUser() != null) {
            existing.setSwapRequestingUser(shiftSwap.getSwapRequestingUser());
        }
        if (shiftSwap.getSwapSuggestingUsers() != null) {
            existing.setSwapSuggestingUsers(shiftSwap.getSwapSuggestingUsers());
        }
        if (shiftSwap.getRequestedShift() != null) {
            existing.setRequestedShift(shiftSwap.getRequestedShift());
        }
        if (shiftSwap.getSuggestedShift() != null) {
            existing.setSuggestedShift(shiftSwap.getSuggestedShift());
        }

        return repository.save(existing);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
