package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.ShiftRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ShiftService {
    private final ShiftRepository repository;
    private final UserService userService;

    public ShiftService(ShiftRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    /**
     * Fetches all shifts from the database.
     *
     * @return List of all shift
     */
    public List<Shift> findAll() {
        return repository.findAll();
    }

    /**
     * Fetches all shifts from the current month from a user from the database.
     *
     * @param principal is current user
     * @return List of all shift from a current month from a user
     */
    public List<Shift> findAllByCurrentMonth(Principal principal) {
        User user = userService.getPrincipalWithTeam(principal);
        LocalDate today = LocalDate.now();
        LocalDate nextMonth = today.plusMonths(1).withDayOfMonth(1);

        List<UUID> users = new ArrayList<>();
        users.add(user.getId());
        return repository.findAllByUsersAndDateAfterAndDateBefore(users, today, nextMonth);
    }


    /**
     * Fetches a shift by id from the database.
     *
     * @param id the id of the shift
     * @return the shift
     */
    public Shift findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Could not find shift with id: " + id + "!"));
    }

    /**
     * Creates a shift in the database.
     *
     * @param shift the shift to create
     * @return the created shift
     */
    public Shift create(Shift shift) {
        return repository.save(shift);
    }

    /**
     * Updates a shift in the database.
     *
     * @param shift the shift to update
     * @return the updated shift
     */
    public Shift update(Shift shift) {
        Shift dbShift = findById(shift.getId());

        if (shift.getDate() != null) {
            dbShift.setDate(shift.getDate());
        }
        if (shift.getMonthlyPlan() != null) {
            dbShift.setMonthlyPlan(shift.getMonthlyPlan());
        }
        if (shift.getShiftType() != null) {
            dbShift.setShiftType(shift.getShiftType());
        }
        if (shift.getUsers() != null) {
            dbShift.setUsers(shift.getUsers());
        }
        if (shift.getSuggestedShiftSwap() != null) {
            dbShift.setSuggestedShiftSwap(shift.getSuggestedShiftSwap());
        }
        if (shift.getRequestedShiftSwap() != null) {
            dbShift.setRequestedShiftSwap(shift.getRequestedShiftSwap());
        }

        return repository.save(dbShift);
    }

    /**
     * Deletes a shift from the database.
     *
     * @param id the id of the shift to delete
     */
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
