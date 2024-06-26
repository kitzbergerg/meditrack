package ase.meditrack.service;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.entity.*;
import ase.meditrack.repository.*;
import ase.meditrack.util.DefaultTestCreator;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockBean(KeycloakConfig.class)
@MockBean(KeycloakConfig.KeycloakPostConstruct.class)
@MockBean(RealmResource.class)
@MockBean(MailService.class)
public class ShiftServiceTest {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private ShiftRepository repository;
    @Autowired
    private ShiftService service;
    @Autowired
    private UserRepository userRepository;
    private User user;
    @Autowired
    private DefaultTestCreator defaultTestCreator;
    @Autowired
    private TeamService teamService;
    private Team team;
    @Autowired
    private ShiftTypeRepository shiftTypeRepository;
    @Autowired
    private MonthlyPlanRepository monthlyPlanRepository;
    @Autowired
    private ShiftSwapRepository shiftSwapRepository;
    @Autowired
    private MonthlyWorkDetailsRepository monthlyWorkDetailsRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        team = defaultTestCreator.createDefaultTeam();
        Role role = defaultTestCreator.createDefaultRole(team);

        user = new User(
                UUID.fromString(USER_ID),
                role,
                1f,
                0,
                null,
                team,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        team.setUsers(List.of(user));
        Preferences preferences = new Preferences(null, List.of(), user);
        user.setPreferences(preferences);
        user = userRepository.save(user);
    }

    @Test
    void findAllReturnsAllShift() {
        List<Shift> resultList = service.findAll();

        assertAll(
                () -> assertNotNull(resultList),
                () -> assertEquals(0, resultList.size())
        );
    }


    @Test
    void findAllByCurrentMonthReturnsCorrectShifts() {
        // current month
        Shift shift = new Shift();
        shift.setDate(LocalDate.now().plusDays(2));
        List<User> userList = new ArrayList<>();
        userList.add(user);
        shift.setUsers(userList);
        repository.save(shift);

        // last month
        Shift shift2 = new Shift();
        shift2.setDate(LocalDate.now().minusDays(70));
        List<User> userList2 = new ArrayList<>();
        userList2.add(user);
        shift.setUsers(userList2);
        repository.save(shift2);

        // last month
        Shift shift3 = new Shift();
        shift3.setDate(LocalDate.now().minusDays(70));
        List<User> userList3 = new ArrayList<>();
        userList3.add(user);
        shift.setUsers(userList3);
        repository.save(shift3);

        Principal principal = new Principal() {
            @Override
            public String getName() {
                return USER_ID;
            }
        };

        List<Shift> shifts = service.findAllByCurrentMonth(principal);
        List<Shift> allShifts = service.findAll();

        assertAll(
                () -> assertNotNull(shifts),
                () -> assertEquals(1, shifts.size()),
                () -> assertEquals(3, allShifts.size())
        );
    }

    @Test
    void findByIdReturnsShift() {
        Shift shift = new Shift();
        repository.save(shift);
        Shift savedShift = repository.findById(shift.getId()).get();

        Shift result = service.findById(savedShift.getId());

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(shift.getId(), result.getId()),
                () -> assertEquals(shift, result)
        );
    }

    @Test
    void createShift() {
        Shift shift = new Shift();
        shift.setDate(LocalDate.now());

        List<User> users = new ArrayList<>();
        users.add(user);
        shift.setUsers(users);

        ShiftType shiftType = new ShiftType();
        shiftType.setName("Test ShiftType");
        shiftType.setColor("#FF0000");
        shiftType.setAbbreviation("TS");
        shiftType.setStartTime(LocalTime.of(8, 0, 0, 0));
        shiftType.setEndTime(LocalTime.of(16, 0, 0, 0));
        shiftType.setBreakStartTime(LocalTime.of(12, 0, 0, 0));
        shiftType.setBreakEndTime(LocalTime.of(12, 30, 0, 0));
        shiftTypeRepository.save(shiftType);
        shift.setShiftType(shiftType);

        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setTeam(team);
        monthlyPlan.setYear(2024);
        monthlyPlan.setMonth(6);
        monthlyPlan.setPublished(false);
        monthlyPlanRepository.save(monthlyPlan);

        MonthlyWorkDetails monthlyWorkDetails = new MonthlyWorkDetails();
        monthlyWorkDetails.setHoursActuallyWorked(3);
        monthlyWorkDetails.setMonthlyPlan(monthlyPlan);
        monthlyWorkDetails.setUser(user);
        monthlyWorkDetails.setOvertime(3);
        monthlyWorkDetails.setHoursShouldWork(120);
        monthlyWorkDetails.setYear(2024);
        monthlyWorkDetails.setMonth(6);
        monthlyWorkDetailsRepository.save(monthlyWorkDetails);
        List<MonthlyWorkDetails> monthlyWorkDetailsList = new ArrayList<>();
        monthlyWorkDetailsList.add(monthlyWorkDetails);
        monthlyPlan.setMonthlyWorkDetails(monthlyWorkDetailsList);
        monthlyPlanRepository.save(monthlyPlan);

        shift.setMonthlyPlan(monthlyPlan);
        Shift savedShift = service.create(shift);

        assertAll(
                () -> assertNotNull(savedShift),
                () -> assertEquals(shift.getId(), savedShift.getId())
        );
    }

    @Test
    void updateShift() {
        Shift shift = new Shift();
        repository.save(shift);
        Shift savedShift = repository.findById(shift.getId()).get();

        Shift updatedShift = new Shift();
        updatedShift.setId(savedShift.getId());
        updatedShift.setDate(LocalDate.now());

        List<User> users = new ArrayList<>();
        users.add(user);
        updatedShift.setUsers(users);

        ShiftType shiftType = new ShiftType();
        shiftType.setName("Test ShiftType");
        shiftType.setColor("#FF0000");
        shiftType.setAbbreviation("TS");
        shiftType.setStartTime(LocalTime.of(8, 0, 0, 0));
        shiftType.setEndTime(LocalTime.of(16, 0, 0, 0));
        shiftType.setBreakStartTime(LocalTime.of(12, 0, 0, 0));
        shiftType.setBreakEndTime(LocalTime.of(12, 30, 0, 0));
        shiftTypeRepository.save(shiftType);
        updatedShift.setShiftType(shiftType);

        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setTeam(team);
        monthlyPlan.setYear(2024);
        monthlyPlan.setMonth(6);
        monthlyPlan.setPublished(false);
        monthlyPlanRepository.save(monthlyPlan);

        MonthlyWorkDetails monthlyWorkDetails = new MonthlyWorkDetails();
        monthlyWorkDetails.setHoursActuallyWorked(3);
        monthlyWorkDetails.setMonthlyPlan(monthlyPlan);
        monthlyWorkDetails.setUser(user);
        monthlyWorkDetails.setOvertime(3);
        monthlyWorkDetails.setHoursShouldWork(120);
        monthlyWorkDetails.setYear(2024);
        monthlyWorkDetails.setMonth(6);
        monthlyWorkDetailsRepository.save(monthlyWorkDetails);
        List<MonthlyWorkDetails> monthlyWorkDetailsList = new ArrayList<>();
        monthlyPlan.setMonthlyWorkDetails(monthlyWorkDetailsList);
        monthlyPlanRepository.save(monthlyPlan);

        updatedShift.setMonthlyPlan(monthlyPlan);
        repository.save(updatedShift);

        Shift result = service.update(updatedShift);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(updatedShift.getId(), result.getId())
        );
    }

    @Test
    void deleteShift() {
        Shift shift = new Shift();
        shift.setDate(LocalDate.now());

        ShiftType shiftType = new ShiftType();
        shiftType.setName("Test ShiftType");
        shiftType.setColor("#FF0000");
        shiftType.setAbbreviation("TS");
        shiftType.setStartTime(LocalTime.of(8, 0, 0, 0));
        shiftType.setEndTime(LocalTime.of(16, 0, 0, 0));
        shiftType.setBreakStartTime(LocalTime.of(12, 0, 0, 0));
        shiftType.setBreakEndTime(LocalTime.of(12, 30, 0, 0));
        shiftTypeRepository.save(shiftType);
        shift.setShiftType(shiftType);

        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setTeam(team);
        monthlyPlan.setYear(2024);
        monthlyPlan.setMonth(6);
        monthlyPlan.setPublished(false);
        monthlyPlanRepository.save(monthlyPlan);

        MonthlyWorkDetails monthlyWorkDetails = new MonthlyWorkDetails();
        monthlyWorkDetails.setHoursActuallyWorked(3);
        monthlyWorkDetails.setMonthlyPlan(monthlyPlan);
        monthlyWorkDetails.setUser(user);
        monthlyWorkDetails.setOvertime(3);
        monthlyWorkDetails.setHoursShouldWork(120);
        monthlyWorkDetails.setYear(2024);
        monthlyWorkDetails.setMonth(6);
        monthlyWorkDetailsRepository.save(monthlyWorkDetails);
        List<MonthlyWorkDetails> monthlyWorkDetailsList = new ArrayList<>();
        monthlyWorkDetailsList.add(monthlyWorkDetails);
        monthlyPlan.setMonthlyWorkDetails(monthlyWorkDetailsList);
        monthlyPlanRepository.save(monthlyPlan);

        shift.setMonthlyPlan(monthlyPlan);

        List<User> users = new ArrayList<>();
        users.add(user);
        shift.setUsers(users);

        repository.save(shift);

        List<Shift> resultList = service.findAll();
        assertEquals(1, resultList.size());

        service.delete(shift.getId());

        resultList = service.findAll();
        assertEquals(0, resultList.size());
    }
}
