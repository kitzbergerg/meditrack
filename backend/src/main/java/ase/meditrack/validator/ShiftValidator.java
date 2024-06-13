package ase.meditrack.validator;

import ase.meditrack.exception.ResourceConflictException;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.repository.ShiftRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        ShiftType shiftType = shift.getShiftType();

        // Ensure there is only one shift per day
        List<Shift> sameDayShifts = repository.findAllByUsersAndDate(users, shift.getDate());
        if (!sameDayShifts.isEmpty() && sameDayShifts.stream().anyMatch(existingShift ->
                !existingShift.getId().equals(shift.getId()))) {
            throw new ResourceConflictException("There can only be one shift per day.");
        }

        // Check the previous day for a new day shift
        if ("day".equalsIgnoreCase(shiftType.getType())) {
            LocalDate previousDay = shift.getDate().minusDays(1);
            List<Shift> previousShifts = repository.findAllByUsersAndDate(users, previousDay);
            for (Shift previousShift : previousShifts) {
                if ("night".equalsIgnoreCase(previousShift.getShiftType().getType())) {
                    throw new ResourceConflictException("A day shift cannot follow a night shift.");
                }
            }
        }

        // Check the next day for a new night shift
        if ("night".equalsIgnoreCase(shiftType.getType())) {
            LocalDate nextDay = shift.getDate().plusDays(1);
            List<Shift> nextShifts = repository.findAllByUsersAndDate(users, nextDay);
            for (Shift nextShift : nextShifts) {
                if ("day".equalsIgnoreCase(nextShift.getShiftType().getType())) {
                    throw new ResourceConflictException("A night shift cannot precede a day shift.");
                }
            }
        }
    }
}
