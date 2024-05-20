package ase.meditrack.config;

import ase.meditrack.model.dto.UserDto;
import ase.meditrack.model.entity.Holiday;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.mapper.UserMapper;
import ase.meditrack.service.HardConstraintsService;
import ase.meditrack.service.HolidayService;
import ase.meditrack.service.MonthlyPlanService;
import ase.meditrack.service.PreferencesService;
import ase.meditrack.service.RoleService;
import ase.meditrack.service.ShiftService;
import ase.meditrack.service.ShiftSwapService;
import ase.meditrack.service.TeamService;
import ase.meditrack.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Profile("generate-data")
@Component
@Slf4j
public class DataGeneratorBean {
    private final UserService userService;
    private final TeamService teamService;
    private final ShiftService shiftService;
    private final ShiftSwapService shiftSwapService;
    private final RoleService roleService;
    private final PreferencesService preferencesService;
    private final MonthlyPlanService monthlyPlanService;
    private final HolidayService holidayService;
    private final HardConstraintsService hardConstraintsService;
    private final UserMapper userMapper;

    public DataGeneratorBean (UserService userService, TeamService teamService, ShiftService shiftService,
                              ShiftSwapService shiftSwapService, RoleService roleService,
                              PreferencesService preferencesService, MonthlyPlanService monthlyPlanService,
                              HolidayService holidayService, HardConstraintsService hardConstraintsService,
                              UserMapper userMapper) {
        this.userService = userService;
        this.teamService = teamService;
        this.shiftService = shiftService;
        this.shiftSwapService = shiftSwapService;
        this.roleService = roleService;
        this.preferencesService = preferencesService;
        this.monthlyPlanService = monthlyPlanService;
        this.holidayService = holidayService;
        this.hardConstraintsService = hardConstraintsService;
        this.userMapper = userMapper;
    }

    private final Random randomNumGenerator = new Random();

    private static final List<String> ROLES = List.of("Nurse", "Doctor", "Trainee");
    private static final Integer NUM_TEAMS = 5;
    private static final Integer NUM_USERS = 10;
    private static final Integer NUM_HOLIDAYS = 5;

    private List<Role> roles;
    private List<Team> teams;
    private List<User> users;

    @PostConstruct
    private void generateData() {
        try {
            log.info("Generating data...");
            createRoles();
            createTeams();
            createUsers();
            createHolidays();
            createPreferences();
            createShifts();
            createMonthlyPlan();
            createHardConstraints();
            createShiftSwap();
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
            role.setUsers(null);
            roles.add(roleService.create(role));
        }
    }

    private void createTeams() {
        log.info("Generating {} teams...", NUM_TEAMS);
        teams = new ArrayList<>();
        for (int i = 0; i < NUM_TEAMS; i++) {
            Team team = new Team();
            team.setName("Team" + i);
            team.setWorkingHours(40);
            team.setUsers(null);
            team.setHardConstraints(null);
            team.setMonthlyPlans(null);
            team.setShiftTypes(null);
            teams.add(teamService.create(team));
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

            //set roles
            userEntity.setRole(roles.get(i%roles.size()));

            //set teams
            userEntity.setTeam(teams.get(i%teams.size()));

            users.add(userService.create(userEntity));
        }
    }

    private void createHolidays() {
        log.info("Generating holidays for {} users...", NUM_USERS);
        for (int i = 0; i < NUM_USERS; i++) {
            User user = users.get(i);
            for (int j = 0; j < NUM_HOLIDAYS; j++) {
                Holiday holiday = new Holiday();
                holiday.setStartDate(generateValidRandomDates());
                holiday.setEndDate(holiday.getStartDate().plusDays(1));
                holiday.setIsApproved(false);
                holiday.setUser(user);
                holidayService.create(holiday);
            }
        }
    }

    private LocalDate generateValidRandomDates() {
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

    private void createPreferences() {

    }

    private void createShifts() {

    }

    private void createMonthlyPlan() {

    }

    private void createHardConstraints() {

    }

    private void createShiftOffShiftIdList() {

    }

    private void createShiftSwap() {

    }
}
