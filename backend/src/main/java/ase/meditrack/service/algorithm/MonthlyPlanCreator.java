package ase.meditrack.service.algorithm;

import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.MonthlyPlanRepository;
import ase.meditrack.repository.ShiftRepository;
import ase.meditrack.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
public class MonthlyPlanCreator {

    private final ShiftRepository shiftRepository;
    private final MonthlyPlanRepository monthlyPlanRepository;
    private final UserService userService;

    public MonthlyPlanCreator(ShiftRepository shiftRepository, MonthlyPlanRepository monthlyPlanRepository,
                              UserService userService) {
        this.shiftRepository = shiftRepository;
        this.monthlyPlanRepository = monthlyPlanRepository;
        this.userService = userService;
    }


    /**
     * Create a monthly plan for the given parameters.
     * Stores the shifts and monthly plan in the database and returns the created plan.
     *
     * @param month     the month for which to create the plan
     * @param year      the year for which to create the plan
     * @param principal principal that calls the rest endpoint
     * @return the created plan
     */
    @Transactional
    public MonthlyPlan createMonthlyPlan(int month, int year, Principal principal) {
        User user = userService.getPrincipalWithTeam(principal);
        Team team = user.getTeam();
        List<ShiftType> shiftTypes = team.getShiftTypes();
        List<User> users = userService.findByTeam(principal);
        users = users.stream().filter(u -> u.getId() != user.getId()).toList();

        // map to algorithm input
        AlgorithmMapper algorithmMapper = new AlgorithmMapper();

        AlgorithmInput input = algorithmMapper.mapToAlgorithmInput(
                month,
                year,
                users,
                shiftTypes,
                team.getRoles(),
                team
        );

        AlgorithmOutput output = SchedulingSolver.solve(input)
                .orElseThrow(() -> new RuntimeException("unable to create plan"));

        MonthlyPlan monthlyPlan = monthlyPlanRepository.save(new MonthlyPlan(null, month, year, false, team, null));

        List<Shift> shifts = algorithmMapper.mapFromAlgorithmOutput(
                output,
                shiftTypes,
                users,
                monthlyPlan,
                month,
                year
        );

        shifts = shiftRepository.saveAll(shifts);
        monthlyPlan.setShifts(shifts);
        return monthlyPlan;
    }

}
