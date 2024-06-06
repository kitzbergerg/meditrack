package ase.meditrack.service;

import ase.meditrack.model.entity.MonthlyWorkDetails;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.MonthlyWorkDetailsRepository;
import ase.meditrack.repository.ShiftRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
public class MonthlyWorkDetailsService {
    private final ShiftRepository shiftRepository;

    private MonthlyWorkDetailsRepository monthlyWorkDetailRepository;


    public MonthlyWorkDetailsService(MonthlyWorkDetailsRepository monthlyWorkDetailRepository,
                                     ShiftRepository shiftRepository) {
        this.monthlyWorkDetailRepository = monthlyWorkDetailRepository;
        this.shiftRepository = shiftRepository;
    }

    /**
     * Updates work details when a shift is updated.
     *
     * @param shift        that is updated
     * @param oldShiftType of the old shift, null if new shift is created
     */
    @Transactional
    public void updateMonthlyWorkDetailsForShift(Shift shift, ShiftType oldShiftType) {
        User user = shift.getUsers().get(0);  // Assuming shift has exactly one user

        LocalDate date = shift.getDate();

        log.info("month: " + date.getMonth().getValue());
        log.info("year: " + date.getYear());

        MonthlyWorkDetails monthlyWorkDetail = monthlyWorkDetailRepository.
                findMonthlyWorkDetailsByUserIdAndMonthAndYear(user.getId(), date.getMonth().getValue(),
                        date.getYear());

        // Calculate the duration of this single shift
        double shiftDuration = calculateShiftDuration(shift.getShiftType());
        double oldShiftDuration = 0;
        if (oldShiftType != null) {
            oldShiftDuration = calculateShiftDuration(oldShiftType);
        }

        // Update actual working hours based on the shift being added/updated
        double newActualHours = monthlyWorkDetail.getHoursActuallyWorked() - oldShiftDuration + shiftDuration;
        int overtime = (int) Math.round(newActualHours - monthlyWorkDetail.getHoursShouldWork());

        monthlyWorkDetail.setHoursActuallyWorked(Math.round(newActualHours));
        monthlyWorkDetail.setOvertime(overtime);
    }

    /**
     * Updates monthly work details given a deleted shift.
     *
     * @param shift that was deleted
     */
    @Transactional
    public void updateMonthlyWorkDetailsForDeletedShift(Shift shift) {
        User user = shift.getUsers().get(0);  // Assuming shift has exactly one user

        LocalDate date = shift.getDate();

        MonthlyWorkDetails monthlyWorkDetail = monthlyWorkDetailRepository.
                findMonthlyWorkDetailsByUserIdAndMonthAndYear(user.getId(), date.getMonth().getValue(),
                        date.getYear());

        // Calculate the duration of this single shift
        double shiftDuration = calculateShiftDuration(shift.getShiftType());

        // Update actual working hours based on the shift being deleted
        double newActualHours = monthlyWorkDetail.getHoursActuallyWorked() - shiftDuration;
        int overtime = (int) Math.round(newActualHours - monthlyWorkDetail.getHoursShouldWork());

        monthlyWorkDetail.setHoursActuallyWorked(Math.round(newActualHours));
        monthlyWorkDetail.setOvertime(overtime);
    }

    /**
     * Calculates the hours a user should work in a month.
     *
     * @param user  that the target hours are calculated for
     * @param team  of the user
     * @param month that the target hours should be calculated for
     * @param year  that the target hours should be calculated for
     * @return the hours the user should work in a month
     */
    public Float calculateTargetWorkingHours(User user, Team team, int month, int year) {
        int workingDaysInMonth = getWorkingDaysInMonth(month, year);
        float weeklyWorkingHours = team.getWorkingHours();
        float dailyWorkingHours = weeklyWorkingHours / 5; // Assuming a 5-day work week
        float targetWorkingHours = dailyWorkingHours * workingDaysInMonth * user.getWorkingHoursPercentage() / 100;
        return Math.round(targetWorkingHours * 2) / 2.0f;
    }

    /**
     * Gets the amount of working days in a month.
     *
     * @param month to get the working days for
     * @param year  to get the working days for
     * @return amount of working days in a month
     */
    public int getWorkingDaysInMonth(int month, int year) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
        int workingDays = 0;

        for (LocalDate date = firstDay; !date.isAfter(lastDay); date = date.plusDays(1)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                workingDays++;
            }
        }

        return workingDays;
    }

    /**
     * Calculates the working hours of a user, given the shifts the user works.
     *
     * @param user   that the working hours are calculated for
     * @param shifts that the user works in a month
     * @return length of the hours he works in a month
     */
    public Float calculateActualWorkingHours(User user, List<Shift> shifts) {
        return (float) shifts.stream()
                .filter(shift -> shift.getUsers().contains(user))
                .mapToDouble(shift -> calculateShiftDuration(shift.getShiftType()))
                .sum();
    }

    /**
     * Calculates shift duration of a shift type.
     *
     * @param shiftType the duration is calculated of
     * @return the length of the shift without the break
     */
    public double calculateShiftDuration(ShiftType shiftType) {
        LocalTime startTime = shiftType.getStartTime();
        LocalTime endTime = shiftType.getEndTime();
        LocalTime breakStartTime = shiftType.getBreakStartTime();
        LocalTime breakEndTime = shiftType.getBreakEndTime();

        Duration totalDuration;
        Duration breakDuration;

        // Calculate total shift duration considering overnight shifts
        if (endTime.isBefore(startTime)) {
            totalDuration = Duration.between(startTime, LocalTime.MAX).plus(Duration.between(LocalTime.MIN, endTime));
        } else {
            totalDuration = Duration.between(startTime, endTime);
        }

        // Calculate break duration considering overnight breaks
        if (breakEndTime.isBefore(breakStartTime)) {
            breakDuration = Duration.between(breakStartTime, LocalTime.MAX)
                    .plus(Duration.between(LocalTime.MIN, breakEndTime));
        } else {
            breakDuration = Duration.between(breakStartTime, breakEndTime);
        }

        totalDuration = roundToNearestMinute(totalDuration);
        breakDuration = roundToNearestMinute(breakDuration);

        // Calculate the net duration in minutes
        Duration netDuration = totalDuration.minus(breakDuration);

        // Convert the net duration to hours and round to two decimal places
        double durationInHours = netDuration.toMinutes() / 60.0;
        return Math.round(durationInHours * 100.0) / 100.0;
    }

    private static Duration roundToNearestMinute(Duration duration) {
        long seconds = duration.getSeconds();
        long roundedSeconds = Math.round(seconds / 60.0) * 60;
        return Duration.ofSeconds(roundedSeconds);
    }

}
