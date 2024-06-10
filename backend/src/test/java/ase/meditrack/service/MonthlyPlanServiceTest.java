package ase.meditrack.service;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.UserRepository;
import ase.meditrack.repository.MonthlyPlanRepository;
import ase.meditrack.repository.ShiftRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockBean(KeycloakConfig.class)
@MockBean(KeycloakConfig.KeycloakPostConstruct.class)
@MockBean(RealmResource.class)
public class MonthlyPlanServiceTest {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private MonthlyPlanRepository repository;
    @Autowired
    private MonthlyPlanService service;
    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;
    @Autowired
    private TeamService teamService;
    private Team team;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = userRepository.save(new User(
                UUID.fromString(USER_ID),
                null,
                1f,
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        ));
        team = teamService.create(
                new Team(null, "test team", 40, null, null, null, null, null),
                () -> USER_ID
        );
    }

    @Test
    void findAllReturnsAllMonthlyPlan() {
        List<MonthlyPlan> resultList = service.findAll();

        assertAll(
                () -> assertNotNull(resultList),
                () -> assertEquals(0, resultList.size())
        );
    }

    @Test
    void findByIdReturnsMonthlyPlan() {
        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setTeam(team);
        monthlyPlan.setYear(2024);
        monthlyPlan.setMonth(6);
        monthlyPlan.setPublished(false);
        repository.save(monthlyPlan);
        MonthlyPlan savedMonthlyPlan = repository.findById(monthlyPlan.getId()).get();

        MonthlyPlan result = service.findById(savedMonthlyPlan.getId());

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(monthlyPlan.getId(), result.getId()),
                () -> assertEquals(monthlyPlan, result)
        );
    }

    @Test
    void getMonthlyPlan() {
        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setTeam(team);
        monthlyPlan.setYear(2024);
        monthlyPlan.setMonth(6);
        monthlyPlan.setPublished(false);
        Shift shift = new Shift();
        shift.setDate(LocalDate.now().plusDays(2));
        shiftRepository.save(shift);
        Shift shift2 = new Shift();
        shift.setDate(LocalDate.now().plusDays(3));
        shiftRepository.save(shift2);
        List<Shift> shifts = new ArrayList<>();
        shifts.add(shift);
        shifts.add(shift2);
        monthlyPlan.setShifts(shifts);
        repository.save(monthlyPlan);

        Principal principal = new Principal() {
            @Override
            public String getName() {
                return USER_ID;
            }
        };
        MonthlyPlan savedMonthlyPlan = service.getMonthlyPlan(6, 2024, principal);

        assertAll(
                () -> assertNotNull(savedMonthlyPlan),
                () -> assertEquals(monthlyPlan.getId(), savedMonthlyPlan.getId()),
                () -> assertEquals(monthlyPlan.getMonth(), savedMonthlyPlan.getMonth())
        );
    }

    @Test
    void updateMonthlyPlan() {
        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setTeam(team);
        monthlyPlan.setYear(2024);
        monthlyPlan.setMonth(6);
        monthlyPlan.setPublished(false);
        repository.save(monthlyPlan);

        MonthlyPlan updatedMonthlyPlan = new MonthlyPlan();
        updatedMonthlyPlan.setTeam(team);
        updatedMonthlyPlan.setYear(2024);
        updatedMonthlyPlan.setMonth(7);
        updatedMonthlyPlan.setPublished(false);

        Shift shift = new Shift();
        shift.setDate(LocalDate.now().plusDays(2));
        List<User> userList = new ArrayList<>();
        userList.add(user);
        shift.setUsers(userList);
        shiftRepository.save(shift);

        Shift shift2 = new Shift();
        shift2.setDate(LocalDate.now().minusDays(70));
        List<User> userList2 = new ArrayList<>();
        userList2.add(user);
        shift.setUsers(userList2);
        shiftRepository.save(shift2);

        List<Shift> shifts = new ArrayList<>();
        shifts.add(shift);
        shifts.add(shift2);

        updatedMonthlyPlan.setShifts(shifts);
        repository.save(updatedMonthlyPlan);

        MonthlyPlan result = service.update(updatedMonthlyPlan);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(updatedMonthlyPlan.getId(), result.getId()),
                () -> assertEquals(updatedMonthlyPlan.getMonth(), result.getMonth())
        );
    }

    @Test
    void deleteMonthlyPlan() {
        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setTeam(team);
        monthlyPlan.setYear(2024);
        monthlyPlan.setMonth(6);
        monthlyPlan.setPublished(false);
        repository.save(monthlyPlan);

        List<MonthlyPlan> resultList = service.findAll();
        assertEquals(1, resultList.size());

        service.delete(monthlyPlan.getId());

        resultList = service.findAll();
        assertEquals(0, resultList.size());
    }
}
