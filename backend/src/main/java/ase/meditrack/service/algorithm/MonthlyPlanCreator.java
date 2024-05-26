package ase.meditrack.service.algorithm;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.MonthlyPlanRepository;
import ase.meditrack.repository.ShiftRepository;
import ase.meditrack.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MonthlyPlanCreator {

    private final ShiftRepository shiftRepository;
    private final MonthlyPlanRepository monthlyPlanRepository;
    private final TeamRepository teamRepository;

    public MonthlyPlanCreator(ShiftRepository shiftRepository, MonthlyPlanRepository monthlyPlanRepository,
                              TeamRepository teamRepository) {
        this.shiftRepository = shiftRepository;
        this.monthlyPlanRepository = monthlyPlanRepository;
        this.teamRepository = teamRepository;
    }


    /**
     * Create a monthly plan for the given parameters.
     * Stores the shifts and monthly plan in the database and returns the created plan.
     *
     * @param month  the month for which to create the plan
     * @param year   the year for which to create the plan
     * @param teamId the team for which to create the plan
     * @return the created plan
     */
    @Transactional
    public MonthlyPlan createMonthlyPlan(int month, int year, UUID teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException("team not found"));
        List<ShiftType> shiftTypes = team.getShiftTypes();
        List<User> users = team.getUsers();

        // map to algorithm input
        AlgorithmMapper algorithmMapper = new AlgorithmMapper();

        AlgorithmInput input = algorithmMapper.mapToAlgorithmInput(
                month,
                year,
                users,
                shiftTypes,
                team.getRoles(),
                team.getHardConstraints(),
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
