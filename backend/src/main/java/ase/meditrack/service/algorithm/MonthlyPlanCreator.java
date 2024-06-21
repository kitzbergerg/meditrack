package ase.meditrack.service.algorithm;

import ase.meditrack.model.entity.Holiday;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.MonthlyWorkDetails;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.HolidayRepository;
import ase.meditrack.repository.MonthlyPlanRepository;
import ase.meditrack.repository.MonthlyWorkDetailsRepository;
import ase.meditrack.repository.ShiftRepository;
import ase.meditrack.service.MonthlyWorkDetailsService;
import ase.meditrack.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MonthlyPlanCreator {

    private final ShiftRepository shiftRepository;
    private final MonthlyPlanRepository monthlyPlanRepository;
    private final UserService userService;
    private final MonthlyWorkDetailsRepository monthlyWorkDetailsRepository;
    private final MonthlyWorkDetailsService monthlyWorkDetailsService;
    private final HolidayRepository holidayRepository;

    public MonthlyPlanCreator(ShiftRepository shiftRepository, MonthlyPlanRepository monthlyPlanRepository,
                              UserService userService, MonthlyWorkDetailsRepository monthlyWorkDetailsRepository,
                              MonthlyWorkDetailsService monthlyWorkDetailsService,
                              HolidayRepository holidayRepository) {
        this.shiftRepository = shiftRepository;
        this.monthlyPlanRepository = monthlyPlanRepository;
        this.userService = userService;
        this.monthlyWorkDetailsRepository = monthlyWorkDetailsRepository;
        this.monthlyWorkDetailsService = monthlyWorkDetailsService;
        this.holidayRepository = holidayRepository;
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
        Map<UUID, List<Holiday>> holidaysPerUser = users.stream().collect(Collectors.toMap(
                User::getId,
                u -> holidayRepository.findHolidaysForUserInCurrentMonth(u.getId(), YearMonth.of(year, month).atDay(1),
                        YearMonth.of(year, month).atEndOfMonth()),
                (existing, replacement) -> existing
        ));

        // map to algorithm input
        AlgorithmMapper algorithmMapper = new AlgorithmMapper();

        YearMonth yearMonth = YearMonth.of(year, month);
        YearMonth yearMonthBefore = yearMonth.minusMonths(1);
        // TODO #98: change to use roles; use
        //  team.getRoles().stream().mapToInt(Role::getMaxConsecutiveShifts).max()
        //  to find role with largest consec shifts
        int maxConsecShifts = team.getHardConstraints().getMaxConsecutiveShifts();
        int daysToDivisibleBy7 = yearMonth.atEndOfMonth().getDayOfMonth() % 7;
        LocalDate startDate = yearMonthBefore.atEndOfMonth().minusDays(Math.max(maxConsecShifts, daysToDivisibleBy7));
        List<Shift> prevMonthShifts = shiftRepository.findAllByTeamAndDateAfterAndDateBefore(
                team.getId(),
                startDate,
                yearMonthBefore.atEndOfMonth()
        );
        AlgorithmInput input = algorithmMapper.mapToAlgorithmInput(
                month,
                year,
                users,
                holidaysPerUser,
                shiftTypes,
                team.getRoles(),
                team,
                prevMonthShifts
        );

        AlgorithmOutput output = SchedulingSolver.solve(input)
                .orElseThrow(() -> new RuntimeException("unable to create plan"));

        MonthlyPlan monthlyPlan = monthlyPlanRepository.save(new MonthlyPlan(null, month, year, false,
                team, null, null));

        List<Shift> shifts = algorithmMapper.mapFromAlgorithmOutput(
                output,
                shiftTypes,
                users,
                monthlyPlan,
                month,
                year
        );

        List<MonthlyWorkDetails> monthlyWorkDetails = new ArrayList<>();
        for (User teamMember : users) {
            Float targetHours = monthlyWorkDetailsService.calculateTargetWorkingHours(teamMember, team, month, year);
            Float actualHours = monthlyWorkDetailsService.calculateActualWorkingHours(teamMember, shifts);
            int overtime = Math.round(actualHours - targetHours);

            MonthlyWorkDetails monthlyWorkDetail = new MonthlyWorkDetails(null,
                    month,
                    year,
                    targetHours,
                    actualHours,
                    overtime,
                    teamMember,
                    monthlyPlan
            );

            monthlyWorkDetails.add(monthlyWorkDetail);
        }

        List<MonthlyWorkDetails> savedDetails = monthlyWorkDetailsRepository.saveAll(monthlyWorkDetails);

        shifts = shiftRepository.saveAll(shifts);
        monthlyPlan.setShifts(shifts);
        monthlyPlan.setMonthlyWorkDetails(savedDetails);
        return monthlyPlan;
    }

}
