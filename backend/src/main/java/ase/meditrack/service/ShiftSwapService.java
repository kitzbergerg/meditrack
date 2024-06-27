package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.ShiftSwapValidator;
import ase.meditrack.model.dto.ShiftSwapDto;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftSwap;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.entity.enums.ShiftSwapStatus;
import ase.meditrack.repository.ShiftRepository;
import ase.meditrack.repository.ShiftSwapRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ShiftSwapService {
    private final ShiftSwapRepository repository;
    private final ShiftRepository shiftRepository;
    private final UserService userService;
    private final ShiftSwapValidator validator;
    private final MailService mailService;

    public ShiftSwapService(ShiftSwapRepository repository, UserService userService, ShiftRepository shiftRepository,
                            ShiftSwapValidator validator, MailService mailService) {
        this.repository = repository;
        this.shiftRepository = shiftRepository;
        this.userService = userService;
        this.validator = validator;
        this.mailService = mailService;
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
     * Fetches all shift swap offers from the current month from a user from the database.
     *
     * @param principal is current user
     * @return List of all shift swap offers from the current month from a user
     */
    public List<ShiftSwap> findAllByCurrentMonth(Principal principal) {
        User user = userService.getPrincipalWithTeam(principal);
        LocalDate today = LocalDate.now();
        LocalDate nextMonth = today.plusMonths(1).withDayOfMonth(1);

        return repository.findAllCreatedShiftSwapOffers(user.getId(), today, nextMonth);
    }

    /**
     * Fetches all shift swap requests from the current month from a user from the database.
     *
     * @param principal is current user
     * @return List of all shift swap requests from the current month from a user
     */
    public List<ShiftSwap> findAllRequests(Principal principal) {
        User user = userService.getPrincipalWithTeam(principal);
        LocalDate today = LocalDate.now();
        LocalDate nextMonth = today.plusMonths(1).withDayOfMonth(1);

        return repository.findAllShiftSwapRequests(user.getId(), today, nextMonth);
    }

    /**
     * Fetches all shift swap suggestions from the current month from a user from the database.
     *
     * @param principal is current user
     * @return List of all shift swap suggestions from the current month from a user
     */
    public List<ShiftSwap> findAllSuggestions(Principal principal) {
        User user = userService.getPrincipalWithTeam(principal);
        LocalDate today = LocalDate.now();
        LocalDate nextMonth = today.plusMonths(1).withDayOfMonth(1);

        return repository.findAllShiftSwapSuggestions(user.getId(), today, nextMonth);
    }

    /**
     * Fetches all shift swap offers from the current month from one team.
     * from user with the same role from the database.
     *
     * @param principal is current user
     * @return List of all shift swap offers from other
     */
    public List<ShiftSwap> findAllOffersByCurrentMonth(Principal principal) {
        User user = userService.getPrincipalWithTeam(principal);
        LocalDate today = LocalDate.now();
        LocalDate nextMonth = today.plusMonths(1).withDayOfMonth(1);

        return repository.findAllShiftSwapOffersWithSameRole(
                user.getTeam().getId(), user.getRole().getId(), user.getId(), today, nextMonth);
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
     * @param shouldSendMail if a mail should be sent to the suggested user about the swap
     * @return the created shift swap
     */
    @Transactional
    public ShiftSwap create(ShiftSwap shiftSwap, Boolean shouldSendMail) {
        validator.shiftSwapCreateValidation(shiftSwap);
        Optional<Shift> requestedShift = shiftRepository.findById(shiftSwap.getRequestedShift().getId());
        requestedShift.ifPresent(shift -> shift.getRequestedShiftSwap().add(shiftSwap));

        ShiftSwap created = repository.save(shiftSwap);

       if (shouldSendMail != null && shouldSendMail) {
           if (shiftSwap.getSwapSuggestingUser() != null && shiftSwap.getSwapRequestingUser() != null) {
               User suggestingUser = userService.findById(shiftSwap.getSwapSuggestingUser().getId());
               User requestingUser = userService.findById(shiftSwap.getSwapRequestingUser().getId());
               if (suggestingUser.getUserRepresentation() != null
                       && suggestingUser.getUserRepresentation().getEmail() != null) {
                   new Thread(() -> sendRequestMail(suggestingUser, requestingUser, created)).start();
               }
           }
       }
       return findById(created.getId());
    }

    private void sendRequestMail(User suggestingUser, User requestingUser, ShiftSwap swap) {
        mailService.sendSimpleMessage(suggestingUser.getUserRepresentation().getEmail(), "New shift swap request!",
                generateRequestMail(swap, requestingUser));
    }

    /**
     * Updates a shift swap in the database. Only the status of a shift swap can be updated, which may lead to changing
     * shifts from users.
     *
     * @param shiftSwap the shift swap to update
     * @param shouldSendMail if a mail should be sent to the users about the swap update
     * @return the updated shift swap
     */
    @Transactional
    public ShiftSwap update(ShiftSwap shiftSwap, Boolean shouldSendMail) {
        ShiftSwap dbShiftSwap = findById(shiftSwap.getId());

        // check if shift swap is the same as the one from the database except the status of the user
        validator.shiftSwapUpdateValidation(shiftSwap, dbShiftSwap);

        if (shiftSwap.getSuggestedShiftSwapStatus().equals(ShiftSwapStatus.ACCEPTED)) {

            Shift requestedShift = dbShiftSwap.getRequestedShift();
            Shift suggestedShift = dbShiftSwap.getSuggestedShift();

            // delete all shift swaps corresponding to the shift, since the user gets swapped
            repository.deleteAllByRequestedShiftId(requestedShift.getId());
            repository.deleteAllByRequestedShiftId(suggestedShift.getId());

            List<User> requestedUsers = requestedShift.getUsers();
            List<User> suggestedUsers = suggestedShift.getUsers();

            // swap only the user(s) in the shift (there is only one user in the list)
            requestedShift.setUsers(suggestedUsers);
            suggestedShift.setUsers(requestedUsers);

            if (shouldSendMail != null && shouldSendMail) {
                User requestedUser = userService.findById(requestedUsers.get(0).getId());
                User suggestedUser = userService.findById(suggestedUsers.get(0).getId());
                if (requestedUser.getUserRepresentation() != null
                        && requestedUser.getUserRepresentation().getEmail() != null
                        && suggestedUser.getUserRepresentation() != null
                        && suggestedUser.getUserRepresentation().getEmail() != null) {
                    new Thread(() -> sendAcceptedMails(requestedUser, suggestedUser, dbShiftSwap)).start();
                }
            }
            return null;
        } else {
            // decline
            if (shouldSendMail != null && shouldSendMail) {
                User requestedUser = userService.findById(dbShiftSwap.getSwapRequestingUser().getId());
                if (requestedUser.getUserRepresentation() != null
                        && requestedUser.getUserRepresentation().getEmail() != null) {
                    new Thread(() -> sendDeclinedMail(requestedUser, dbShiftSwap)).start();
                }
            }
            return repository.save(shiftSwap);
        }
    }

    private void sendAcceptedMails(User requestingUser, User suggestingUser, ShiftSwap swap) {
        mailService.sendSimpleMessage(requestingUser.getUserRepresentation().getEmail(), "Shift swap accepted!",
                generateAcceptedMailForRequestingUser(requestingUser, swap));

        mailService.sendSimpleMessage(suggestingUser.getUserRepresentation().getEmail(), "Shift swap accepted!",
                generateAcceptedMailForSuggestingUser(suggestingUser, swap));
    }

    private void sendDeclinedMail(User requestingUser, ShiftSwap swap) {
        mailService.sendSimpleMessage(requestingUser.getUserRepresentation().getEmail(), "Shift swap declined!",
                generateDeclinedMail(swap));
    }

    /**
     * Deletes the shift swap request without deleting the actual shift swap offer.
     *
     * @param id to be deleted
     */

    @Transactional
    public void retract(UUID id) {
        repository.deleteById(id);
    }

    /**
     * Deletes a shift swap offer and all corresponding shift swap requests from the database.
     *
     * @param id the id of the shift swap offer to delete
     */
    @Transactional
    public void delete(UUID id) {
        ShiftSwap shiftSwap = findById(id);
        repository.deleteAllByRequestedShiftId(shiftSwap.getRequestedShift().getId());
    }

    /**
     * Checks if the shift swap belongs to the user.
     *
     * @param principal current user
     * @param shiftSwapId from the shift swap
     * @return true, if the shift swap belongs to the user, false otherwise
     */
    public boolean isShiftSwapFromUser(Principal principal, UUID shiftSwapId) {
        if (shiftSwapId == null) {
            return false;
        }
        ShiftSwap shiftSwap = findById(shiftSwapId);
        return isShiftFromUser(principal, shiftSwap);
    }

    /**
     * Checks if the shift swap belongs to the suggested User.
     *
     * @param principal current user
     * @param shiftSwapDto from the shift swap
     * @return true, if the shift swap belongs to the suggested user, false otherwise
     */
    public boolean isShiftSwapFromSuggestedUser(Principal principal, ShiftSwapDto shiftSwapDto) {
        User user = userService.getPrincipalWithTeam(principal);
        if (shiftSwapDto == null) {
            return false;
        }

        return shiftSwapDto.swapSuggestingUser().equals(user.getId());
    }

    /**
     * Checks if the shift belongs to the user.
     *
     * @param principal current user
     * @param shiftSwap from the shift swap
     * @return true, if the shift belongs to the user, false otherwise
     */
    public boolean isShiftFromUser(Principal principal, ShiftSwap shiftSwap) {
        User user = userService.getPrincipalWithTeam(principal);
        if (shiftSwap.getRequestedShift() == null) {
            return false;
        }
        Optional<Shift> shift = shiftRepository.findById(shiftSwap.getRequestedShift().getId());
        if (shift.isEmpty()) {
            return false;
        }
        if (!shift.get().getUsers().get(0).getId().equals(user.getId())) {
            return false;
        }
        if (shiftSwap.getSwapRequestingUser() == null) {
            return false;
        }
        return user.getId().equals(shiftSwap.getSwapRequestingUser().getId());
    }

    private String generateRequestMail(ShiftSwap swap, User user) {
        return "New shift swap request from " + user.getUserRepresentation().getFirstName() + " "
                + user.getUserRepresentation().getLastName() + ":\n\n"
                + "Swapping the shift from: " + swap.getRequestedShift().getDate() + " to: "
                + swap.getSuggestedShift().getDate() + "!\n\n"
                + "To get more details and approve or reject the swap, please log in to MediTrack.\n\n"
                + "Best regards,\n"
                + "Your MediTrack Team";
    }

    private String generateAcceptedMailForRequestingUser(User suggestingUser, ShiftSwap swap) {
        return "Your shift swap request has been accepted by " + suggestingUser.getUserRepresentation().getFirstName()
                + " " + suggestingUser.getUserRepresentation().getLastName() + "!\n\n"
                + "Swapping the shift from: " + swap.getRequestedShift().getDate() + " to: "
                + swap.getSuggestedShift().getDate() + "!\n\n"
                + "Best regards,\n"
                + "Your MediTrack Team";
    }

    private String generateAcceptedMailForSuggestingUser(User requestingUser, ShiftSwap swap) {
        return "You have accepted the shift swap request from " + requestingUser.getUserRepresentation().getFirstName()
                + " " + requestingUser.getUserRepresentation().getLastName() + "!\n\n"
                + "Swapping the shift from: " + swap.getSuggestedShift().getDate() + " to: "
                + swap.getRequestedShift().getDate() + "!\n\n"
                + "Best regards,\n"
                + "Your MediTrack Team";
    }

    private String generateDeclinedMail(ShiftSwap swap) {
        return "Your shift swap request has been declined!\n\n"
                + "Swapping the shift from: " + swap.getRequestedShift().getDate() + " to: "
                + swap.getSuggestedShift().getDate() + "!\n\n"
                + "Best regards,\n"
                + "Your MediTrack Team";
    }
}
