package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.ShiftSwap;
import ase.meditrack.model.entity.ShiftSwapStatus;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.ShiftSwapRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ShiftSwapService {
    private final ShiftSwapRepository repository;
    private final UserService userService;

    public ShiftSwapService(ShiftSwapRepository repository, UserService userService) {

        this.repository = repository;
        this.userService = userService;
    }

    /**
     * Fetches all shift swaps from the database.
     *
     * @return List of all shift swaps
     */
    public List<ShiftSwap> findAll() {
        return repository.findAll();
    }

    /**
     * Fetches all shifts from the current month from a user from the database.
     *
     * @param principal is current user
     * @return List of all shift from a current month from a user
     */
    public List<ShiftSwap> findAllByCurrentMonth(Principal principal) {
        User user = userService.getPrincipalWithTeam(principal);
        LocalDate today = LocalDate.now();
        LocalDate nextMonth = today.plusMonths(1).withDayOfMonth(1);

        return repository.findAllBySwapRequestingUserIdAndRequestedShiftDateAfterAndRequestedShiftDateBefore(
                user.getId(), today, nextMonth);
    }

    /**
     * Fetches a shift swap by id from the database.
     *
     * @param id the id of the shift swap
     * @return the shift swap
     */
    public ShiftSwap findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Could not find shift swap with id: " + id + "!"));
    }

    /**
     * Creates a shift swap in the database. ShiftSwap status can be set as here, since these are the required values.
     *
     * @param shiftSwap the shift swap to create
     * @return the created shift swap
     */
    public ShiftSwap create(ShiftSwap shiftSwap) {
        shiftSwap.setRequestedShiftSwapStatus(ShiftSwapStatus.ACCEPTED);
        shiftSwap.setSuggestedShiftSwapStatus(ShiftSwapStatus.PENDING);
        return repository.save(shiftSwap);
    }

    /**
     * Updates a shift swap in the database.
     *
     * @param shiftSwap the shift swap to update
     * @return the updated shift swap
     */
    public ShiftSwap update(ShiftSwap shiftSwap) {
        ShiftSwap dbShiftSwap = findById(shiftSwap.getId());

        if (shiftSwap.getSwapRequestingUser() != null) {
            dbShiftSwap.setSwapRequestingUser(shiftSwap.getSwapRequestingUser());
        }
        if (shiftSwap.getSwapSuggestingUser() != null) {
            dbShiftSwap.setSwapSuggestingUser(shiftSwap.getSwapSuggestingUser());
        }
        if (shiftSwap.getRequestedShift() != null) {
            dbShiftSwap.setRequestedShift(shiftSwap.getRequestedShift());
        }
        if (shiftSwap.getSuggestedShift() != null) {
            dbShiftSwap.setSuggestedShift(shiftSwap.getSuggestedShift());
        }

        return repository.save(shiftSwap);
    }

    /**
     * Deletes a shift swap from the database.
     *
     * @param id the id of the shift swap to delete
     */
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
