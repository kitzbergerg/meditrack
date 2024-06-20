package ase.meditrack.service;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.HardConstraints;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.ShiftOffShiftIdList;
import ase.meditrack.repository.*;
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
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
public class HardConstraintsServiceTest {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private HardConstraintsRepository repository;
    @Autowired
    private HardConstraintsService service;
    @Autowired
    private ShiftOffShiftIdListRepository shiftOffShiftIdListRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamService teamService;
    private Team team;
    private Team otherTeam;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        userRepository.save(new User(
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
        team = teamService.create(
                new Team(null, "test team", 40, null, null, null, null, null),
                () -> USER_ID
        );

        // for other team
        String otherUserId = "11111111-1111-1111-1111-111111111111";
        userRepository.save(new User(
                UUID.fromString(otherUserId),
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

        otherTeam = teamService.create(
                new Team(null, "other test team", 40, null, null, null, null, null),
                () -> otherUserId
        );
    }

    @Test
    void findAllReturnsAllHardConstraints() {
        List<HardConstraints> resultList = service.findAll();

        assertAll(
                () -> assertNotNull(resultList),
                () -> assertEquals(2, resultList.size()) // team and otherteam are saved
        );
    }

    @Test
    void findByIdReturnsHardConstraints() {
        HardConstraints hardConstraint = new HardConstraints();
        hardConstraint.setId(team.getId());
        hardConstraint.setWorkingHours(10);
        repository.save(hardConstraint);
        HardConstraints savedHardConstraints = repository.findById(hardConstraint.getId()).get();

        HardConstraints result = service.findById(savedHardConstraints.getId());

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(hardConstraint.getId(), result.getId()),
                () -> assertEquals(hardConstraint.getWorkingHours(), result.getWorkingHours()),
                () -> assertEquals(hardConstraint, result)
        );
    }

    @Test
    void findByTeamReturnsHardConstraints() {
        HardConstraints hardConstraint = new HardConstraints();
        hardConstraint.setId(team.getId());
        hardConstraint.setDaytimeRequiredPeople(2);
        hardConstraint.setTeam(team);
        repository.save(hardConstraint);

        HardConstraints hardConstraintOtherTeam = new HardConstraints();
        hardConstraintOtherTeam.setId(otherTeam.getId());
        hardConstraintOtherTeam.setDaytimeRequiredPeople(2);
        hardConstraintOtherTeam.setTeam(otherTeam);
        repository.save(hardConstraintOtherTeam);

        Principal principal = new Principal() {
            @Override
            public String getName() {
                return USER_ID;
            }
        };

        HardConstraints resultTeam = service.findByTeam(principal);
        List<HardConstraints> resultAll = service.findAll();

        assertAll(
                () -> assertNotNull(resultTeam),
                () -> assertEquals(resultTeam.getTeam(), team),
                () -> assertEquals(resultAll.size(), 2),
                () -> assertEquals(hardConstraint.getId(), resultTeam.getId()),
                () -> assertEquals(hardConstraint.getDaytimeRequiredPeople(), resultTeam.getDaytimeRequiredPeople()),
                () -> assertEquals(hardConstraint, resultTeam)
        );
    }

    @Test
    void findByIdThrowsNotFoundExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.findById(id));

        assertEquals("Could not find hard constraints with id: " + id + "!", exception.getMessage());
    }

    @Test
    void createHardConstraints() {
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return USER_ID;
            }
        };
        Role roleDay = new Role();
        roleDay.setName("Role Day");
        roleDay.setColor("FF0000");
        roleDay.setAbbreviation("RD");
        roleDay.setAllowedFlextimePerMonth(0);
        roleDay.setAllowedFlextimeTotal(0);
        roleDay.setDaytimeRequiredPeople(3);
        roleDay.setNighttimeRequiredPeople(4);
        roleDay.setUsers(null);
        roleDay.setTeam(team);
        roleRepository.save(roleDay);
        Map<Role, Integer> daytime = new HashMap<>();
        daytime.put(roleDay, 1);

        Role roleNight = new Role();
        roleNight.setName("Role Night");
        roleNight.setColor("FF0001");
        roleNight.setAbbreviation("RN");
        roleNight.setAllowedFlextimePerMonth(0);
        roleNight.setAllowedFlextimeTotal(0);
        roleNight.setDaytimeRequiredPeople(3);
        roleNight.setNighttimeRequiredPeople(4);
        roleNight.setUsers(null);
        roleNight.setTeam(team);
        roleRepository.save(roleNight);
        Map<Role, Integer> nighttime = new HashMap<>();
        nighttime.put(roleNight, 2);

        HardConstraints hardConstraint = new HardConstraints();
        hardConstraint.setId(team.getId());
        hardConstraint.setDaytimeRequiredPeople(3);
        hardConstraint.setNighttimeRequiredPeople(4);
        HardConstraints savedHardConstraints = service.update(hardConstraint, principal);

        assertAll(
                () -> assertNotNull(savedHardConstraints),
                () -> assertEquals(hardConstraint.getId(), savedHardConstraints.getId()),
                () -> assertEquals(hardConstraint.getDaytimeRequiredPeople(), savedHardConstraints.getDaytimeRequiredPeople())
        );
    }

    @Test
    void updateHardConstraints() {
        HardConstraints hardConstraint = new HardConstraints();
        hardConstraint.setId(team.getId());
        repository.save(hardConstraint);
        HardConstraints savedHardConstraints = repository.findById(hardConstraint.getId()).get();

        HardConstraints updatedList = new HardConstraints();
        updatedList.setId(savedHardConstraints.getId());
        List<UUID> list = new ArrayList<>();
        list.add(UUID.randomUUID());
        ShiftOffShiftIdList shiftOffShiftIdList = new ShiftOffShiftIdList();
        shiftOffShiftIdList.setShiftOffShiftIdList(list);

        updatedList.setTeam(team);
        updatedList.setNighttimeRequiredPeople(10);
        updatedList.setDaytimeRequiredPeople(16);
        updatedList.setWorkingHours(10);
        updatedList.setMaxWeeklyHours(40);
        updatedList.setMaxConsecutiveShifts(5);

        Principal principal = new Principal() {
            @Override
            public String getName() {
                return USER_ID;
            }
        };

        HardConstraints result = service.update(updatedList, principal);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(updatedList.getId(), result.getId()),
                () -> assertEquals(updatedList.getDaytimeRequiredPeople(), result.getDaytimeRequiredPeople()),
                () -> assertEquals(updatedList.getNighttimeRequiredPeople(), result.getNighttimeRequiredPeople()),
                () -> assertEquals(updatedList.getTeam(), result.getTeam()),
                () -> assertEquals(updatedList.getWorkingHours(), result.getWorkingHours()),
                () -> assertEquals(updatedList.getMaxConsecutiveShifts(), result.getMaxConsecutiveShifts()),
                () -> assertEquals(updatedList.getMaxWeeklyHours(), result.getMaxWeeklyHours())
        );
    }

    @Test
    void deleteHardConstraints() {
        HardConstraints hardConstraint = new HardConstraints();
        hardConstraint.setId(team.getId());
        repository.save(hardConstraint);

        List<HardConstraints> resultList = service.findAll();
        assertEquals(2, resultList.size());

        service.delete(hardConstraint.getId());
        userRepository.deleteById(UUID.fromString(USER_ID));
        teamService.delete(team.getId());

        resultList = service.findAll();
        assertEquals(1, resultList.size());
    }
}
