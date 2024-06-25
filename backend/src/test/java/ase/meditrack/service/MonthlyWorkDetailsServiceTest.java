package ase.meditrack.service;

import ase.meditrack.model.entity.*;
import ase.meditrack.repository.MonthlyWorkDetailsRepository;
import ase.meditrack.repository.ShiftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MonthlyWorkDetailsServiceTest {

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private MonthlyWorkDetailsRepository monthlyWorkDetailsRepository;

    @InjectMocks
    private MonthlyWorkDetailsService monthlyWorkDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateShiftDuration() {
        ShiftType shiftType = new ShiftType();
        shiftType.setStartTime(LocalTime.of(9, 0));
        shiftType.setEndTime(LocalTime.of(17, 0));
        shiftType.setBreakStartTime(LocalTime.of(12, 0));
        shiftType.setBreakEndTime(LocalTime.of(13, 0));

        double duration = monthlyWorkDetailsService.calculateShiftDuration(shiftType);

        assertEquals(7.0, duration, 0.01);
    }

    @Test
    void testCalculateTargetWorkingHours() {
        User user = new User();
        user.setWorkingHoursPercentage(100F);

        Team team = new Team();

        Float targetHours = monthlyWorkDetailsService.calculateTargetWorkingHours(user, team, 6, 2023);

        // June 2023 has 22 working days, 22 * 8 = 176 hours
        assertEquals(176F, targetHours);
    }

    @Test
    void testGetWorkingDaysInMonth() {
        int workingDays = monthlyWorkDetailsService.getWorkingDaysInMonth(6, 2023);

        assertEquals(22, workingDays); // June 2023 has 22 working days
    }

    @Test
    void testCalculateActualWorkingHours() {
        User user = new User();
        user.setId(UUID.randomUUID());

        ShiftType shiftType = new ShiftType();
        shiftType.setStartTime(LocalTime.of(9, 0));
        shiftType.setEndTime(LocalTime.of(17, 0));
        shiftType.setBreakStartTime(LocalTime.of(12, 0));
        shiftType.setBreakEndTime(LocalTime.of(13, 0));

        Shift shift = new Shift();
        shift.setUsers(Collections.singletonList(user));
        shift.setShiftType(shiftType);

        List<Shift> shifts = Collections.singletonList(shift);

        Float actualHours = monthlyWorkDetailsService.calculateActualWorkingHours(user, shifts);

        assertEquals(7F, actualHours);
    }

    @Test
    void testUpdateMonthlyWorkDetailsForShift() {
        User user = new User();
        user.setId(UUID.randomUUID());

        ShiftType oldShiftType = new ShiftType();
        oldShiftType.setStartTime(LocalTime.of(9, 0));
        oldShiftType.setEndTime(LocalTime.of(17, 0));
        oldShiftType.setBreakStartTime(LocalTime.of(12, 0));
        oldShiftType.setBreakEndTime(LocalTime.of(13, 0));

        ShiftType newShiftType = new ShiftType();
        newShiftType.setStartTime(LocalTime.of(10, 0));
        newShiftType.setEndTime(LocalTime.of(19, 0));
        newShiftType.setBreakStartTime(LocalTime.of(13, 0));
        newShiftType.setBreakEndTime(LocalTime.of(14, 0));

        Shift shift = new Shift();
        shift.setUsers(Collections.singletonList(user));
        shift.setShiftType(newShiftType);
        shift.setDate(LocalDate.of(2023, 6, 15));

        MonthlyWorkDetails monthlyWorkDetails = mock(MonthlyWorkDetails.class);
        when(monthlyWorkDetails.getHoursActuallyWorked()).thenReturn(100.0f);
        when(monthlyWorkDetails.getHoursShouldWork()).thenReturn(160.0f);
        when(monthlyWorkDetailsRepository.findMonthlyWorkDetailsByUserIdAndMonthAndYear(any(), anyInt(), anyInt()))
                .thenReturn(monthlyWorkDetails);

        monthlyWorkDetailsService.updateMonthlyWorkDetailsForShift(shift, oldShiftType);

        verify(monthlyWorkDetails).setHoursActuallyWorked(101.0f);
        verify(monthlyWorkDetails).setOvertime(-59);
    }

    @Test
    void testNightShiftUpdateMonthlyWorkDetailsForShift() {
        User user = new User();
        user.setId(UUID.randomUUID());

        ShiftType oldShiftType = new ShiftType();
        oldShiftType.setStartTime(LocalTime.of(9, 0));
        oldShiftType.setEndTime(LocalTime.of(17, 0));
        oldShiftType.setBreakStartTime(LocalTime.of(12, 0));
        oldShiftType.setBreakEndTime(LocalTime.of(13, 0));

        ShiftType newShiftType = new ShiftType();
        newShiftType.setStartTime(LocalTime.of(20, 0));
        newShiftType.setEndTime(LocalTime.of(5, 0));
        newShiftType.setBreakStartTime(LocalTime.of(23, 30));
        newShiftType.setBreakEndTime(LocalTime.of(0, 30));

        Shift shift = new Shift();
        shift.setUsers(Collections.singletonList(user));
        shift.setShiftType(newShiftType);
        shift.setDate(LocalDate.of(2023, 6, 15));

        MonthlyWorkDetails monthlyWorkDetails = mock(MonthlyWorkDetails.class);
        when(monthlyWorkDetails.getHoursActuallyWorked()).thenReturn(100.0f);
        when(monthlyWorkDetails.getHoursShouldWork()).thenReturn(160.0f);
        when(monthlyWorkDetailsRepository.findMonthlyWorkDetailsByUserIdAndMonthAndYear(any(), anyInt(), anyInt()))
                .thenReturn(monthlyWorkDetails);

        monthlyWorkDetailsService.updateMonthlyWorkDetailsForShift(shift, oldShiftType);

        verify(monthlyWorkDetails).setHoursActuallyWorked(101.0f);
        verify(monthlyWorkDetails).setOvertime(-59);
    }

    @Test
    void testUpdateMonthlyWorkDetailsForDeletedShift() {
        User user = new User();
        user.setId(UUID.randomUUID());

        ShiftType shiftType = new ShiftType();
        shiftType.setStartTime(LocalTime.of(9, 0));
        shiftType.setEndTime(LocalTime.of(17, 0));
        shiftType.setBreakStartTime(LocalTime.of(12, 0));
        shiftType.setBreakEndTime(LocalTime.of(13, 0));

        Shift shift = new Shift();
        shift.setUsers(Collections.singletonList(user));
        shift.setShiftType(shiftType);
        shift.setDate(LocalDate.of(2023, 6, 15));

        MonthlyWorkDetails monthlyWorkDetails = mock(MonthlyWorkDetails.class);
        when(monthlyWorkDetailsRepository.findMonthlyWorkDetailsByUserIdAndMonthAndYear(any(), anyInt(), anyInt()))
                .thenReturn(monthlyWorkDetails);

        monthlyWorkDetailsService.updateMonthlyWorkDetailsForDeletedShift(shift);

        verify(monthlyWorkDetails).setHoursActuallyWorked(-7f);
        verify(monthlyWorkDetails).setOvertime(-7);
    }
}
