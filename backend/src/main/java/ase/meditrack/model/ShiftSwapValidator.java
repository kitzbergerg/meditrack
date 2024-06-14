package ase.meditrack.model;

import ase.meditrack.exception.ResourceConflictException;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftSwap;
import ase.meditrack.model.entity.ShiftSwapStatus;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.ShiftRepository;
import ase.meditrack.repository.ShiftSwapRepository;
import ase.meditrack.repository.UserRepository;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class ShiftSwapValidator {

    private final ShiftSwapRepository shiftSwapRepository;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;
    private final ShiftValidator shiftValidator;

    public ShiftSwapValidator(ShiftSwapRepository shiftSwapRepository,
                              UserRepository userRepository,
                              ShiftRepository shiftRepository,
                              ShiftValidator shiftValidator) {
        this.shiftSwapRepository = shiftSwapRepository;
        this.userRepository = userRepository;
        this.shiftRepository = shiftRepository;
        this.shiftValidator = shiftValidator;
    }

    /**
     * Validates shift swaps when created.
     *
     * @param shiftSwap to be validated
     */
    public void shiftSwapCreateValidation(ShiftSwap shiftSwap) {
        LocalDate today = LocalDate.now();
        LocalDate nextMonth = today.plusMonths(1).withDayOfMonth(1);

        User requestedUser = shiftSwap.getSwapRequestingUser();
        Shift requestedShift = shiftSwap.getRequestedShift();

        if (shiftSwap.getRequestedShiftSwapStatus() != ShiftSwapStatus.ACCEPTED) {
            throw new ValidationException("Requested swap status should be ACCEPTED!");
        }
        if (shiftSwap.getSuggestedShiftSwapStatus() != ShiftSwapStatus.PENDING) {
            throw new ValidationException("Suggested swap status should be PENDING!");
        }

        // Both of them are already check in the shift swap controller
        User dbRequestedUser = userRepository.findById(requestedUser.getId()).get();
        Shift dbRequestedShift = shiftRepository.findById(requestedShift.getId()).get();

        List<ShiftSwap> dbAllShiftSwaps = shiftSwapRepository.findAll();

        User suggestedUser = shiftSwap.getSwapSuggestingUser();
        Shift suggestedShift = shiftSwap.getSuggestedShift();

        // This means it is a simple shift swap
        if (suggestedShift == null && suggestedUser == null) {
            for (ShiftSwap dbShiftSwap : dbAllShiftSwaps) {
                // Check if other simple shift swap is present
                if (!requestedShift.getId().equals(dbShiftSwap.getRequestedShift().getId())) {
                    continue;
                }
                if (dbShiftSwap.getSuggestedShift() == null && dbShiftSwap.getSwapSuggestingUser() == null) {
                    throw new ResourceConflictException("Shift is already offered");
                }
            }
        } else if (suggestedShift == null) {
            throw new ValidationException("Suggested shift is not set");
        } else if (suggestedUser == null) {
            throw new ValidationException("Suggested user is not set");
        } else {
            // This means it is a shift swap request
            Optional<User> optionalDbSuggestedUser = userRepository.findById(suggestedUser.getId());
            if (optionalDbSuggestedUser.isEmpty()) {
                throw new ValidationException("Suggested user does not exist");
            }
            Optional<Shift> optionalDbSuggestedShift = shiftRepository.findById(suggestedShift.getId());
            if (optionalDbSuggestedShift.isEmpty()) {
                throw new ValidationException("Suggested shift does not exist");
            }
            User dbSuggestedUser = optionalDbSuggestedUser.get();
            Shift dbSuggestedShift = optionalDbSuggestedShift.get();

            if (dbSuggestedUser.getTeam().getId() != dbRequestedUser.getTeam().getId()) {
                throw new ValidationException("Suggested user does not have the same team");
            }

            if (dbSuggestedUser.getRole().getId() != dbRequestedUser.getRole().getId()) {
                throw new ValidationException("Suggested user does not have the same role");
            }

            if (dbSuggestedUser.getId() != dbSuggestedShift.getUsers().get(0).getId()) {
                throw new ValidationException("Suggested shift does not belong to suggested User");
            }

            // Check if the suggested user offered their shift
            List<ShiftSwap> suggestedUserShiftSwaps =
                    shiftSwapRepository.findAllCreatedShiftSwapOffers(dbSuggestedUser.getId(), today, nextMonth);

            if (suggestedUserShiftSwaps.stream().noneMatch(swap ->
                    swap.getRequestedShift().getId().equals(dbSuggestedShift.getId()))) {
                throw new ValidationException("Suggested user did not offer the shift");
            }

            if (dbAllShiftSwaps.stream().anyMatch(swap ->
                    swap.getRequestedShift().getId().equals(dbRequestedShift.getId())
                    && swap.getSuggestedShift() != null
                    && swap.getSuggestedShift().getId().equals(dbSuggestedShift.getId()))) {
                throw new ResourceConflictException("Shift swap request already exist");
            }

            if (dbRequestedShift.getDate().isBefore(today) || dbRequestedShift.getDate().isAfter(nextMonth)) {
                throw new ValidationException("Requested shift is not in this month");
            }

            if (dbSuggestedShift.getDate().isBefore(today) || dbSuggestedShift.getDate().isAfter(nextMonth)) {
                throw new ValidationException("Suggested shift is not in this month");
            }

            // Check for requesting user if adding new shift without the old shift is possible
            List<UUID> requestedUsersId = new ArrayList<>();
            requestedUsersId.add(requestedUser.getId()); // Has to be fixed! A shift only has one user

            List<Shift> requestedUserShifts =
                    shiftRepository.findAllByUsersAndDateAfterAndDateBefore(requestedUsersId, today, nextMonth);

            requestedUserShifts = requestedUserShifts.stream().filter(shift ->
                    !shift.getId().equals(requestedShift.getId())).toList();


            shiftValidator.validateShiftWithCustomData(suggestedShift, requestedUserShifts);

            // Check for suggested user if adding new shift without the old shift is possible
            List<UUID> suggestedUsersId = new ArrayList<>();
            suggestedUsersId.add(suggestedUser.getId());

            List<Shift> suggestedUserShifts =
                    shiftRepository.findAllByUsersAndDateAfterAndDateBefore(suggestedUsersId, today, nextMonth);

            suggestedUserShifts = suggestedUserShifts.stream().filter(shift ->
                    !shift.getId().equals(suggestedShift.getId())).toList();


            shiftValidator.validateShiftWithCustomData(requestedShift, suggestedUserShifts);

            // check for same shift swap but requested and suggested swapped
        }
    }


}
