package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.MonthlyPlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MonthlyPlanService {
    private final MonthlyPlanRepository repository;
    private final UserService userService;
    private final RealmResource meditrackRealm;

    public MonthlyPlanService(MonthlyPlanRepository repository, UserService userService, RealmResource meditrackRealm) {
        this.repository = repository;
        this.userService = userService;
        this.meditrackRealm = meditrackRealm;
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
     * @param id the id of the monthly plan
     * @return the monthly plan
     */
    public MonthlyPlan findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Could not find monthly plan with id: " + id + "!"));
    }

    public MonthlyPlan getMonthlyPlan(int month, int year, Principal principal) {
        User user = userService.getPrincipalWithTeam(principal);
        Team team = user.getTeam();
        log.info("Fetching monthly plan for user {}", team);
        MonthlyPlan plan = repository.findMonthlyPlanByTeamAndMonthAndYear(team, month, year);
        if (plan == null) {
            throw new NotFoundException("Could not find monthly plan for month: " + month + "!");
        }
        List<Shift> shifts = plan.getShifts();
        for (Shift shift : shifts) {
            shift.setUsers(shift.getUsers().stream()
                    .peek(u -> u.setUserRepresentation(meditrackRealm.users().get(u.getId().toString()).toRepresentation()))
                    .toList());
        }
        plan.setShifts(shifts);
        log.info("Shifts of monthly plan: {}", shifts);
        return plan;
    }

    /**
     * Updates a monthly plan in the database.
     *
     * @param monthlyPlan the monthly plan to update
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
     * @param id the id of the monthly plan to delete
     */
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
