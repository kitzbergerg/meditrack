package ase.meditrack.algorithm;

import ase.meditrack.model.entity.User;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.Preferences;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.Team;

import ase.meditrack.service.algorithm.AlgorithmInput;
import ase.meditrack.service.algorithm.AlgorithmMapper;
import ase.meditrack.service.algorithm.AlgorithmOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AlgorithmMapperTest {

    private AlgorithmMapper algorithmMapper;

    @BeforeEach
    void setUp() {
        algorithmMapper = new AlgorithmMapper();
    }

    @Test
    void testShiftTypeAndEmployeeMappingsInMapToAlgorithmInput() throws Exception {
        int month = 5;
        int year = 2024;

        List<User> employees = new ArrayList<>();
        List<ShiftType> shiftTypes = new ArrayList<>();
        List<Role> roles = new ArrayList<>();
        Team team = mock(Team.class);

        // Create mock data
        Preferences preferences = new Preferences();
        preferences.setOffDays(List.of());
        User user = mock(User.class,  Mockito.RETURNS_DEEP_STUBS);
        UUID userUuid = UUID.randomUUID();
        when(user.getId()).thenReturn(userUuid);
        when(user.getWorkingHoursPercentage()).thenReturn(1.0f);
        when(user.getCurrentOverTime()).thenReturn(0);
        when(user.getCanWorkShiftTypes()).thenReturn(shiftTypes);
        when(user.getPreferences()).thenReturn(preferences);
        when(user.getRole().getId()).thenReturn(null);
        when(user.getRole().getAllowedFlextimeTotal()).thenReturn(10);
        when(user.getRole().getAllowedFlextimePerMonth()).thenReturn(10);
        when(user.getRole().getWorkingHours()).thenReturn(40);
        when(user.getRole().getMaxWeeklyHours()).thenReturn(80);
        when(user.getRole().getMaxConsecutiveShifts()).thenReturn(3);
        when(user.getRole().getWorkingHours()).thenReturn(40);
        employees.add(user);

        ShiftType shiftType = mock(ShiftType.class);
        UUID shiftTypeUuid = UUID.randomUUID();
        when(shiftType.getId()).thenReturn(shiftTypeUuid);
        when(shiftType.getStartTime()).thenReturn(LocalTime.of(9, 0));
        when(shiftType.getEndTime()).thenReturn(LocalTime.of(17, 0));
        shiftTypes.add(shiftType);

        algorithmMapper.mapToAlgorithmInput(month, year, employees, Map.of(user.getId(), List.of()), shiftTypes, roles,
                team, List.of());

        // Use reflection to access private fields
        Field indexToShiftTypeUuidField = AlgorithmMapper.class.getDeclaredField("indexToShiftTypeUuid");
        indexToShiftTypeUuidField.setAccessible(true);
        Map<Integer, UUID> indexToShiftTypeUuid = (Map<Integer, UUID>) indexToShiftTypeUuidField.get(algorithmMapper);

        Field indexToEmployeeUuidField = AlgorithmMapper.class.getDeclaredField("indexToEmployeeUuid");
        indexToEmployeeUuidField.setAccessible(true);
        Map<Integer, UUID> indexToEmployeeUuid = (Map<Integer, UUID>) indexToEmployeeUuidField.get(algorithmMapper);

        assertEquals(1, indexToShiftTypeUuid.size());
        assertEquals(shiftTypeUuid, indexToShiftTypeUuid.values().iterator().next());

        assertEquals(1, indexToEmployeeUuid.size());
        assertEquals(userUuid, indexToEmployeeUuid.values().iterator().next());
    }

    @Test
    void testMapToAlgorithmInput() {
        int month = 5;
        int year = 2024;

        List<User> employees = new ArrayList<>();
        List<ShiftType> shiftTypes = new ArrayList<>();
        List<ShiftType> shiftTypes2 = new ArrayList<>();
        List<Role> roles = new ArrayList<>();
        Team team = mock(Team.class);

        // Create mock data
        Preferences preferences = new Preferences();
        preferences.setOffDays(List.of());
        User user = mock(User.class, Mockito.RETURNS_DEEP_STUBS);
        when(user.getId()).thenReturn(UUID.randomUUID());
        when(user.getWorkingHoursPercentage()).thenReturn(1.0f);
        when(user.getCurrentOverTime()).thenReturn(0);
        when(user.getCanWorkShiftTypes()).thenReturn(shiftTypes);
        when(user.getPreferences()).thenReturn(preferences);
        when(user.getRole().getId()).thenReturn(null);
        when(user.getRole().getAllowedFlextimeTotal()).thenReturn(10);
        when(user.getRole().getAllowedFlextimePerMonth()).thenReturn(10);
        employees.add(user);

        User user2 = mock(User.class, Mockito.RETURNS_DEEP_STUBS);
        when(user2.getId()).thenReturn(UUID.randomUUID());
        when(user2.getWorkingHoursPercentage()).thenReturn(1.0f);
        when(user2.getCurrentOverTime()).thenReturn(0);
        when(user2.getCanWorkShiftTypes()).thenReturn(shiftTypes2);
        when(user2.getPreferences()).thenReturn(preferences);
        when(user2.getRole().getId()).thenReturn(null);
        when(user2.getRole().getAllowedFlextimeTotal()).thenReturn(10);
        when(user2.getRole().getAllowedFlextimePerMonth()).thenReturn(10);
        employees.add(user2);

        ShiftType shiftType = mock(ShiftType.class);
        when(shiftType.getId()).thenReturn(UUID.randomUUID());
        when(shiftType.getStartTime()).thenReturn(LocalTime.of(9, 0));
        when(shiftType.getEndTime()).thenReturn(LocalTime.of(17, 0));
        shiftTypes.add(shiftType);
        shiftTypes2.add(shiftType);

        ShiftType shiftType2 = mock(ShiftType.class);
        when(shiftType2.getId()).thenReturn(UUID.randomUUID());
        when(shiftType2.getStartTime()).thenReturn(LocalTime.of(8, 0));
        when(shiftType2.getEndTime()).thenReturn(LocalTime.of(16, 0));
        shiftTypes.add(shiftType2);

        Role role = mock(Role.class);
        when(role.getId()).thenReturn(UUID.randomUUID());
        when(role.getName()).thenReturn("Doctor");
        roles.add(role);

        Role role2 = mock(Role.class);
        when(role2.getId()).thenReturn(UUID.randomUUID());
        when(role2.getName()).thenReturn("Nurse");
        roles.add(role2);


        AlgorithmInput input = algorithmMapper.mapToAlgorithmInput(
                month,
                year,
                employees,
                Map.of(user.getId(), List.of(), user2.getId(), List.of()),
                shiftTypes,
                roles,
                team,
                List.of()
        );


        assertEquals(2, input.employees().size());
        assertEquals(Arrays.asList(0, 1), input.employees().get(0).worksShiftTypes());
        assertEquals(List.of(0), input.employees().get(1).worksShiftTypes());

        assertEquals(2, input.shiftTypes().size());
        assertEquals(LocalTime.of(9, 0), input.shiftTypes().get(0).startTime());
        assertEquals(LocalTime.of(17, 0), input.shiftTypes().get(0).endTime());
        assertEquals(8, input.shiftTypes().get(0).duration());

        assertEquals(LocalTime.of(8, 0), input.shiftTypes().get(1).startTime());
        assertEquals(LocalTime.of(16, 0), input.shiftTypes().get(1).endTime());
        assertEquals(8, input.shiftTypes().get(1).duration());

        assertEquals(2, input.roles().size());
        assertEquals("Doctor", input.roles().get(0).name());
        assertEquals("Nurse", input.roles().get(1).name());
    }

    @Test
    void testMapFromAlgorithmOutput() throws Exception {
        AlgorithmOutput output = mock(AlgorithmOutput.class);
        List<ShiftType> shiftTypes = new ArrayList<>();
        List<User> users = new ArrayList<>();
        MonthlyPlan monthlyPlan = mock(MonthlyPlan.class);
        int month = 5;
        int year = 2024;

        // Create mock data
        User user = mock(User.class);
        UUID userUuid = UUID.randomUUID();
        when(user.getId()).thenReturn(userUuid);
        users.add(user);

        ShiftType shiftType = mock(ShiftType.class);
        UUID shiftTypeUuid = UUID.randomUUID();
        when(shiftType.getId()).thenReturn(shiftTypeUuid);
        shiftTypes.add(shiftType);

        // Set private fields
        Field indexToShiftTypeUuidField = AlgorithmMapper.class.getDeclaredField("indexToShiftTypeUuid");
        indexToShiftTypeUuidField.setAccessible(true);
        Map<Integer, UUID> indexToShiftTypeUuid = new HashMap<>();
        indexToShiftTypeUuid.put(0, shiftTypeUuid);
        indexToShiftTypeUuidField.set(algorithmMapper, indexToShiftTypeUuid);

        Field indexToEmployeeUuidField = AlgorithmMapper.class.getDeclaredField("indexToEmployeeUuid");
        indexToEmployeeUuidField.setAccessible(true);
        Map<Integer, UUID> indexToEmployeeUuid = new HashMap<>();
        indexToEmployeeUuid.put(0, userUuid);
        indexToEmployeeUuidField.set(algorithmMapper, indexToEmployeeUuid);

        HashMap<Integer, List<AlgorithmOutput.ShiftTypeDayPair>> assignments = new HashMap<>();
        List<AlgorithmOutput.ShiftTypeDayPair> shiftPairs = new ArrayList<>();
        shiftPairs.add(new AlgorithmOutput.ShiftTypeDayPair(0, 0));
        assignments.put(0, shiftPairs);

        when(output.assignmentOfEmployeesToShifts()).thenReturn(assignments);

        List<Shift> shifts =
                algorithmMapper.mapFromAlgorithmOutput(output, shiftTypes, users, monthlyPlan, month, year);

        assertNotNull(shifts);
        assertEquals(1, shifts.size());
        Shift shift = shifts.get(0);
        assertEquals(shiftType, shift.getShiftType());
        assertEquals(1, shift.getUsers().size());
        assertEquals(user, shift.getUsers().get(0));
        assertEquals(monthlyPlan, shift.getMonthlyPlan());
        assertEquals(LocalDate.of(year, month, 1), shift.getDate());
    }
}
