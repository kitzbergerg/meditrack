package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftSwap;
import ase.meditrack.model.entity.ShiftSwapStatus;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.ShiftRepository;
import ase.meditrack.repository.ShiftSwapRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ShiftSwapService {
    private final ShiftSwapRepository repository;
    private final ShiftRepository shiftRepository;
    private final UserService userService;

    public ShiftSwapService(ShiftSwapRepository repository, UserService userService, ShiftRepository shiftRepository) {

        this.repository = repository;
        this.shiftRepository = shiftRepository;
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
     * Fetches all shift swap offers from the current month from one team
     * from user with the same role from the database.
     *
     * @param principal is current user
     * @return List of all shift swap offers from other
     */
    public List<ShiftSwap> findAllOffersByCurrentMonth(Principal principal) {
        User user = userService.getPrincipalWithTeam(principal);
        LocalDate today = LocalDate.now();
        LocalDate nextMonth = today.plusMonths(1).withDayOfMonth(1);

        List<ShiftSwap> allOffers
                = repository.findAllShiftSwapOffersWithSameRole(
                        user.getTeam().getId(), user.getId(), today, nextMonth);

        List<ShiftSwap> filteredOffers = new ArrayList<>();
        for (ShiftSwap offer : allOffers) {
            if (offer.getSwapRequestingUser().getRole().equals(user.getRole())) {
                filteredOffers.add(offer);
            }
        }
        return filteredOffers;

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
    @Transactional
    public ShiftSwap create(ShiftSwap shiftSwap) {
        shiftSwap.setRequestedShiftSwapStatus(ShiftSwapStatus.ACCEPTED);
        shiftSwap.setSuggestedShiftSwapStatus(ShiftSwapStatus.PENDING);
        ShiftSwap created = repository.save(shiftSwap);
        Optional<Shift> shift = shiftRepository.findById(shiftSwap.getRequestedShift().getId());
        if (shift.isEmpty()) {
            throw new NotFoundException("Could not find shift with id: " + shiftSwap.getRequestedShift().getId());
        }
        shift.get().setRequestedShiftSwap(shiftSwap);

        return findById(created.getId());
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

            LocalDate today = LocalDate.now();
            LocalDate nextMonth = today.plusMonths(1).withDayOfMonth(1);
            List<Shift> userShifts
                    = shiftRepository.findAllByUsersAndDateAfterAndDateBefore(Collections.singletonList(
                    shiftSwap.getSwapRequestingUser().getId()), today, nextMonth);

            List<ShiftSwap> filteredShiftSwaps = new ArrayList<>();

                boolean overlapFound = false;

                for (Shift userShift : userShifts) {
                    if (shiftSwap.getRequestedShift().getDate().isEqual(userShift.getDate())) {
                        LocalTime offerStart = shiftSwap.getRequestedShift().getShiftType().getStartTime();
                        LocalTime offerEnd = shiftSwap.getRequestedShift().getShiftType().getEndTime();
                        LocalTime userShiftStart = userShift.getShiftType().getStartTime();
                        LocalTime userShiftEnd = userShift.getShiftType().getEndTime();

                        if (!(offerEnd.isBefore(userShiftStart) || offerStart.isAfter(userShiftEnd))) {
                            overlapFound = true;
                            break;
                        }
                    }
                }

                if (!overlapFound) {
                    filteredShiftSwaps.add(shiftSwap);
                }

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

        /*
        only user can delete his offer
        User user = userService.getPrincipalWithTeam(principal);
        ShiftSwap shiftSwap = repository.findById(id).get();
        if (shiftSwap.getSwapRequestingUser().equals(user)) {
            repository.deleteById(id);
        }
         */
        // else exception
    }
}
