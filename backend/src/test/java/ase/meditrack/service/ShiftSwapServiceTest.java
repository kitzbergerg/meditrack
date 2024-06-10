package ase.meditrack.service;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.entity.ShiftSwap;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.entity.Team;
import ase.meditrack.repository.UserRepository;
import ase.meditrack.repository.ShiftRepository;
import ase.meditrack.repository.ShiftSwapRepository;
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
import java.util.UUID;
import java.util.List;

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
public class ShiftSwapServiceTest {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private ShiftSwapRepository repository;
    @Autowired
    private ShiftSwapService service;
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
    void findAllReturnsAllShiftSwap() {
        List<ShiftSwap> resultList = service.findAll();

        assertAll(
                () -> assertNotNull(resultList),
                () -> assertEquals(0, resultList.size())
        );
    }

    @Test
    void findAllByCurrentMonthReturnsCorrectShiftSwaps() {
        // current month
        Shift shift = new Shift();
        shift.setDate(LocalDate.now().plusDays(2));
        shift = shiftRepository.save(shift);
        ShiftSwap shiftSwap = new ShiftSwap();
        shiftSwap.setSwapRequestingUser(user);
        shiftSwap.setRequestedShift(shift);
        repository.save(shiftSwap);

        // last month
        Shift shift2 = new Shift();
        shift2.setDate(LocalDate.now().minusDays(70));
        shift2 = shiftRepository.save(shift2);
        ShiftSwap shiftSwap3 = new ShiftSwap();
        shiftSwap.setSwapRequestingUser(user);
        shiftSwap.setRequestedShift(shift2);
        repository.save(shiftSwap3);

        // last month
        Shift shift3 = new Shift();
        shift3.setDate(LocalDate.now().minusDays(70));
        shift3 = shiftRepository.save(shift3);
        ShiftSwap shiftSwap4 = new ShiftSwap();
        shiftSwap.setSwapRequestingUser(user);
        shiftSwap.setRequestedShift(shift3);
        repository.save(shiftSwap4);

        Principal principal = new Principal() {
            @Override
            public String getName() {
                return USER_ID;
            }
        };

        List<ShiftSwap> shiftSwaps = service.findAllByCurrentMonth(principal);
        List<ShiftSwap> allShiftSwaps = service.findAll();

        assertAll(
                () -> assertNotNull(shiftSwaps),
                () -> assertEquals(1, shiftSwaps.size()),
                () -> assertEquals(3, allShiftSwaps.size())
        );
    }
}
