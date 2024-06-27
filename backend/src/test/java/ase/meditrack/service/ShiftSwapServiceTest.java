package ase.meditrack.service;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.dto.ShiftSwapDto;
import ase.meditrack.model.entity.Preferences;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftSwap;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.entity.enums.ShiftSwapStatus;
import ase.meditrack.model.mapper.ShiftSwapMapper;
import ase.meditrack.repository.RoleRepository;
import ase.meditrack.repository.ShiftRepository;
import ase.meditrack.repository.ShiftSwapRepository;
import ase.meditrack.repository.UserRepository;
import ase.meditrack.util.DefaultTestCreator;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.resource.RealmResource;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@Transactional
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockBean(KeycloakConfig.class)
@MockBean(KeycloakConfig.KeycloakPostConstruct.class)
@MockBean(RealmResource.class)
@MockBean(MailService.class)
@ExtendWith(SpringExtension.class)
public class ShiftSwapServiceTest {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";
    private static final String USER_ID_2 = "23450000-0000-0000-0000-000000000000";
    private User user;
    private User userSuggesting;
    private Team team;
    private Role role;

    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ShiftSwapRepository shiftSwapRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShiftSwapMapper shiftSwapMapper;
    @MockBean
    private UserService userService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private DefaultTestCreator defaultTestCreator;
    @Autowired
    private ShiftSwapService shiftSwapService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        team = defaultTestCreator.createDefaultTeam();
        Role role = defaultTestCreator.createDefaultRole(team);


        user = userRepository.save(new User(
                UUID.fromString(USER_ID),
                role,
                1f,
                0,
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
        ));

        userSuggesting = userRepository.save(new User(
                UUID.fromString(USER_ID_2),
                role,
                1f,
                0,
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
        ));

        team.setUsers(List.of(user, userSuggesting));
        Preferences preferences = new Preferences(null, List.of(), user);
        user.setPreferences(preferences);
        user = userRepository.save(user);

        Preferences preferences2 = new Preferences(null, List.of(), userSuggesting);
        userSuggesting.setPreferences(preferences2);
        userSuggesting = userRepository.save(userSuggesting);

    }

    @Test
    void testCreateSimpleShiftSwap() {
        Shift shift = new Shift();
        shift.setUsers(List.of(user));
        shift.setDate(LocalDate.now().plusDays(2));
        shift = shiftRepository.save(shift);

        ShiftSwap shiftSwap = new ShiftSwap();
        shiftSwap.setSwapRequestingUser(user);
        shiftSwap.setRequestedShift(shift);
        shiftSwap.setRequestedShiftSwapStatus(ShiftSwapStatus.ACCEPTED);

        ShiftSwap result = shiftSwapService.create(shiftSwap, false);
        assertEquals(result.getSwapRequestingUser().getId(), user.getId());
    }

    @Test
    void testCreateShiftSwapWithoutSimpleShouldFail() {
        Shift shift = new Shift();
        shift.setUsers(List.of(user));
        shift.setDate(LocalDate.now().plusDays(2));
        shift = shiftRepository.save(shift);

        Shift shiftSuggesting = new Shift();
        shiftSuggesting.setUsers(List.of(userSuggesting));
        shiftSuggesting.setDate(LocalDate.now().plusDays(2));
        shiftSuggesting = shiftRepository.save(shiftSuggesting);

        ShiftSwap shiftSwap = new ShiftSwap();
        shiftSwap.setSwapRequestingUser(user);
        shiftSwap.setRequestedShift(shift);
        shiftSwap.setRequestedShiftSwapStatus(ShiftSwapStatus.ACCEPTED);
        shiftSwap.setSwapSuggestingUser(userSuggesting);
        shiftSwap.setSuggestedShift(shiftSuggesting);
        shiftSwap.setSuggestedShiftSwapStatus(ShiftSwapStatus.PENDING);

        assertThrows(ValidationException.class, () -> shiftSwapService.create(shiftSwap, false));
    }

    @Test
    void testIsShiftFromUser() {
        Shift shift = new Shift();
        shift.setUsers(List.of(user));
        shift.setDate(LocalDate.now().plusDays(2));
        shift = shiftRepository.save(shift);

        ShiftSwap shiftSwap = new ShiftSwap();
        shiftSwap.setSwapRequestingUser(user);
        shiftSwap.setRequestedShift(shift);
        shiftSwap.setRequestedShiftSwapStatus(ShiftSwapStatus.ACCEPTED);

        ShiftSwap result = shiftSwapRepository.save(shiftSwap);
        Principal principal = () -> USER_ID;
        Principal principal2 = () -> USER_ID_2;
        when(userService.getPrincipalWithTeam(principal)).thenReturn(user);
        when(userService.getPrincipalWithTeam(principal2)).thenReturn(userSuggesting);
        assertTrue(shiftSwapService.isShiftFromUser(principal, result));
        assertFalse(shiftSwapService.isShiftFromUser(principal2, result));
    }

    @Test
    void testIsShiftFromUserId() {
        Shift shift = new Shift();
        shift.setUsers(List.of(user));
        shift.setDate(LocalDate.now().plusDays(2));
        shift = shiftRepository.save(shift);

        ShiftSwap shiftSwap = new ShiftSwap();
        shiftSwap.setSwapRequestingUser(user);
        shiftSwap.setRequestedShift(shift);
        shiftSwap.setRequestedShiftSwapStatus(ShiftSwapStatus.ACCEPTED);

        ShiftSwap result = shiftSwapRepository.save(shiftSwap);
        Principal principal = () -> USER_ID;
        Principal principal2 = () -> USER_ID_2;
        when(userService.getPrincipalWithTeam(principal)).thenReturn(user);
        when(userService.getPrincipalWithTeam(principal2)).thenReturn(userSuggesting);
        assertTrue(shiftSwapService.isShiftSwapFromUser(principal, result.getId()));
        assertFalse(shiftSwapService.isShiftSwapFromUser(principal2, result.getId()));
    }

    @Test
    void testisShiftSwapFromSuggestedUser() {
        Shift shift = new Shift();
        shift.setUsers(List.of(user));
        shift.setDate(LocalDate.now().plusDays(2));
        shift = shiftRepository.save(shift);

        Shift shiftSuggesting = new Shift();
        shiftSuggesting.setUsers(List.of(userSuggesting));
        shiftSuggesting.setDate(LocalDate.now().plusDays(2));
        shiftSuggesting = shiftRepository.save(shiftSuggesting);

        ShiftSwap shiftSwap = new ShiftSwap();
        shiftSwap.setSwapRequestingUser(user);
        shiftSwap.setRequestedShift(shift);
        shiftSwap.setRequestedShiftSwapStatus(ShiftSwapStatus.ACCEPTED);
        shiftSwap.setSwapSuggestingUser(userSuggesting);
        shiftSwap.setSuggestedShift(shiftSuggesting);
        shiftSwap.setSuggestedShiftSwapStatus(ShiftSwapStatus.PENDING);

        ShiftSwapDto shiftSwapDto = shiftSwapMapper.toDto(shiftSwap);

        Principal principal = () -> USER_ID;
        Principal principalSuggesting = () -> USER_ID_2;
        when(userService.getPrincipalWithTeam(principal)).thenReturn(user);
        when(userService.getPrincipalWithTeam(principalSuggesting)).thenReturn(userSuggesting);
        assertTrue(shiftSwapService.isShiftSwapFromSuggestedUser(principalSuggesting, shiftSwapDto));
        assertFalse(shiftSwapService.isShiftSwapFromSuggestedUser(principal, shiftSwapDto));

    }

    @Test
    void testFindAll() {
        Shift shift = new Shift();
        shift.setUsers(List.of(user));
        shift.setDate(LocalDate.now().plusDays(2));
        shift = shiftRepository.save(shift);

        ShiftSwap shiftSwap = new ShiftSwap();
        shiftSwap.setSwapRequestingUser(user);
        shiftSwap.setRequestedShift(shift);
        shiftSwap.setRequestedShiftSwapStatus(ShiftSwapStatus.ACCEPTED);

        ShiftSwap result = shiftSwapRepository.save(shiftSwap);
        assertTrue(shiftSwapService.findAll().contains(result));
    }

    @Test
    void testRetractShiftSwap() {
        Shift shift = new Shift();
        shift.setUsers(List.of(user));
        shift.setDate(LocalDate.now().plusDays(2));
        shift = shiftRepository.save(shift);

        ShiftSwap shiftSwap = new ShiftSwap();
        shiftSwap.setSwapRequestingUser(user);
        shiftSwap.setRequestedShift(shift);
        shiftSwap.setRequestedShiftSwapStatus(ShiftSwapStatus.ACCEPTED);

        ShiftSwap result = shiftSwapRepository.save(shiftSwap);
        assertTrue(shiftSwapService.findAll().contains(result));
        shiftSwapService.retract(result.getId());
        assertFalse(shiftSwapService.findAll().contains(result));
    }

    @Test
    void testDeleteSimpleShiftSwap() {
        Shift shift = new Shift();
        shift.setUsers(List.of(user));
        shift.setDate(LocalDate.now().plusDays(2));
        shift = shiftRepository.save(shift);

        ShiftSwap shiftSwap = new ShiftSwap();
        shiftSwap.setSwapRequestingUser(user);
        shiftSwap.setRequestedShift(shift);
        shiftSwap.setRequestedShiftSwapStatus(ShiftSwapStatus.ACCEPTED);

        ShiftSwap simpleShiftSwap = shiftSwapRepository.save(shiftSwap);

        Shift shiftSuggesting = new Shift();
        shiftSuggesting.setUsers(List.of(userSuggesting));
        shiftSuggesting.setDate(LocalDate.now().plusDays(2));
        shiftSuggesting = shiftRepository.save(shiftSuggesting);

        ShiftSwap shiftSwap2 = new ShiftSwap();
        shiftSwap2.setSwapRequestingUser(user);
        shiftSwap2.setRequestedShift(shift);
        shiftSwap2.setRequestedShiftSwapStatus(ShiftSwapStatus.ACCEPTED);
        shiftSwap.setSwapSuggestingUser(userSuggesting);
        shiftSwap.setSuggestedShift(shiftSuggesting);
        shiftSwap.setSuggestedShiftSwapStatus(ShiftSwapStatus.PENDING);

        ShiftSwap shiftSwapRequest = shiftSwapRepository.save(shiftSwap2);

        Principal principal = () -> USER_ID;
        when(userService.getPrincipalWithTeam(principal)).thenReturn(user);

        assertEquals(1, shiftSwapService.findAllByCurrentMonth(principal).size());
        assertEquals(1, shiftSwapService.findAllRequests(principal).size());
        shiftSwapService.delete(simpleShiftSwap.getId());
        assertFalse(shiftSwapService.findAll().contains(shiftSwapRequest));
    }

    @Test
    void testFindShiftSwapOffersInCurrentMonth() {
        Shift shift = new Shift();
        shift.setUsers(List.of(user));
        shift.setDate(LocalDate.now().plusDays(2));
        shift = shiftRepository.save(shift);

        ShiftSwap shiftSwap = new ShiftSwap();
        shiftSwap.setSwapRequestingUser(user);
        shiftSwap.setRequestedShift(shift);
        shiftSwap.setRequestedShiftSwapStatus(ShiftSwapStatus.ACCEPTED);

        ShiftSwap simpleShiftSwap = shiftSwapRepository.save(shiftSwap);

        Shift shiftLastMonth = new Shift();
        shiftLastMonth.setUsers(List.of(userSuggesting));
        shiftLastMonth.setDate(LocalDate.now().minusDays(31));
        shiftLastMonth = shiftRepository.save(shiftLastMonth);

        ShiftSwap shiftSwapLastMonth = new ShiftSwap();
        shiftSwapLastMonth.setSwapRequestingUser(user);
        shiftSwapLastMonth.setRequestedShift(shiftLastMonth);
        shiftSwapLastMonth.setRequestedShiftSwapStatus(ShiftSwapStatus.ACCEPTED);

        ShiftSwap simpleShiftSwapLastMonth = shiftSwapRepository.save(shiftSwapLastMonth);

        Principal principal2 = () -> USER_ID_2;
        when(userService.getPrincipalWithTeam(principal2)).thenReturn(userSuggesting);

        assertFalse(shiftSwapService.findAllOffersByCurrentMonth(principal2).contains(simpleShiftSwapLastMonth));
        assertEquals(1, shiftSwapService.findAllOffersByCurrentMonth(principal2).size());
    }

    @Test
    void testFindShiftSwapRequests() {
        Shift shift = new Shift();
        shift.setUsers(List.of(user));
        shift.setDate(LocalDate.now().plusDays(2));
        shift = shiftRepository.save(shift);

        ShiftSwap shiftSwap = new ShiftSwap();
        shiftSwap.setSwapRequestingUser(user);
        shiftSwap.setRequestedShift(shift);
        shiftSwap.setRequestedShiftSwapStatus(ShiftSwapStatus.ACCEPTED);

        ShiftSwap simpleShiftSwap = shiftSwapRepository.save(shiftSwap);

        Shift shiftSuggesting = new Shift();
        shiftSuggesting.setUsers(List.of(userSuggesting));
        shiftSuggesting.setDate(LocalDate.now().plusDays(2));
        shiftSuggesting = shiftRepository.save(shiftSuggesting);

        ShiftSwap shiftSwap2 = new ShiftSwap();
        shiftSwap2.setSwapRequestingUser(user);
        shiftSwap2.setRequestedShift(shift);
        shiftSwap2.setRequestedShiftSwapStatus(ShiftSwapStatus.ACCEPTED);
        shiftSwap2.setSwapSuggestingUser(userSuggesting);
        shiftSwap2.setSuggestedShift(shiftSuggesting);
        shiftSwap2.setSuggestedShiftSwapStatus(ShiftSwapStatus.PENDING);

        ShiftSwap shiftSwapRequest = shiftSwapRepository.save(shiftSwap2);

        Principal principal2 = () -> USER_ID_2;
        when(userService.getPrincipalWithTeam(principal2)).thenReturn(userSuggesting);
        assertFalse(shiftSwapService.findAllSuggestions(principal2).contains(simpleShiftSwap));
        assertTrue(shiftSwapService.findAllSuggestions(principal2).contains(shiftSwapRequest));
        assertEquals(1, shiftSwapService.findAllOffersByCurrentMonth(principal2).size());
    }


}
