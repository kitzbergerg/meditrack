package ase.meditrack.model;

import ase.meditrack.exception.ResourceConflictException;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.repository.ShiftRepository;
import ase.meditrack.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ShiftValidator {
    private static final int MINIMUM_REST_PERIOD = 11;
    private final ShiftRepository repository;
    private final UserService userService;

    public ShiftValidator(ShiftRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    /**
     * Validates the shift.
     *
     * @param shift to validate
     */
    public void validateShift(Shift shift) {
        if (shift.getShiftType() == null || shift.getDate() == null || shift.getUsers() == null) {
            return;
        }

        List<UUID> users = new ArrayList<>();
        users.add(shift.getUsers().get(0).getId());

        validateSingleShiftPerDay(shift, repository.findAllByUsersAndDate(users, shift.getDate()));
        validateHasSufficientRest(shift, repository.findAllByUsersAndDate(users,
                        shift.getDate().minusDays(1)),
                repository.findAllByUsersAndDate(users, shift.getDate().plusDays(1)), shift.getDate());
        validateMaxWorkingHoursPerWeek(shift);
        validateMaxConsecutiveShifts(shift);
    }

    /**
     * Validates shift for shift swap.
     *
     * @param shift to validate
     * @param allShifts array to check conflicts
     */
    public void validateShiftWithCustomData(Shift shift, List<Shift> allShifts) {

        validateSingleShiftPerDay(shift, filterShiftsByDate(allShifts, shift.getDate()));
        validateHasSufficientRest(shift, filterShiftsByDate(allShifts, shift.getDate().minusDays(1)),
                filterShiftsByDate(allShifts, shift.getDate().plusDays(1)), shift.getDate());
        validateMaxWorkingHoursPerWeek(shift);
        validateMaxConsecutiveShifts(shift);
    }


    private void validateSingleShiftPerDay(Shift shift, List<Shift> sameDayShifts) {
        if (!sameDayShifts.isEmpty() && sameDayShifts.stream().anyMatch(existingShift ->
                !existingShift.getId().equals(shift.getId()))) {
            throw new ResourceConflictException("There can only be one shift per day.");
        }
    }

    private void validateMaxConsecutiveShifts(Shift shift) {
        LocalDate shiftDate = shift.getDate();
        int maxConsecutiveShifts = userService.findById(shift.getUsers().get(0).getId())
                .getRole().getMaxConsecutiveShifts();
        LocalDate startDate = shiftDate.minusDays(maxConsecutiveShifts);
        LocalDate endDate = shiftDate.plusDays(maxConsecutiveShifts);

        List<Shift> userShifts = repository.findAllByUsersAndDateAfterAndDateBefore(
                List.of(shift.getUsers().get(0).getId()), startDate.minusDays(1),
                endDate.plusDays(1));

        int consecutiveShifts = 0;

        for (LocalDate date = startDate; !date.isAfter(endDate);
             date = date.plusDays(1)) {
            LocalDate finalDate = date;
            boolean hasShift = userShifts.stream().anyMatch(s -> s.getDate().equals(finalDate));
            // Log if the user has a shift on the current date
            if (date.equals(shiftDate) || hasShift) {
                consecutiveShifts++;
            } else {
                consecutiveShifts = 0;
            }
            if (consecutiveShifts > maxConsecutiveShifts) {
                throw new ResourceConflictException("Max consecutive shifts exceeded.");
            }
        }
    }

    private void validateHasSufficientRest(Shift shift, List<Shift> previousShifts, List<Shift> nextShifts,
                                           LocalDate shiftDate) {
        LocalTime shiftStartTime = shift.getShiftType().getStartTime();
        LocalTime shiftEndTime = shift.getShiftType().getEndTime();
        LocalDateTime currentShiftStartDateTime = LocalDateTime.of(shiftDate, shiftStartTime);
        LocalDateTime currentShiftEndDateTime = shiftEndTime.isBefore(shiftStartTime)
                ? LocalDateTime.of(shiftDate.plusDays(1), shiftEndTime)
                : LocalDateTime.of(shiftDate, shiftEndTime);

        boolean hasSufficientRestBefore = previousShifts.stream()
                .filter(s -> s.getDate().equals(shiftDate.minusDays(1)))
                .findAny()
                .map(s -> {
                    LocalDate previousShiftDate = s.getDate();
                    LocalTime previousShiftEndTime = s.getShiftType().getEndTime();
                    LocalDateTime previousShiftEndDateTime
                            = previousShiftEndTime.isBefore(s.getShiftType().getStartTime())
                            ? LocalDateTime.of(previousShiftDate.plusDays(1), previousShiftEndTime)
                            : LocalDateTime.of(previousShiftDate, previousShiftEndTime);

                    Duration restDuration = Duration.between(previousShiftEndDateTime, currentShiftStartDateTime);
                    return !restDuration.isNegative()
                            && restDuration.compareTo(Duration.ofHours(MINIMUM_REST_PERIOD)) >= 0;
                })
                .orElse(true);

        boolean hasSufficientRestAfter = nextShifts.stream()
                .filter(s -> s.getDate().equals(shiftDate.plusDays(1)))
                .findAny()
                .map(s -> {
                    LocalDate nextShiftDate = s.getDate();
                    LocalTime nextShiftStartTime = s.getShiftType().getStartTime();
                    LocalDateTime nextShiftStartDateTime = LocalDateTime.of(nextShiftDate, nextShiftStartTime);

                    Duration restDuration = Duration.between(currentShiftEndDateTime, nextShiftStartDateTime);
                    return !restDuration.isNegative()
                            && restDuration.compareTo(Duration.ofHours(MINIMUM_REST_PERIOD)) >= 0;
                })
                .orElse(true);

        if (!hasSufficientRestBefore) {
            throw new ResourceConflictException("There must be at least 11 hours of rest before the shift.");
        }
        if (!hasSufficientRestAfter) {
            throw new ResourceConflictException("There must be at least 11 hours of rest after the shift.");
        }
    }

    private void validateMaxWorkingHoursPerWeek(Shift shift) {
        LocalDate shiftDate = shift.getDate();
        int dayOfMonth = shiftDate.getDayOfMonth();
        int weekStart = (dayOfMonth - 1) / 7 * 7 + 1;
        LocalDate weekStartDate = shiftDate.withDayOfMonth(weekStart);
        LocalDate weekEndDate = weekStartDate.plusDays(6);

        List<Shift> weekShifts = repository.findAllByUsersAndDateAfterAndDateBefore(
                Collections.singletonList(shift.getUsers().get(0).getId()), weekStartDate.minusDays(1),
                weekEndDate.plusDays(1));

        long totalWorkingHours = weekShifts.stream()
                .mapToLong(this::calculateShiftDuration)
                .sum();

        long newShiftHours = calculateShiftDuration(shift);

        int maxWeeklyHours = userService.findById(shift.getUsers().get(0).getId()).getRole().getMaxWeeklyHours();
        if ((totalWorkingHours + newShiftHours) > maxWeeklyHours) {
            throw new ResourceConflictException("Max working hours per week exceeded.");
        }
    }

    private long calculateShiftDuration(Shift shift) {
        LocalDate shiftDate = shift.getDate();
        LocalTime shiftStartTime = shift.getShiftType().getStartTime();
        LocalTime shiftEndTime = shift.getShiftType().getEndTime();

        LocalDateTime shiftStartDateTime = LocalDateTime.of(shiftDate, shiftStartTime);
        LocalDateTime shiftEndDateTime = shiftEndTime.isBefore(shiftStartTime)
                ? LocalDateTime.of(shiftDate.plusDays(1), shiftEndTime)
                : LocalDateTime.of(shiftDate, shiftEndTime);

        return Duration.between(shiftStartDateTime, shiftEndDateTime).toHours();
    }

    private void validateDayShiftAfterNightShift(Shift shift, List<Shift> previousShifts) {
        LocalTime dayStart = LocalTime.of(8, 0);
        LocalTime nightStart = LocalTime.of(20, 0);

        LocalTime shiftStartTime = shift.getShiftType().getStartTime();

        boolean isDayShift = !shiftStartTime.isBefore(dayStart) && shiftStartTime.isBefore(nightStart);

        if (isDayShift) {
            for (Shift previousShift : previousShifts) {
                LocalTime previousShiftStartTime = previousShift.getShiftType().getStartTime();

                boolean isPreviousNightShift = previousShiftStartTime.isBefore(dayStart)
                        || !previousShiftStartTime.isBefore(nightStart);

                if (isPreviousNightShift) {
                    throw new ResourceConflictException("A day shift cannot follow a night shift.");
                }
            }
        }
    }

    private void validateNightShiftBeforeDayShift(Shift shift, List<Shift> nextShifts) {
        LocalTime dayStart = LocalTime.of(8, 0);
        LocalTime nightStart = LocalTime.of(20, 0);

        LocalTime shiftStartTime = shift.getShiftType().getStartTime();

        boolean isNightShift = shiftStartTime.isBefore(dayStart) || !shiftStartTime.isBefore(nightStart);

        if (isNightShift) {
            for (Shift nextShift : nextShifts) {
                LocalTime nextShiftStartTime = nextShift.getShiftType().getStartTime();
                boolean isNextDayShift = !nextShiftStartTime.isBefore(dayStart)
                        && nextShiftStartTime.isBefore(nightStart);

                if (isNextDayShift) {
                    throw new ResourceConflictException("A night shift cannot precede a day shift.");
                }
            }
        }
    }

    private List<Shift> filterShiftsByDate(List<Shift> shifts, LocalDate date) {
        return shifts.stream()
                .filter(shift -> shift.getDate().equals(date))
                .collect(Collectors.toList());
    }

}
