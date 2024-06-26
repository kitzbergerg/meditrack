package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.MonthlyPlanRepository;
import ase.meditrack.repository.ShiftRepository;
import ase.meditrack.repository.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class MonthlyPlanService {
    private final ShiftRepository shiftRepository;
    private final TeamRepository teamRepository;
    private final MonthlyPlanRepository repository;
    private final UserService userService;
    private final RealmResource meditrackRealm;
    private final MailService mailService;

    public MonthlyPlanService(MonthlyPlanRepository repository, UserService userService, RealmResource meditrackRealm,
                              TeamRepository teamRepository, MailService mailService,
                              ShiftRepository shiftRepository) {
        this.repository = repository;
        this.userService = userService;
        this.meditrackRealm = meditrackRealm;
        this.teamRepository = teamRepository;
        this.shiftRepository = shiftRepository;
        this.mailService = mailService;
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

    /**
     * Fetches a monthly plan for a given month and year.
     *
     * @param month     of the plan
     * @param year      of the plan
     * @param principal that fetches the plan
     * @return the monthly plan
     */
    public MonthlyPlan getMonthlyPlan(int month, int year, Principal principal) {
        User user = userService.findById(UUID.fromString(principal.getName()));
        Team team = user.getTeam();
        //log.info("Fetching monthly plan for team {}", team.getId());
        MonthlyPlan plan = repository.findMonthlyPlanByTeamAndMonthAndYear(team, month, year);
        if (plan == null) {
            throw new NotFoundException("Could not find monthly plan for month: " + month + "!");
        }

        List<Shift> shifts = plan.getShifts();
        for (Shift shift : shifts) {
            shift.setUsers(shift.getUsers().stream()
                    .peek(u -> u.setUserRepresentation(meditrackRealm.users().get(u.getId().toString())
                            .toRepresentation()))
                    .toList());
        }
        plan.setShifts(shifts);
        log.info("Shifts for monthly plan: {}", plan.getId());
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
     * Publishes a monthly plan.
     *
     * @param principal that publishes the plan
     * @param id        of the monthly plan to publish
     * @param shouldSendMail if a mail should be sent to the users of the team
     */
    @Transactional
    public void publish(UUID id, Principal principal, Boolean shouldSendMail) {
        UUID userId = UUID.fromString(principal.getName());
        MonthlyPlan plan = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("MonthlyPlan not found with id " + id));
        plan.setPublished(true);
        plan.getTeam().getUsers().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Can not publish plan of other team!"));

        repository.save(plan);

        if (shouldSendMail != null && shouldSendMail) {
            List<String> mails = userService.findByTeam(principal).stream()
                    .map(u -> u.getUserRepresentation().getEmail())
                    .toList();
            new Thread(() -> mailService.sendSimpleMessages(mails, "Monthly Plan published!",
                    generateMonthlyPlanPublishedMessage(plan))).start();
        }
    }

    /**
     * Deletes a monthly plan from the database.
     *
     * @param id the id of the monthly plan to delete
     */
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    /**
     * Checks if a user is in a team.
     *
     * @param userId        the id of the user
     * @param monthlyPlanId the id of the monthly plan
     * @return true if the user is in the team, false otherwise
     */
    public boolean isUserInTeam(UUID userId, UUID monthlyPlanId) {
        if (userId == null || monthlyPlanId == null) {
            return false;
        }
        User user = userService.findById(userId);
        if (user.getTeam() == null) {
            return false;
        }
        Optional<MonthlyPlan> plan = repository.findById(monthlyPlanId);
        return plan.filter(monthlyPlan -> userService.findById(userId).getTeam().getId()
                .equals(monthlyPlan.getTeam().getId())).isPresent();
    }

    /**
     * Checks if a shift is from the users team.
     *
     * @param userId  the id of the user
     * @param shiftId the id of the shift
     * @return true if the shift is from the users team, false otherwise
     */
    public boolean isShiftFromTeam(UUID userId, UUID shiftId) {
        if (userId == null || shiftId == null) {
            return false;
        }
        User user = userService.findById(userId);
        if (user.getTeam() == null) {
            return false;
        }
        Optional<Shift> shift = shiftRepository.findById(shiftId);
        return shift.filter(s -> userService.findById(userId).getTeam().getId()
                .equals(s.getMonthlyPlan().getTeam().getId())).isPresent();
    }

    /**
     * Checks if a monthly plan is published.
     *
     * @param month     of the plan
     * @param year      of the plan
     * @param principal that fetches the plan
     * @return true if the plan is published, false otherwise
     */
    public boolean isPublished(Month month, Year year, Principal principal) {
        User user = userService.getPrincipalWithTeam(principal);
        Team team = user.getTeam();
        MonthlyPlan plan = repository.findMonthlyPlanByTeamAndMonthAndYear(team, month.getValue(), year.getValue());
        return plan == null || plan.getPublished();
    }

    private String generateMonthlyPlanPublishedMessage(MonthlyPlan plan) {
        return "The monthly plan for " + plan.getMonth() + "/" + plan.getYear() + " has been published!\n\n"
                + "You can now log in to MediTrack and see your schedule for the month.\n\n"
                + "If you have any questions or need help, please contact your team leader.\n\n"
                + "Best regards,\n"
                + "Your MediTrack Team";
    }
}
