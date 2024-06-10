package ase.meditrack.service;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.HardConstraints;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.ShiftOffShiftIdList;
import ase.meditrack.repository.HardConstraintsRepository;
import ase.meditrack.repository.RoleRepository;
import ase.meditrack.repository.ShiftOffShiftIdListRepository;
import ase.meditrack.repository.UserRepository;
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
                null
        ));
        team = teamService.create(
                new Team(null, "test team", 40, null, null, null, null, null),
                () -> USER_ID
        );
    }

    @Test
    void findAllReturnsAllHardConstraints() {
        List<HardConstraints> resultList = service.findAll();

        assertAll(
                () -> assertNotNull(resultList),
                () -> assertEquals(1, resultList.size()) // team is saved
        );
    }

    @Test
    void findByIdReturnsHardConstraints() {
        HardConstraints hardConstraint = new HardConstraints();
        hardConstraint.setId(team.getId());
        hardConstraint.setMandatoryOffDays(2);
        repository.save(hardConstraint);
        HardConstraints savedHardConstraints = repository.findById(hardConstraint.getId()).get();

        HardConstraints result = service.findById(savedHardConstraints.getId());

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(hardConstraint.getId(), result.getId()),
                () -> assertEquals(hardConstraint.getMandatoryOffDays(), result.getMandatoryOffDays()),
                () -> assertEquals(hardConstraint, result)
        );
    }

    @Test
    void findByTeamReturnsHardConstraints() {
        HardConstraints hardConstraint = new HardConstraints();
        hardConstraint.setId(team.getId());
        hardConstraint.setMandatoryOffDays(2);
        repository.save(hardConstraint);

        // other team creation
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
                null
        ));

        Team otherTeam = teamService.create(
                new Team(null, "other test team", 40, null, null, null, null, null),
                () -> otherUserId
        );

        HardConstraints hardConstraintOtherTeam = new HardConstraints();
        hardConstraintOtherTeam.setId(otherTeam.getId());
        hardConstraintOtherTeam.setMandatoryOffDays(2);
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
                () -> assertEquals(hardConstraint.getMandatoryOffDays(), resultTeam.getMandatoryOffDays()),
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
        roleDay.setUsers(null);
        roleDay.setTeam(team);
        roleRepository.save(roleDay);
        Map<Role, Integer> daytime = new HashMap<>();
        daytime.put(roleDay, 1);

        Role roleNight = new Role();
        roleNight.setName("Role Night");
        roleNight.setColor("FF0001");
        roleNight.setAbbreviation("RN");
        roleNight.setUsers(null);
        roleNight.setTeam(team);
        roleRepository.save(roleNight);
        Map<Role, Integer> nighttime = new HashMap<>();
        nighttime.put(roleNight, 2);

        HardConstraints hardConstraint = new HardConstraints();
        hardConstraint.setId(team.getId());
        hardConstraint.setDaytimeRequiredRoles(daytime);
        hardConstraint.setNighttimeRequiredRoles(nighttime);
        HardConstraints savedHardConstraints = service.create(hardConstraint, principal);

        assertAll(
                () -> assertNotNull(savedHardConstraints),
                () -> assertEquals(hardConstraint.getId(), savedHardConstraints.getId()),
                () -> assertEquals(hardConstraint.getMandatoryOffDays(), savedHardConstraints.getMandatoryOffDays())
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

        Map<ShiftOffShiftIdList, UUID> shiftOffShift = new HashMap<>();
        shiftOffShift.put(shiftOffShiftIdList, UUID.randomUUID());
        shiftOffShiftIdListRepository.save(shiftOffShiftIdList);
        updatedList.setShiftOffShift(shiftOffShift);

        Map<Role, Integer> daytimeRequiredRoles = new HashMap<>();
        Role role1 = new Role();
        role1.setName("Doctor");
        role1.setColor("FF0000");
        role1.setAbbreviation("D");
        role1.setUsers(null);
        role1.setTeam(team);
        Role role2 = new Role();
        role2.setName("Nurse");
        role2.setColor("FF0200");
        role2.setAbbreviation("N");
        role2.setUsers(null);
        role2.setTeam(team);
        daytimeRequiredRoles.put(role1, 2);
        daytimeRequiredRoles.put(role2, 3);
        roleRepository.save(role1);
        roleRepository.save(role2);
        updatedList.setDaytimeRequiredRoles(daytimeRequiredRoles);

        Map<Role, Integer> nighttimeRequiredRoles = new HashMap<>();
        nighttimeRequiredRoles.put(role1, 1);
        updatedList.setNighttimeRequiredRoles(nighttimeRequiredRoles);

        updatedList.setAllowedFlextimeTotal(40);
        updatedList.setAllowedFlextimePerMonth(10);
        updatedList.setMandatoryOffDays(5);
        updatedList.setMinRestPeriod(8);
        updatedList.setMaximumShiftLengths(12);
        updatedList.setTeam(team);

        HardConstraints result = service.update(updatedList);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(updatedList.getId(), result.getId()),
                () -> assertEquals(updatedList.getMandatoryOffDays(), result.getMandatoryOffDays())
        );
    }

    @Test
    void deleteHardConstraints() {
        HardConstraints hardConstraint = new HardConstraints();
        hardConstraint.setId(team.getId());
        repository.save(hardConstraint);

        List<HardConstraints> resultList = service.findAll();
        assertEquals(1, resultList.size());

        service.delete(hardConstraint.getId());
        userRepository.deleteAll();
        teamService.delete(team.getId());

        resultList = service.findAll();
        assertEquals(0, resultList.size());
    }
}
