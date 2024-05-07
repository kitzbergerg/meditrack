package ase.meditrack.service;

import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.repository.MonthlyPlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class MonthlyPlanService {
    private final MonthlyPlanRepository repository;

    public MonthlyPlanService(MonthlyPlanRepository repository) {
        this.repository = repository;
    }

    public MonthlyPlan findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<MonthlyPlan> findAll() {
        return repository.findAll();
    }

    public MonthlyPlan create(MonthlyPlan monthlyPlan) {
        return repository.save(monthlyPlan);
    }

    public MonthlyPlan update(MonthlyPlan monthlyPlan) {
        MonthlyPlan existing = repository.findById(monthlyPlan.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (monthlyPlan.getMonth() != null) {
            existing.setMonth(monthlyPlan.getMonth());
        }
        if (monthlyPlan.getYear() != null) {
            existing.setYear(monthlyPlan.getYear());
        }
        if (monthlyPlan.getPublished() != null) {
            existing.setPublished(monthlyPlan.getPublished());
        }
        if (monthlyPlan.getTeam() != null) {
            existing.setTeam(monthlyPlan.getTeam());
        }
        if (monthlyPlan.getShifts() != null) {
            existing.setShifts(monthlyPlan.getShifts());
        }

        return repository.save(existing);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
