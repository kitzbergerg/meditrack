package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.HolidayValidator;
import ase.meditrack.model.entity.Holiday;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.entity.enums.HolidayRequestStatus;
import ase.meditrack.repository.HolidayRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class HolidayService {
    private final HolidayRepository repository;
    private final UserService userService;
    private final HolidayValidator validator;
    private final TeamService teamService;
    private final MailService mailService;

    public HolidayService(HolidayRepository repository, UserService userService,
                HolidayValidator validator, TeamService teamService, MailService mailService) {
        this.repository = repository;
        this.userService = userService;
        this.validator = validator;
        this.teamService = teamService;
        this.mailService = mailService;
    }

    /**
     * Creates a holiday for the current user in the database.
     *
     * @param holiday the holiday to create
     * @param userId the id of the user
     * @return the created holiday
     */
    public Holiday create(Holiday holiday, String userId) {
        User user = userService.findById(UUID.fromString(userId));
        validator.validateHoliday(holiday, user);
        holiday.setUser(user);
        holiday.setStatus(HolidayRequestStatus.REQUESTED);
        return repository.save(holiday);
    }

    /**
     * Fetches all upcoming holidays from the database for a specific user.
     *
     * @param userId the id of the user
     * @return List of all holidays for a specific user
     */
    public List<Holiday> findAllByUser(String userId) {
        User user = userService.findById(UUID.fromString(userId));
        return repository.findAllByUser(user)
                .stream()
                .filter(holiday -> holiday.getStartDate().isAfter(LocalDate.now().minusDays(1)))
                .toList();
    }

    /**
     * Fetches all holidays from the database for a specific user and id.
     *
     * @param id the id of the holiday
     * @param userId the id of the user
     * @return the holiday with the specific id for the user
     */
    public Holiday findByIdAndUser(UUID id, String userId) {
        User user = userService.findById(UUID.fromString(userId));
        return repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Could not find holiday with id: " + id + " for user with "
                        + "id: " + userId + "!"));
    }

    /**
     * Fetches all holidays from the database.
     *
     * @return List of all holidays
     */
    public List<Holiday> findAll() {
        return repository.findAll();
    }

    /**
     * Fetches a holiday by id from the database.
     *
     * @param id the id of the holiday
     * @return the holiday
     */
    public Holiday findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Could not find holiday with id: " + id + "!"));
    }

    /**
     * Fetches all upcoming holidays from the database for a specific team.
     *
     * @param principal the principal
     * @return List of all holidays for a specific team
     */
    public List<Holiday> findAllByTeam(Principal principal) {
        return repository.findByUserIn(userService.findByTeam(principal))
                .stream()
                .filter(holiday -> holiday.getStartDate().isAfter(LocalDate.now().minusDays(1)))
                .toList();
    }

    /**
     * Updates a holiday for the current user in the database.
     *
     * @param holiday the holiday to update
     * @param userId the id of the user
     * @return the updated holiday
     */
    public Holiday update(Holiday holiday, String userId) {
        Holiday dbHoliday = findById(holiday.getId());

        validator.validateHolidayOnUpdate(holiday, userId, dbHoliday);

        if (holiday.getStartDate() != null) {
            dbHoliday.setStartDate(holiday.getStartDate());
        }
        if (holiday.getEndDate() != null) {
            dbHoliday.setEndDate(holiday.getEndDate());
        }
        // only allow updating the status to cancelled for the user
        if (holiday.getStatus() != null && holiday.getStatus() == HolidayRequestStatus.CANCELLED) {
            dbHoliday.setStatus(holiday.getStatus());
        }

        return repository.save(dbHoliday);
    }

    /**
     * Updates the status of a holiday in the database.
     * @param id the id of the holiday
     * @param status the new status
     * @param principal the principal
     * @param shouldSendMail if a mail should be sent to the user about the status change
     * @return the updated holiday
     */
    public Holiday updateStatus(UUID id, HolidayRequestStatus status, Principal principal, Boolean shouldSendMail) {
        Holiday holiday = findById(id);
        // check if holiday is in the list of the dm's team holidays
        if (!findAllByTeam(principal).contains(holiday)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You are not allowed to update the status of this holiday! Only the team lead can update it.");
        }
        holiday.setStatus(status);
        holiday = repository.save(holiday);

        if (shouldSendMail) {
            //get email address from user
            User user = userService.findById(holiday.getUser().getId());
            mailService.sendSimpleMessage(user.getUserRepresentation().getEmail(),
                    "The status of your holiday request has been updated!",
                    generateStatusUpdateMessageForUser(holiday));
        }
        return holiday;
    }

    /**
     * Deletes a holiday from the database.
     *
     * @param id the id of the holiday
     */
    public void delete(UUID id) {
        validator.validateHolidayOnDelete(findById(id));
        repository.deleteById(id);
    }

    /**
     * Checks if the holiday belongs to the team.
     *
     * @param principal current user
     * @param holidayId of the holiday
     * @return true, if the holiday belongs to the team, false otherwise
     */
    public boolean isHolidayFromTeam(Principal principal, UUID holidayId) {
        Holiday holiday = findById(holidayId);
        return teamService.isInTeam(UUID.fromString(principal.getName()), holiday.getUser().getTeam().getId());
    }

    /**
     * Checks if the holiday belongs to the user.
     *
     * @param principal current user
     * @param holidayId of the holiday
     * @return true, if the holiday belongs to the user, false otherwise
     */
    public boolean isHolidayFromUser(Principal principal, UUID holidayId) {
        Holiday holiday = findById(holidayId);
        return isCurrentUserSameAsUser(principal, holiday.getUser().getId());
    }

    /**
     * Checks if the user of the holiday is the same as the current user.
     *
     * @param principal current user
     * @param userIdFromHoliday the user who owns the holiday
     * @return true if the both users are the same, false otherwise
     */
    public boolean isCurrentUserSameAsUser(Principal principal, UUID userIdFromHoliday) {
        User user = userService.getPrincipalWithTeam(principal);
        return user.getId().equals(userIdFromHoliday);
    }

    private String generateHolidayRequestMessageForDm(Holiday holiday) {
        return "New holiday request from " + holiday.getUser().getUserRepresentation().getFirstName() + " "
                + holiday.getUser().getUserRepresentation().getLastName() + ":\n\n"
                + "Holiday from: " + holiday.getStartDate() + "to: " + holiday.getEndDate() + "!\n\n"
                + "To get more details and approve or reject the holiday, please log in to MediTrack.\n\n"
                + "Best regards,\n"
                + "Your MediTrack Team";
    }

    private String generateStatusUpdateMessageForUser(Holiday holiday) {
        return "The status of your holiday request from " + holiday.getStartDate() + " to " + holiday.getEndDate()
                + " has been updated to " + holiday.getStatus() + "!\n\n"
                + "To get more details about and edit your holiday, please log in to MediTrack.\n\n"
                + "If you have any questions or need help, please contact your team leader.\n\n"
                + "Best regards,\n"
                + "Your MediTrack Team";
    }
}
