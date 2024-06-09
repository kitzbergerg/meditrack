package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.exception.ResourceConflictException;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.MonthlyPlanRepository;
import ase.meditrack.repository.ShiftRepository;
import ase.meditrack.repository.ShiftTypeRepository;
import ase.meditrack.validator.ShiftValidator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ShiftService {
    private final ShiftRepository repository;
    private final UserService userService;
    private final ShiftTypeRepository shiftTypeRepository;

    private final MonthlyWorkDetailsService monthlyWorkDetailsService;
    private final MonthlyPlanRepository monthlyPlanRepository;
    private final ShiftValidator shiftValidator;

    public ShiftService(ShiftRepository repository, MonthlyWorkDetailsService monthlyWorkDetailsService,
                        ShiftTypeRepository shiftTypeRepository, MonthlyPlanRepository monthlyPlanRepository,
                        UserService userService, ShiftValidator shiftValidator) {
        this.repository = repository;
        this.monthlyWorkDetailsService = monthlyWorkDetailsService;
        this.shiftTypeRepository = shiftTypeRepository;
        this.monthlyPlanRepository = monthlyPlanRepository;
        this.userService = userService;
        this.shiftValidator = shiftValidator;
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
    @Transactional
    public Shift create(Shift shift) {
        Optional<ShiftType> type = shiftTypeRepository.findById(shift.getShiftType().getId());
        Optional<MonthlyPlan> plan = monthlyPlanRepository.findById(shift.getMonthlyPlan().getId());
        if (type.isEmpty() || plan.isEmpty()) {
            throw new NotFoundException("Could not find shift type or plan of shift!");
        }
        shift.setShiftType(type.get());
        shift.setMonthlyPlan(plan.get());
        validateShiftSequence(shift, type.get());
        shiftValidator.validateShift(shift);
        Shift createdShift = repository.save(shift);
        monthlyWorkDetailsService.updateMonthlyWorkDetailsForShift(createdShift, null);
        return createdShift;
    }

    /**
     * Updates a shift in the database.
     *
     * @param shift the shift to update
     * @return the updated shift
     */
    public Shift update(Shift shift) {
        Shift dbShift = findById(shift.getId());
        Optional<ShiftType> oldShiftType = shiftTypeRepository.findById(dbShift.getShiftType().getId());
        Optional<ShiftType> newShiftType = shiftTypeRepository.findById(shift.getShiftType().getId());
        Optional<MonthlyPlan> plan = monthlyPlanRepository.findById(shift.getMonthlyPlan().getId());
        if (newShiftType.isEmpty() || oldShiftType.isEmpty() || plan.isEmpty()) {
            throw new NotFoundException("Could not find shift type or plan of shift!");
        }
        validateShiftSequence(shift, newShiftType.get());
        shift.setShiftType(newShiftType.get());
        shift.setMonthlyPlan(plan.get());
        shiftValidator.validateShift(shift);
        Shift createdShift = repository.save(shift);
        monthlyWorkDetailsService.updateMonthlyWorkDetailsForShift(createdShift, oldShiftType.get());

        if (shift.getDate() != null) {
            dbShift.setDate(shift.getDate());
        }
        if (shift.getMonthlyPlan() != null) {
            dbShift.setMonthlyPlan(shift.getMonthlyPlan());
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

        return repository.save(createdShift);
    }

    /**
     * Deletes a shift from the database.
     *
     * @param id the id of the shift to delete
     */
    public void delete(UUID id) {
        Optional<Shift> shift = repository.findById(id);
        if (shift.isEmpty()) {
            throw new NotFoundException("Could not find shift to delete!");
        }

        Optional<ShiftType> newShiftType = shiftTypeRepository.findById(shift.get().getShiftType().getId());
        Optional<MonthlyPlan> plan = monthlyPlanRepository.findById(shift.get().getMonthlyPlan().getId());
        if (newShiftType.isEmpty() || plan.isEmpty()) {
            throw new NotFoundException("Could not find shift type or plan of shift!");
        }

        monthlyWorkDetailsService.updateMonthlyWorkDetailsForDeletedShift(shift.get());
        repository.deleteById(id);
    }
}
