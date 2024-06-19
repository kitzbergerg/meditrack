package ase.meditrack.model;

import ase.meditrack.exception.ResourceConflictException;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.repository.ShiftRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ShiftValidator {
    private final ShiftRepository repository;

    public ShiftValidator(ShiftRepository repository) {
        this.repository = repository;
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
        validateDayShiftAfterNightShift(shift, repository.findAllByUsersAndDate(users, shift.getDate().minusDays(1)));
        validateNightShiftBeforeDayShift(shift, repository.findAllByUsersAndDate(users, shift.getDate().plusDays(1)));

    }

    /**
     * Validates shift for shift swap.
     *
     * @param shift to validate
     * @param allShifts array to check conflicts
     */
    public void validateShiftWithCustomData(Shift shift, List<Shift> allShifts) {

        validateSingleShiftPerDay(shift, filterShiftsByDate(allShifts, shift.getDate()));
        validateDayShiftAfterNightShift(shift, filterShiftsByDate(allShifts, shift.getDate().minusDays(1)));
        validateNightShiftBeforeDayShift(shift, filterShiftsByDate(allShifts, shift.getDate().plusDays(1)));
    }


    private void validateSingleShiftPerDay(Shift shift, List<Shift> sameDayShifts) {
        if (!sameDayShifts.isEmpty() && sameDayShifts.stream().anyMatch(existingShift ->
                !existingShift.getId().equals(shift.getId()))) {
            throw new ResourceConflictException("There can only be one shift per day.");
        }
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
