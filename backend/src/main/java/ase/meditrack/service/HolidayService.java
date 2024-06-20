package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.Holiday;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.HolidayRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class HolidayService {
    private final HolidayRepository repository;
    private final UserService userService;
    private final TeamService teamService;

    public HolidayService(HolidayRepository repository, UserService userService, TeamService teamService) {
        this.repository = repository;
        this.userService = userService;
        this.teamService = teamService;
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
     * Creates a holiday in the database.
     *
     * @param holiday the holiday to create
     * @return the created holiday
     */
    public Holiday create(Holiday holiday) {
        return repository.save(holiday);
    }

    /**
     * Updates a holiday in the database.
     *
     * @param holiday the holiday to update
     * @return the updated holiday
     */
    public Holiday update(Holiday holiday) {
        Holiday dbHoliday = findById(holiday.getId());

        if (holiday.getStartDate() != null) {
            dbHoliday.setStartDate(holiday.getStartDate());
        }
        if (holiday.getEndDate() != null) {
            dbHoliday.setEndDate(holiday.getEndDate());
        }
        if (holiday.getIsApproved() != null) {
            dbHoliday.setIsApproved(holiday.getIsApproved());
        }
        if (holiday.getUser() != null) {
            dbHoliday.setUser(holiday.getUser());
        }

        return repository.save(dbHoliday);
    }

    /**
     * Deletes a holiday from the database.
     *
     * @param id the id of the holiday
     */
    public void delete(UUID id) {
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
}
