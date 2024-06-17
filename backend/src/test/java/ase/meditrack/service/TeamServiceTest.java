package ase.meditrack.service;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.entity.HardConstraints;
import ase.meditrack.repository.TeamRepository;
import ase.meditrack.repository.UserRepository;
import ase.meditrack.repository.ShiftTypeRepository;
import ase.meditrack.repository.MonthlyPlanRepository;
import ase.meditrack.repository.HardConstraintsRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.security.Principal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
class TeamServiceTest {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private TeamService service;
    @Autowired
    private TeamRepository repository;
    @Autowired
    private ShiftTypeRepository shiftTypeRepository;
    @Autowired
    private MonthlyPlanRepository monthlyPlanRepository;
    @Autowired
    private HardConstraintsRepository hardConstraintsRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;

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
                null,
                null
        ));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_getTeams_succeeds() {
        List<Team> teams = service.findAll();

        assertAll(
                () -> assertNotNull(teams),
                () -> assertEquals(0, teams.size())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_deleteTeam_succeeds() {
        Team team = new Team();
        team.setId(null);
        team.setName("testTeam");
        team.setWorkingHours(1);

        Principal principal = new Principal() {
            @Override
            public String getName() {
                return USER_ID;
            }
        };
        service.create(team, principal);

        List<Team> resultList = service.findAll();
        Assertions.assertEquals(1, resultList.size());

        userRepository.delete(user);
        service.delete(team.getId());

        resultList = service.findAll();
        Assertions.assertEquals(0, resultList.size());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_findTeamById_succeeds() {
        Team team = new Team();
        team.setId(null);
        team.setName("testTeam");
        team.setWorkingHours(1);

        Principal principal = new Principal() {
            @Override
            public String getName() {
                return USER_ID;
            }
        };
        service.create(team, principal);

        Team foundTeam = service.findById(team.getId());

        assertAll(
                () -> assertEquals(team.getId(), foundTeam.getId()),
                () -> assertEquals(team.getName(), foundTeam.getName())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_updateTeam_succeeds() {
        Team team = new Team();
        team.setId(null);
        team.setName("testTeam");
        team.setWorkingHours(1);

        Principal principal = new Principal() {
            @Override
            public String getName() {
                return USER_ID;
            }
        };
        service.create(team, principal);

        Team updatedTeam = new Team();
        updatedTeam.setId(team.getId());
        updatedTeam.setName("test team");
        ShiftType shiftType = new ShiftType();
        shiftType.setName("Test ShiftType");
        shiftType.setColor("#FF0000");
        shiftType.setAbbreviation("TS");
        shiftType.setStartTime(LocalTime.of(8, 0, 0, 0));
        shiftType.setEndTime(LocalTime.of(16, 0, 0, 0));
        shiftType.setBreakStartTime(LocalTime.of(12, 0, 0, 0));
        shiftType.setBreakEndTime(LocalTime.of(12, 30, 0, 0));
        shiftTypeRepository.save(shiftType);
        List<ShiftType> shiftTypes = new ArrayList<>();
        shiftTypes.add(shiftType);
        updatedTeam.setShiftTypes(shiftTypes);
        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setTeam(team);
        monthlyPlan.setYear(2024);
        monthlyPlan.setMonth(6);
        monthlyPlan.setPublished(false);
        monthlyPlanRepository.save(monthlyPlan);
        List<MonthlyPlan> monthlyPlans = new ArrayList<>();
        monthlyPlans.add(monthlyPlan);
        updatedTeam.setMonthlyPlans(monthlyPlans);
        List<User> users = new ArrayList<>();
        users.add(user);
        updatedTeam.setUsers(users);
        HardConstraints hardConstraint = new HardConstraints();
        hardConstraint.setId(team.getId());
        hardConstraintsRepository.save(hardConstraint);
        updatedTeam.setHardConstraints(hardConstraint);
        updatedTeam.setWorkingHours(3);

        Team responseTeam = service.update(updatedTeam);

        assertAll(
                () -> assertEquals(updatedTeam.getId(), responseTeam.getId()),
                () -> assertEquals(updatedTeam.getName(), responseTeam.getName()),
                () -> assertEquals(1, repository.count())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_createTeam_succeeds() {
        Team team = new Team();
        team.setId(null);
        team.setName("testTeam");
        team.setWorkingHours(1);

        Principal principal = new Principal() {
            @Override
            public String getName() {
                return USER_ID;
            }
        };

        Team created = service.create(team, principal);

        assertAll(
                () -> assertNotNull(created),
                () -> assertNotNull(created.getId()),
                () -> assertEquals(team.getName(), created.getName()),
                () -> assertEquals(1, repository.count())
        );
    }
}
