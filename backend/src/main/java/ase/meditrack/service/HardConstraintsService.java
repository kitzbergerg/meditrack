package ase.meditrack.service;

import ase.meditrack.model.entity.HardConstraints;
import ase.meditrack.repository.HardConstraintsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class HardConstraintsService {
    private final HardConstraintsRepository repository;

    public HardConstraintsService(HardConstraintsRepository repository) {
        this.repository = repository;
    }

    public HardConstraints findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<HardConstraints> findAll() {
        return repository.findAll();
    }

    public HardConstraints create(HardConstraints hardConstraints) {
        return repository.save(hardConstraints);
    }

    public HardConstraints update(HardConstraints hardConstraints) {
        HardConstraints existing = repository.findById(hardConstraints.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (hardConstraints.getShiftOffShift() != null) {
            existing.setShiftOffShift(hardConstraints.getShiftOffShift());
        }
        if (hardConstraints.getDaytimeRequiredRoles() != null) {
            existing.setDaytimeRequiredRoles(hardConstraints.getDaytimeRequiredRoles());
        }
        if (hardConstraints.getNighttimeRequiredRoles() != null) {
            existing.setNighttimeRequiredRoles(hardConstraints.getNighttimeRequiredRoles());
        }
        if (hardConstraints.getDaytimeRequiredPeople() != null) {
            existing.setDaytimeRequiredPeople(hardConstraints.getDaytimeRequiredPeople());
        }
        if (hardConstraints.getNighttimeRequiredPeople() != null) {
            existing.setNighttimeRequiredPeople(hardConstraints.getNighttimeRequiredPeople());
        }
        if (hardConstraints.getAllowedFlextimeTotal() != null) {
            existing.setAllowedFlextimeTotal(hardConstraints.getAllowedFlextimeTotal());
        }
        if (hardConstraints.getAllowedFlextimePerMonth() != null) {
            existing.setAllowedFlextimePerMonth(hardConstraints.getAllowedFlextimePerMonth());
        }

        return repository.save(existing);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
