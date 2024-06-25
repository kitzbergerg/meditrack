package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.*;
import ase.meditrack.repository.ShiftRepository;
import ase.meditrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(any())).thenReturn(mock(UserResource.class));
    }

    @Test
    void testGetSickReplacement_shiftNotFound() {
        UUID shiftId = UUID.randomUUID();
        when(shiftRepository.findById(shiftId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getSickReplacement(shiftId));
    }

    @Test
    void testGetSickReplacement_noReplacementAvailable() {
        UUID shiftId = UUID.randomUUID();
        User sickUser = new User();
        sickUser.setId(UUID.randomUUID());
        Role role = new Role();
        sickUser.setRole(role);

        Shift shift = new Shift();
        shift.setUsers(Collections.singletonList(sickUser));
        shift.setDate(LocalDate.now());

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(userRepository.findAllByRole(role)).thenReturn(Collections.singletonList(sickUser));
        when(shiftRepository.findAllByUsersAndDateAfterAndDateBefore(any(), any(), any())).thenReturn(Collections.emptyList());

        List<User> users = userService.getSickReplacement(shiftId);

        assertTrue(users.isEmpty());
    }

    @Test
    void testGetSickReplacement_shiftOnSameDay() {
        UUID shiftId = UUID.randomUUID();
        User sickUser = new User();
        sickUser.setId(UUID.randomUUID());
        Role role = new Role();
        sickUser.setRole(role);

        User replacementUser = new User();
        replacementUser.setId(UUID.randomUUID());
        replacementUser.setRole(role);

        Shift shift = new Shift();
        shift.setUsers(Collections.singletonList(sickUser));
        shift.setDate(LocalDate.now());

        Shift replacementShift = new Shift();
        replacementShift.setUsers(Collections.singletonList(replacementUser));
        replacementShift.setDate(shift.getDate());

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(userRepository.findAllByRole(role)).thenReturn(List.of(replacementUser, sickUser));
        when(shiftRepository.findAllByUsersAndDateAfterAndDateBefore(any(), any(), any())).thenReturn(Collections.singletonList(replacementShift));

        List<User> users = userService.getSickReplacement(shiftId);

        assertTrue(users.isEmpty());
    }

    @Test
    void testGetSickReplacement_replacementAvailable() {
        UUID shiftId = UUID.randomUUID();
        User sickUser = new User();
        sickUser.setId(UUID.randomUUID());
        Role role = new Role();
        sickUser.setRole(role);

        User replacementUser = new User();
        replacementUser.setId(UUID.randomUUID());
        replacementUser.setRole(role);
        replacementUser.setHolidays(Collections.emptyList());

        Shift shift = new Shift();
        shift.setUsers(Collections.singletonList(sickUser));
        shift.setDate(LocalDate.now());

        ShiftType shiftType = new ShiftType();
        shiftType.setStartTime(LocalTime.of(9, 0));
        shiftType.setEndTime(LocalTime.of(18, 0));
        shift.setShiftType(shiftType);

        UserResource userResourceMock = mock(UserResource.class);
        UserRepresentation userRepresentation = new UserRepresentation();

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(userRepository.findAllByRole(role)).thenReturn(Arrays.asList(replacementUser, sickUser));
        when(usersResource.get(any())).thenReturn(userResourceMock);
        when(userResourceMock.toRepresentation()).thenReturn(userRepresentation);
        when(shiftRepository.findAllByUsersAndDateAfterAndDateBefore(any(), any(), any())).thenReturn(Collections.emptyList());

        List<User> users = userService.getSickReplacement(shiftId);

        assertEquals(1, users.size());
        assertEquals(replacementUser.getId(), users.get(0).getId());
    }

    @Test
    void testGetSickReplacement_userOnHoliday() {
        UUID shiftId = UUID.randomUUID();
        User sickUser = new User();
        sickUser.setId(UUID.randomUUID());
        Role role = new Role();
        sickUser.setRole(role);

        User replacementUser = new User();
        replacementUser.setId(UUID.randomUUID());
        replacementUser.setRole(role);

        Holiday holiday = new Holiday();
        holiday.setStartDate(LocalDate.now().minusDays(1));
        holiday.setEndDate(LocalDate.now().plusDays(1));
        replacementUser.setHolidays(Collections.singletonList(holiday));

        Shift shift = new Shift();
        shift.setUsers(Collections.singletonList(sickUser));
        shift.setDate(LocalDate.now());

        ShiftType shiftType = new ShiftType();
        shiftType.setStartTime(LocalTime.of(9, 0));
        shiftType.setEndTime(LocalTime.of(18, 0));
        shift.setShiftType(shiftType);

        UserResource userResourceMock = mock(UserResource.class);
        UserRepresentation userRepresentation = new UserRepresentation();

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(userRepository.findAllByRole(role)).thenReturn(Arrays.asList(replacementUser, sickUser));
        when(usersResource.get(any())).thenReturn(userResourceMock);
        when(userResourceMock.toRepresentation()).thenReturn(userRepresentation);
        when(shiftRepository.findAllByUsersAndDateAfterAndDateBefore(any(), any(), any())).thenReturn(Collections.emptyList());

        List<User> users = userService.getSickReplacement(shiftId);

        assertTrue(users.isEmpty());
    }

    @Test
    void testGetSickReplacement_userHasWorkedTooManyDaysInRow() {
        UUID shiftId = UUID.randomUUID();
        User sickUser = new User();
        sickUser.setId(UUID.randomUUID());
        Role role = new Role();
        sickUser.setRole(role);

        User replacementUser = new User();
        replacementUser.setId(UUID.randomUUID());
        replacementUser.setRole(role);
        replacementUser.setHolidays(Collections.emptyList());

        Shift shift = new Shift();
        shift.setUsers(Collections.singletonList(sickUser));
        shift.setDate(LocalDate.now());

        ShiftType shiftType = new ShiftType();
        shiftType.setStartTime(LocalTime.of(9, 0));
        shiftType.setEndTime(LocalTime.of(18, 0));
        shift.setShiftType(shiftType);

        UserResource userResourceMock = mock(UserResource.class);
        UserRepresentation userRepresentation = new UserRepresentation();

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(userRepository.findAllByRole(role)).thenReturn(Arrays.asList(replacementUser, sickUser));
        when(usersResource.get(any())).thenReturn(userResourceMock);
        when(userResourceMock.toRepresentation()).thenReturn(userRepresentation);

        List<Shift> userShifts = new ArrayList<>();
        for (int i = -3; i <= 3; i++) {
            if (i == 0) {
                continue;
            }
            Shift userShift = new Shift();
            userShift.setDate(LocalDate.now().plusDays(i));
            userShift.setShiftType(shiftType);
            userShifts.add(userShift);
        }

        when(shiftRepository.findAllByUsersAndDateAfterAndDateBefore(any(), any(), any())).thenReturn(userShifts);

        List<User> users = userService.getSickReplacement(shiftId);

        assertTrue(users.isEmpty());
    }

    @Test
    void testGetSickReplacement_userHasNotWorkedTooManyDaysInRow() {
        UUID shiftId = UUID.randomUUID();
        User sickUser = new User();
        sickUser.setId(UUID.randomUUID());
        Role role = new Role();
        sickUser.setRole(role);

        User replacementUser = new User();
        replacementUser.setId(UUID.randomUUID());
        replacementUser.setRole(role);
        replacementUser.setHolidays(Collections.emptyList());

        Shift shift = new Shift();
        shift.setUsers(Collections.singletonList(sickUser));
        shift.setDate(LocalDate.now());

        ShiftType shiftType = new ShiftType();
        shiftType.setStartTime(LocalTime.of(9, 0));
        shiftType.setEndTime(LocalTime.of(18, 0));
        shift.setShiftType(shiftType);

        UserResource userResourceMock = mock(UserResource.class);
        UserRepresentation userRepresentation = new UserRepresentation();

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(userRepository.findAllByRole(role)).thenReturn(Arrays.asList(replacementUser, sickUser));
        when(usersResource.get(any())).thenReturn(userResourceMock);
        when(userResourceMock.toRepresentation()).thenReturn(userRepresentation);

        List<Shift> userShifts = new ArrayList<>();
        for (int i = -3; i <= 2; i++) {
            if (i == 0) {
                continue;
            }
            Shift userShift = new Shift();
            userShift.setDate(LocalDate.now().plusDays(i));
            userShift.setShiftType(shiftType);
            userShifts.add(userShift);
        }

        when(shiftRepository.findAllByUsersAndDateAfterAndDateBefore(any(), any(), any())).thenReturn(userShifts);

        List<User> users = userService.getSickReplacement(shiftId);

        assertEquals(replacementUser.getId(), users.get(0).getId());
    }

    @Test
    void testGetSickReplacement_nightShiftBeforeDayShift() {
        UUID shiftId = UUID.randomUUID();
        User sickUser = new User();
        sickUser.setId(UUID.randomUUID());
        Role role = new Role();
        sickUser.setRole(role);

        User replacementUser = new User();
        replacementUser.setId(UUID.randomUUID());
        replacementUser.setRole(role);
        replacementUser.setHolidays(Collections.emptyList());

        Shift shift = new Shift();
        shift.setUsers(Collections.singletonList(sickUser));
        shift.setDate(LocalDate.now());

        ShiftType dayShiftType = new ShiftType();
        dayShiftType.setStartTime(LocalTime.of(9, 0));
        dayShiftType.setEndTime(LocalTime.of(18, 0));
        shift.setShiftType(dayShiftType);


        UserResource userResourceMock = mock(UserResource.class);
        UserRepresentation userRepresentation = new UserRepresentation();

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(userRepository.findAllByRole(role)).thenReturn(Arrays.asList(replacementUser, sickUser));
        when(usersResource.get(any())).thenReturn(userResourceMock);
        when(userResourceMock.toRepresentation()).thenReturn(userRepresentation);

        List<Shift> userShifts = new ArrayList<>();

        Shift userShift = new Shift();
        userShift.setDate(LocalDate.now().minusDays(1));

        ShiftType nightShiftType = new ShiftType();
        nightShiftType.setStartTime(LocalTime.of(18, 0));
        nightShiftType.setEndTime(LocalTime.of(6, 0));
        userShift.setShiftType(nightShiftType);
        userShifts.add(userShift);


        when(shiftRepository.findAllByUsersAndDateAfterAndDateBefore(any(), any(), any())).thenReturn(userShifts);

        List<User> users = userService.getSickReplacement(shiftId);

        assertTrue(users.isEmpty());
    }

    @Test
    void testGetSickReplacement_dayShiftBeforeNightShift() {
        UUID shiftId = UUID.randomUUID();
        User sickUser = new User();
        sickUser.setId(UUID.randomUUID());
        Role role = new Role();
        sickUser.setRole(role);

        User replacementUser = new User();
        replacementUser.setId(UUID.randomUUID());
        replacementUser.setRole(role);
        replacementUser.setHolidays(Collections.emptyList());

        Shift shift = new Shift();
        shift.setUsers(Collections.singletonList(sickUser));
        shift.setDate(LocalDate.now());

        ShiftType dayShiftType = new ShiftType();
        dayShiftType.setStartTime(LocalTime.of(9, 0));
        dayShiftType.setEndTime(LocalTime.of(18, 0));

        ShiftType nightShiftType = new ShiftType();
        nightShiftType.setStartTime(LocalTime.of(18, 0));
        nightShiftType.setEndTime(LocalTime.of(6, 0));

        shift.setShiftType(nightShiftType);

        UserResource userResourceMock = mock(UserResource.class);
        UserRepresentation userRepresentation = new UserRepresentation();

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(userRepository.findAllByRole(role)).thenReturn(Arrays.asList(replacementUser, sickUser));
        when(usersResource.get(any())).thenReturn(userResourceMock);
        when(userResourceMock.toRepresentation()).thenReturn(userRepresentation);

        List<Shift> userShifts = new ArrayList<>();

        Shift userShift = new Shift();
        userShift.setDate(LocalDate.now().minusDays(1));

        userShift.setShiftType(dayShiftType);
        userShifts.add(userShift);


        when(shiftRepository.findAllByUsersAndDateAfterAndDateBefore(any(), any(), any())).thenReturn(userShifts);

        List<User> users = userService.getSickReplacement(shiftId);

        assertEquals(replacementUser.getId(), users.get(0).getId());
    }

    @Test
    void testGetSickReplacement_dayShiftAfterNightShift() {
        UUID shiftId = UUID.randomUUID();
        User sickUser = new User();
        sickUser.setId(UUID.randomUUID());
        Role role = new Role();
        sickUser.setRole(role);

        User replacementUser = new User();
        replacementUser.setId(UUID.randomUUID());
        replacementUser.setRole(role);
        replacementUser.setHolidays(Collections.emptyList());

        Shift shift = new Shift();
        shift.setUsers(Collections.singletonList(sickUser));
        shift.setDate(LocalDate.now());

        ShiftType dayShiftType = new ShiftType();
        dayShiftType.setStartTime(LocalTime.of(9, 0));
        dayShiftType.setEndTime(LocalTime.of(18, 0));

        ShiftType nightShiftType = new ShiftType();
        nightShiftType.setStartTime(LocalTime.of(18, 0));
        nightShiftType.setEndTime(LocalTime.of(6, 0));

        shift.setShiftType(nightShiftType);

        UserResource userResourceMock = mock(UserResource.class);
        UserRepresentation userRepresentation = new UserRepresentation();

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(userRepository.findAllByRole(role)).thenReturn(Arrays.asList(replacementUser, sickUser));
        when(usersResource.get(any())).thenReturn(userResourceMock);
        when(userResourceMock.toRepresentation()).thenReturn(userRepresentation);

        List<Shift> userShifts = new ArrayList<>();

        Shift userShift = new Shift();
        userShift.setDate(LocalDate.now().plusDays(1));

        userShift.setShiftType(dayShiftType);
        userShifts.add(userShift);


        when(shiftRepository.findAllByUsersAndDateAfterAndDateBefore(any(), any(), any())).thenReturn(userShifts);

        List<User> users = userService.getSickReplacement(shiftId);

        assertTrue(users.isEmpty());
    }

    @Test
    void testGetSickReplacement_nightShiftAfterDayShift() {
        UUID shiftId = UUID.randomUUID();
        User sickUser = new User();
        sickUser.setId(UUID.randomUUID());
        Role role = new Role();
        sickUser.setRole(role);

        User replacementUser = new User();
        replacementUser.setId(UUID.randomUUID());
        replacementUser.setRole(role);
        replacementUser.setHolidays(Collections.emptyList());

        Shift shift = new Shift();
        shift.setUsers(Collections.singletonList(sickUser));
        shift.setDate(LocalDate.now());

        ShiftType dayShiftType = new ShiftType();
        dayShiftType.setStartTime(LocalTime.of(9, 0));
        dayShiftType.setEndTime(LocalTime.of(18, 0));

        ShiftType nightShiftType = new ShiftType();
        nightShiftType.setStartTime(LocalTime.of(18, 0));
        nightShiftType.setEndTime(LocalTime.of(6, 0));

        shift.setShiftType(dayShiftType);

        UserResource userResourceMock = mock(UserResource.class);
        UserRepresentation userRepresentation = new UserRepresentation();

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(userRepository.findAllByRole(role)).thenReturn(Arrays.asList(replacementUser, sickUser));
        when(usersResource.get(any())).thenReturn(userResourceMock);
        when(userResourceMock.toRepresentation()).thenReturn(userRepresentation);

        List<Shift> userShifts = new ArrayList<>();

        Shift userShift = new Shift();
        userShift.setDate(LocalDate.now().plusDays(1));

        userShift.setShiftType(nightShiftType);
        userShifts.add(userShift);


        when(shiftRepository.findAllByUsersAndDateAfterAndDateBefore(any(), any(), any())).thenReturn(userShifts);

        List<User> users = userService.getSickReplacement(shiftId);

        assertEquals(replacementUser.getId(), users.get(0).getId());
    }

    @Test
    void testGetSickReplacement_dayShiftBeforeDayShift() {
        UUID shiftId = UUID.randomUUID();
        User sickUser = new User();
        sickUser.setId(UUID.randomUUID());
        Role role = new Role();
        sickUser.setRole(role);

        User replacementUser = new User();
        replacementUser.setId(UUID.randomUUID());
        replacementUser.setRole(role);
        replacementUser.setHolidays(Collections.emptyList());

        Shift shift = new Shift();
        shift.setUsers(Collections.singletonList(sickUser));
        shift.setDate(LocalDate.now());

        ShiftType dayShiftType = new ShiftType();
        dayShiftType.setStartTime(LocalTime.of(9, 0));
        dayShiftType.setEndTime(LocalTime.of(18, 0));
        shift.setShiftType(dayShiftType);


        UserResource userResourceMock = mock(UserResource.class);
        UserRepresentation userRepresentation = new UserRepresentation();

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(userRepository.findAllByRole(role)).thenReturn(Arrays.asList(replacementUser, sickUser));
        when(usersResource.get(any())).thenReturn(userResourceMock);
        when(userResourceMock.toRepresentation()).thenReturn(userRepresentation);

        List<Shift> userShifts = new ArrayList<>();

        Shift userShift = new Shift();
        userShift.setDate(LocalDate.now().minusDays(1));
        userShift.setShiftType(dayShiftType);
        userShifts.add(userShift);


        when(shiftRepository.findAllByUsersAndDateAfterAndDateBefore(any(), any(), any())).thenReturn(userShifts);

        List<User> users = userService.getSickReplacement(shiftId);

        assertEquals(replacementUser.getId(), users.get(0).getId());
    }

    @Test
    void testGetSickReplacement_nightShiftBeforeNightShift() {
        UUID shiftId = UUID.randomUUID();
        User sickUser = new User();
        sickUser.setId(UUID.randomUUID());
        Role role = new Role();
        sickUser.setRole(role);

        User replacementUser = new User();
        replacementUser.setId(UUID.randomUUID());
        replacementUser.setRole(role);
        replacementUser.setHolidays(Collections.emptyList());

        Shift shift = new Shift();
        shift.setUsers(Collections.singletonList(sickUser));
        shift.setDate(LocalDate.now());

        ShiftType nightShiftType = new ShiftType();
        nightShiftType.setStartTime(LocalTime.of(9, 0));
        nightShiftType.setEndTime(LocalTime.of(18, 0));
        shift.setShiftType(nightShiftType);


        UserResource userResourceMock = mock(UserResource.class);
        UserRepresentation userRepresentation = new UserRepresentation();

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(userRepository.findAllByRole(role)).thenReturn(Arrays.asList(replacementUser, sickUser));
        when(usersResource.get(any())).thenReturn(userResourceMock);
        when(userResourceMock.toRepresentation()).thenReturn(userRepresentation);

        List<Shift> userShifts = new ArrayList<>();

        Shift userShift = new Shift();
        userShift.setDate(LocalDate.now().minusDays(1));
        userShift.setShiftType(nightShiftType);
        userShifts.add(userShift);

        when(shiftRepository.findAllByUsersAndDateAfterAndDateBefore(any(), any(), any())).thenReturn(userShifts);

        List<User> users = userService.getSickReplacement(shiftId);

        assertEquals(replacementUser.getId(), users.get(0).getId());
    }

    @Test
    void testGetSickReplacement_dayShiftAfterDayShift() {
        UUID shiftId = UUID.randomUUID();
        User sickUser = new User();
        sickUser.setId(UUID.randomUUID());
        Role role = new Role();
        sickUser.setRole(role);

        User replacementUser = new User();
        replacementUser.setId(UUID.randomUUID());
        replacementUser.setRole(role);
        replacementUser.setHolidays(Collections.emptyList());

        Shift shift = new Shift();
        shift.setUsers(Collections.singletonList(sickUser));
        shift.setDate(LocalDate.now());

        ShiftType dayShiftType = new ShiftType();
        dayShiftType.setStartTime(LocalTime.of(9, 0));
        dayShiftType.setEndTime(LocalTime.of(18, 0));
        shift.setShiftType(dayShiftType);


        UserResource userResourceMock = mock(UserResource.class);
        UserRepresentation userRepresentation = new UserRepresentation();

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(userRepository.findAllByRole(role)).thenReturn(Arrays.asList(replacementUser, sickUser));
        when(usersResource.get(any())).thenReturn(userResourceMock);
        when(userResourceMock.toRepresentation()).thenReturn(userRepresentation);

        List<Shift> userShifts = new ArrayList<>();

        Shift userShift = new Shift();
        userShift.setDate(LocalDate.now().plusDays(1));
        userShift.setShiftType(dayShiftType);
        userShifts.add(userShift);


        when(shiftRepository.findAllByUsersAndDateAfterAndDateBefore(any(), any(), any())).thenReturn(userShifts);

        List<User> users = userService.getSickReplacement(shiftId);

        assertEquals(replacementUser.getId(), users.get(0).getId());
    }

    @Test
    void testGetSickReplacement_nightShiftAfterNightShift() {
        UUID shiftId = UUID.randomUUID();
        User sickUser = new User();
        sickUser.setId(UUID.randomUUID());
        Role role = new Role();
        sickUser.setRole(role);

        User replacementUser = new User();
        replacementUser.setId(UUID.randomUUID());
        replacementUser.setRole(role);
        replacementUser.setHolidays(Collections.emptyList());

        Shift shift = new Shift();
        shift.setUsers(Collections.singletonList(sickUser));
        shift.setDate(LocalDate.now());

        ShiftType nightShiftType = new ShiftType();
        nightShiftType.setStartTime(LocalTime.of(9, 0));
        nightShiftType.setEndTime(LocalTime.of(18, 0));
        shift.setShiftType(nightShiftType);


        UserResource userResourceMock = mock(UserResource.class);
        UserRepresentation userRepresentation = new UserRepresentation();

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(userRepository.findAllByRole(role)).thenReturn(Arrays.asList(replacementUser, sickUser));
        when(usersResource.get(any())).thenReturn(userResourceMock);
        when(userResourceMock.toRepresentation()).thenReturn(userRepresentation);

        List<Shift> userShifts = new ArrayList<>();

        Shift userShift = new Shift();
        userShift.setDate(LocalDate.now().plusDays(1));
        userShift.setShiftType(nightShiftType);
        userShifts.add(userShift);

        when(shiftRepository.findAllByUsersAndDateAfterAndDateBefore(any(), any(), any())).thenReturn(userShifts);

        List<User> users = userService.getSickReplacement(shiftId);

        assertEquals(replacementUser.getId(), users.get(0).getId());
    }
}
