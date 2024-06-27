package ase.meditrack.service;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.Preferences;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.MonthlyPlanRepository;
import ase.meditrack.repository.RoleRepository;
import ase.meditrack.repository.ShiftRepository;
import ase.meditrack.repository.UserRepository;
import ase.meditrack.util.DefaultTestCreator;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.security.Principal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockBean(KeycloakConfig.class)
@MockBean(MailService.class)
@MockBean(KeycloakConfig.KeycloakPostConstruct.class)
@MockBean(RealmResource.class)
public class MonthlyPlanServiceTest {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";
    private User user;
    private Team team;

    @Autowired
    private MonthlyPlanRepository repository;
    @Autowired
    private DefaultTestCreator defaultTestCreator;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private UserRepository userRepository;
    @MockBean
    private UserService userService;

    @Autowired
    private TeamService teamService;
    @MockBean
    RealmResource realmResource;
    @Mock
    private RealmResource meditrackRealm;
    @Mock
    private UsersResource usersResource;
    @Autowired
    private MonthlyPlanService service;

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
    void publishMonthlyPlan() {
        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setTeam(team);
        monthlyPlan.setYear(2024);
        monthlyPlan.setMonth(6);
        monthlyPlan.setPublished(false);
        repository.save(monthlyPlan);

        service.publish(monthlyPlan.getId(), () -> USER_ID, false);

        MonthlyPlan savedMonthlyPlan = repository.findById(monthlyPlan.getId()).get();

        MonthlyPlan result = service.findById(savedMonthlyPlan.getId());

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(monthlyPlan.getId(), result.getId()),
                () -> assertTrue(monthlyPlan.getPublished())
        );
    }

    @Test
    void isPublishedMonthlyPlan() {
        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setTeam(team);
        monthlyPlan.setYear(2024);
        monthlyPlan.setMonth(6);
        monthlyPlan.setPublished(false);
        repository.save(monthlyPlan);

        when(userService.getPrincipalWithTeam(any())).thenReturn(user);

        assertFalse(service.isPublished(Month.JUNE, Year.of(2024), () -> USER_ID));

        service.publish(monthlyPlan.getId(), () -> USER_ID, false);

        assertTrue(service.isPublished(Month.JUNE, Year.of(2024), () -> USER_ID));
    }

    @Test
    void isUserInTeamOfMonthlyPlan() {
        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setTeam(new Team());
        monthlyPlan.setYear(2024);
        monthlyPlan.setMonth(6);
        monthlyPlan.setPublished(false);
        repository.save(monthlyPlan);
        when(userService.findById(any())).thenReturn(user);

        assertFalse(service.isUserInTeam(user.getId(), monthlyPlan.getId()));

        monthlyPlan.setTeam(team);

        assertTrue(service.isUserInTeam(user.getId(), monthlyPlan.getId()));
    }

    @Test
    void isShiftFromTeam() {
        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setTeam(team);
        monthlyPlan.setYear(2024);
        monthlyPlan.setMonth(6);
        monthlyPlan.setPublished(false);
        Shift shift = new Shift();
        shift.setUsers(List.of(user));
        shift.setMonthlyPlan(monthlyPlan);
        shift.setDate(LocalDate.now().plusDays(2));
        shift = shiftRepository.save(shift);
        List<Shift> shifts = new ArrayList<>();
        shifts.add(shift);
        monthlyPlan.setShifts(shifts);
        repository.save(monthlyPlan);

        when(userService.findById(any())).thenReturn(user);

        assertTrue(service.isShiftFromTeam(user.getId(), shift.getId()));
    }

    @Test
    void getMonthlyPlanNotFound() {
        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setTeam(team);
        monthlyPlan.setYear(2024);
        monthlyPlan.setMonth(6);
        monthlyPlan.setPublished(false);

        repository.save(monthlyPlan);

        when(userService.findById(any())).thenReturn(user);
        Principal principal = () -> USER_ID;

        assertThrows(NotFoundException.class, () -> service.getMonthlyPlan(7, 2024, principal));
    }

    @Test
    void getMonthlyPlan() {
        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setTeam(team);
        monthlyPlan.setYear(2024);
        monthlyPlan.setMonth(6);
        monthlyPlan.setPublished(false);

        Shift shift1 = new Shift();
        shift1.setUsers(List.of(user));
        shift1.setDate(LocalDate.now().plusDays(2));
        Shift shift2 = new Shift();
        shift2.setUsers(List.of(user));
        shift2.setDate(LocalDate.now().plusDays(3));
        List<Shift> shifts = new ArrayList<>();
        shifts.add(shift1);
        shifts.add(shift2);
        monthlyPlan.setShifts(shifts);

        repository.save(monthlyPlan);

        when(userService.findById(any())).thenReturn(user);
        when(usersResource.get(any())).thenReturn(mock(UserResource.class));
        when(realmResource.users()).thenReturn(usersResource);
        Principal principal = () -> USER_ID;

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
