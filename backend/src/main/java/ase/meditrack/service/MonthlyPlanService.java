package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.repository.MonthlyPlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class MonthlyPlanService {
    private final MonthlyPlanRepository repository;

    public MonthlyPlanService(MonthlyPlanRepository repository) {
        this.repository = repository;
    }

    /**
     * Fetches all monthly plans from the database.
     *
     * @return List of all monthly plans
     */
    public List<MonthlyPlan> findAll() {
        return repository.findAll();
    }

    /**
     * Fetches a monthly plan by id from the database.
     *
     * @param id, the id of the monthly plan
     * @return the monthly plan
     */
    public MonthlyPlan findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Could not find monthly plan with id: " + id + "!"));
    }

    /**
     * Updates a monthly plan in the database.
     *
     * @param monthlyPlan, the monthly plan to update
     * @return the updated monthly plan
     */
    public MonthlyPlan update(MonthlyPlan monthlyPlan) {
        MonthlyPlan dbPlan = findById(monthlyPlan.getId());

        if (monthlyPlan.getMonth() != null) {
            dbPlan.setMonth(monthlyPlan.getMonth());
        }
        if (monthlyPlan.getYear() != null) {
            dbPlan.setYear(monthlyPlan.getYear());
        }
        if (monthlyPlan.getPublished() != null) {
            dbPlan.setPublished(monthlyPlan.getPublished());
        }
        if (monthlyPlan.getTeam() != null) {
            dbPlan.setTeam(monthlyPlan.getTeam());
        }
        if (monthlyPlan.getShifts() != null) {
            dbPlan.setShifts(monthlyPlan.getShifts());
        }

        return repository.save(dbPlan);
    }

    /**
     * Deletes a monthly plan from the database.
     *
     * @param id, the id of the monthly plan to delete
     */
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
