package ase.meditrack.config;

import ase.meditrack.model.dto.UserDto;
import ase.meditrack.model.entity.HardConstraints;
import ase.meditrack.model.entity.Holiday;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.Preferences;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftOffShiftIdList;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.mapper.UserMapper;
import ase.meditrack.repository.HardConstraintsRepository;
import ase.meditrack.repository.HolidayRepository;
import ase.meditrack.repository.MonthlyPlanRepository;
import ase.meditrack.repository.PreferencesRepository;
import ase.meditrack.repository.RoleRepository;
import ase.meditrack.repository.ShiftOffShiftIdListRepository;
import ase.meditrack.repository.ShiftRepository;
import ase.meditrack.repository.ShiftSwapRepository;
import ase.meditrack.repository.ShiftTypeRepository;
import ase.meditrack.repository.TeamRepository;
import ase.meditrack.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Profile("generate-data")
@Component
@Slf4j
public class DataGeneratorBean {
    private final UserService userService;
    private final TeamRepository teamRepository;
    private final ShiftRepository shiftRepository;
    private final ShiftSwapRepository shiftSwapRepository;
    private final RoleRepository roleRepository;
    private final PreferencesRepository preferencesRepository;
    private final MonthlyPlanRepository monthlyPlanRepository;
    private final HolidayRepository holidayRepository;
    private final HardConstraintsRepository hardConstraintsRepository;
    private final ShiftTypeRepository shiftTypeRepository;
    private final UserMapper userMapper;
    private final ShiftOffShiftIdListRepository shiftOffShiftIdListRepository;

    public DataGeneratorBean (UserService userService, TeamRepository teamRepository,
                              ShiftRepository shiftRepository, ShiftSwapRepository shiftSwapRepository,
                              RoleRepository roleRepository, PreferencesRepository preferencesRepository,
                              MonthlyPlanRepository monthlyPlanRepository, HolidayRepository holidayRepository,
                              HardConstraintsRepository hardConstraintsRepository, UserMapper userMapper,
                              ShiftTypeRepository shiftTypeRepository, ShiftOffShiftIdListRepository shiftOffShiftIdListRepository) {
        this.userService = userService;
        this.teamRepository = teamRepository;
        this.shiftRepository = shiftRepository;
        this.shiftSwapRepository = shiftSwapRepository;
        this.roleRepository = roleRepository;
        this.preferencesRepository = preferencesRepository;
        this.monthlyPlanRepository = monthlyPlanRepository;
        this.holidayRepository = holidayRepository;
        this.hardConstraintsRepository = hardConstraintsRepository;
        this.shiftTypeRepository = shiftTypeRepository;
        this.userMapper = userMapper;
        this.shiftOffShiftIdListRepository = shiftOffShiftIdListRepository;
    }

    private final Random randomNumGenerator = new Random();

    private static final List<String> ROLES = List.of("Nurse", "Doctor", "Trainee");
    private static final Integer NUM_TEAMS = 5;
    private static final Integer NUM_USERS = 10;
    private static final Integer NUM_HOLIDAYS = 2;
    private static final Integer NUM_PREFERENCES = 1;
    private static final Integer NUM_MONTHLY_PLANS = 3;

    private List<Role> roles;
    private List<Team> teams;
    private List<User> users;
    private List<Shift> shifts;
    private List<ShiftType> shiftTypes;
    private List<MonthlyPlan> monthlyPlans;

    @PostConstruct
    private void generateData() {
        try {
            log.info("Generating data...");
            createRoles();
            createUsers();
            createTeams();
            createHolidays();
            createShiftTypes();
            createShifts();
            createMonthlyPlan();
            createShiftSwap();
            createHardConstraints();
            createPreferences();
            log.info("Data generation complete!");
        } catch (Exception e) {
            log.error("Error generating data: {}", e.getMessage());
        }
    }

    private void createRoles() {
        log.info("Creating roles...");
        roles = new ArrayList<>();
        for (String roleName : ROLES) {
            Role role = new Role();
            role.setName(roleName);
            roles.add(roleRepository.save(role));
        }
    }

    private void createUsers() {
        log.info("Generating {} users...", NUM_USERS);
        users = new ArrayList<>();
        for (int i = 0; i < NUM_USERS; i++) {
            UserDto user = new UserDto(
                    null,
                    "User" + i,
                    "s€cr€tPa$$w0rd",
                    "user" + i + "@meditrack.com",
                    "UserFirstname" + i,
                    "UserLastname" + i,
                    List.of("admin"),
                    null,
                    100f-i,
                    0,
                    List.of("specialSkill1", "specialSkill2"),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                    );
            User userEntity = userMapper.fromDto(user);
            users.add(userService.create(userEntity));
        }
    }

    private void createTeams() {
        log.info("Generating {} teams...", NUM_TEAMS);
        teams = new ArrayList<>();
        for (int i = 0; i < NUM_TEAMS; i++) {
            Team team = new Team();
            team.setName("Team" + i);
            team.setWorkingHours(40);
            teams.add(teamRepository.save(team));
        }
    }

    private void createHolidays() {
        log.info("Generating {} holidays for every user...", NUM_HOLIDAYS);
        for (int i = 0; i < NUM_USERS; i++) {
            for (int j = 0; j < NUM_HOLIDAYS; j++) {
                Holiday holiday = new Holiday();
                holiday.setStartDate(generateValidRandomDate());
                holiday.setEndDate(holiday.getStartDate().plusDays(1));
                holiday.setIsApproved(false);
                holidayRepository.save(holiday);
            }
        }
    }

    private LocalDate generateValidRandomDate() {
        int month = 0;
        int day = 0;
        while (month < LocalDate.now().getMonthValue()) {
            month = randomNumGenerator.nextInt(12) + 1;
        }
        while (day < LocalDate.now().getDayOfMonth()) {
            day = randomNumGenerator.nextInt(28) + 1;
        }
        return LocalDate.of(2024, month, day);
    }

    private void createShiftTypes() {
        log.info("Generating shift types...");
        shiftTypes = new ArrayList<>();
        ShiftType nightShift = new ShiftType();
        nightShift.setName("Night Shift");
        nightShift.setStartTime(LocalTime.of(22,0));
        nightShift.setEndTime(LocalTime.of(6,0));
        shiftTypes.add(shiftTypeRepository.save(nightShift));
        ShiftType earlyDayShift = new ShiftType();
        nightShift.setName("Early Day Shift");
        nightShift.setStartTime(LocalTime.of(6,0));
        nightShift.setEndTime(LocalTime.of(14,0));
        shiftTypes.add(shiftTypeRepository.save(earlyDayShift));
        ShiftType lateDayShift = new ShiftType();
        nightShift.setName("Late Day Shift");
        nightShift.setStartTime(LocalTime.of(14,0));
        nightShift.setEndTime(LocalTime.of(22,0));
        shiftTypes.add(shiftTypeRepository.save(lateDayShift));
    }

    private void createShifts() {
        log.info("Generating shifts...");
        shifts = new ArrayList<>();
        for (ShiftType s : shiftTypes) {
            for (int i = 0; i <= 30; i++) {
                Shift shift = new Shift();
                shift.setDate(LocalDate.now().plusDays(i));
                shift.setShiftType(s);
                shifts.add(shiftRepository.save(shift));
            }
        }
    }

    private void createMonthlyPlan() {
        log.info("Generating the next {} monthly plans for every team...", NUM_MONTHLY_PLANS);
        monthlyPlans = new ArrayList<>();
        for (Team team : teams) {
            for (int i = 0; i < NUM_MONTHLY_PLANS; i++) {
                MonthlyPlan monthlyPlan = new MonthlyPlan();
                monthlyPlan.setMonth(LocalDate.now().getMonthValue() + i);
                monthlyPlan.setYear(LocalDate.now().getYear());
                monthlyPlan.setPublished(false);
                monthlyPlan.setTeam(team);
                monthlyPlan.setShifts(shifts);
                monthlyPlans.add(monthlyPlanRepository.save(monthlyPlan));
            }
        }
    }

    private void createHardConstraints() {
        log.info("Generating hard constraints for every team...");
        for (Team team : teams) {
            HardConstraints hardConstraints = new HardConstraints();
            hardConstraints.setId(team.getId());
            hardConstraints.setDaytimeRequiredRoles(Map.of(roles.get(0), 2, roles.get(1), 1));
            hardConstraints.setNighttimeRequiredRoles(Map.of(roles.get(0), 1, roles.get(1), 1));
            hardConstraints.setDaytimeRequiredPeople(5);
            hardConstraints.setNighttimeRequiredPeople(3);
            hardConstraints.setAllowedFlextimeTotal(10);
            hardConstraints.setAllowedFlextimePerMonth(5);
            hardConstraints.setShiftOffShift(Map.of(createShiftOffShiftIdList(),
                    shifts.get(randomNumGenerator.nextInt(shifts.size())).getId()));
            hardConstraints.setTeam(team);
            hardConstraintsRepository.save(hardConstraints);
        }
    }

    private ShiftOffShiftIdList createShiftOffShiftIdList() {
        log.info("Generating shift off shift id list...");
        ShiftOffShiftIdList shiftOffShiftIdList = new ShiftOffShiftIdList();
        shiftOffShiftIdList.setShiftOffShiftIdList(List.of());
        return shiftOffShiftIdListRepository.save(shiftOffShiftIdList);
    }

    private void createPreferences() {
        log.info("Generating preferences for every user...");
        for (User user : users) {
            Preferences preferences = new Preferences();
            preferences.setId(user.getId());
            preferences.setOffDays(List.of(generateValidRandomDate(), generateValidRandomDate()));
            preferences.setUser(user);
            preferencesRepository.save(preferences);
        }
    }

    private void createShiftSwap() {
        //tbd
    }
}
