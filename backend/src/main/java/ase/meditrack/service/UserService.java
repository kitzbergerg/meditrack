package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.UserValidator;
import ase.meditrack.model.dto.UserDto;
import ase.meditrack.model.entity.MonthlyWorkDetails;
import ase.meditrack.model.entity.Preferences;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.mapper.UserMapper;
import ase.meditrack.repository.MonthlyWorkDetailsRepository;
import ase.meditrack.repository.ShiftRepository;
import ase.meditrack.repository.ShiftTypeRepository;
import ase.meditrack.repository.TeamRepository;
import ase.meditrack.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static jakarta.ws.rs.core.Response.Status.Family.SUCCESSFUL;

@Service
@Slf4j
public class UserService {
    private final RealmResource meditrackRealm;
    private final UserRepository repository;
    private final UserValidator userValidator;
    private final ShiftTypeRepository shiftTypeRepository;
    private final MailService mailService;
    private final MonthlyWorkDetailsRepository monthlyWorkDetailsRepository;
    private final TeamRepository teamRepository;
    private final UserMapper mapper;
    private final ShiftRepository shiftRepository;

    public UserService(RealmResource meditrackRealm, UserRepository repository, UserValidator userValidator,
                       ShiftTypeRepository shiftTypeRepository, MailService mailService,
                       MonthlyWorkDetailsRepository monthlyWorkDetailsRepository, TeamRepository teamRepository,
                       UserMapper mapper, ShiftRepository shiftRepository) {
        this.meditrackRealm = meditrackRealm;
        this.repository = repository;
        this.userValidator = userValidator;
        this.shiftTypeRepository = shiftTypeRepository;
        this.mailService = mailService;
        this.monthlyWorkDetailsRepository = monthlyWorkDetailsRepository;
        this.teamRepository = teamRepository;
        this.mapper = mapper;
        this.shiftRepository = shiftRepository;
    }

    private static void setUserRoles(RealmResource meditrackRealm, String userId, List<String> roles) {
        if (roles == null) return;
        // for some reason keycloak doesn't use the roles in UserRepresentation, so we need to set them explicitly
        List<RoleRepresentation> userRoles =
                roles.stream().map(role -> meditrackRealm.roles().get(role).toRepresentation()).toList();
        UserResource user = meditrackRealm.users().get(userId);
        RoleScopeResource roleScopeResource = user.roles().realmLevel();
        roleScopeResource.remove(roleScopeResource.listAll());
        user.roles().realmLevel().add(userRoles);
    }

    /**
     * Fetches all users from the database and matches additional attributes from keycloak.
     *
     * @return List of all users
     */
    public List<User> findAll() {
        return repository.findAll()
                .stream()
                .peek(u -> u.setUserRepresentation(meditrackRealm.users().get(u.getId().toString()).toRepresentation()))
                .toList();
    }

    /**
     * Fetches all users from the team of the dm from the database and matches additional attributes from keycloak.
     *
     * @param principal the current user's id
     * @return List of all users from the team of the dm
     */
    public List<User> findByTeam(Principal principal) throws NotFoundException {
        return repository.findAllByTeam(getPrincipalWithTeam(principal).getTeam()).stream()
                .peek(u -> u.setUserRepresentation(meditrackRealm.users().get(u.getId().toString()).toRepresentation()))
                .toList();
    }

    /**
     * Fetches a user by id from the database and matches additional attributes from keycloak.
     *
     * @param id the id of the user
     * @return the user
     */
    public User findById(UUID id) {
        return repository.findById(id).map(u -> {
            u.setUserRepresentation(meditrackRealm.users().get(u.getId().toString()).toRepresentation());
            return u;
        }).orElseThrow(() -> new NotFoundException("Could not find user with id: " + id + "!"));
    }

    /**
     * Creates a user in the database and in keycloak.
     *
     * @param user the user to create
     * @param shouldSendInviteMail if true, an invitation mail will be sent to the user
     * @return the created user
     */
    public User create(User user, Boolean shouldSendInviteMail) {
        UserRepresentation userRepresentation = createKeycloakUser(user.getUserRepresentation());
        user.setId(UUID.fromString(userRepresentation.getId()));
        user.setCurrentOverTime(0);
        if (user.getPreferences() == null) {
            user.setPreferences(new Preferences(
                    null,
                    List.of(),
                    user
            ));
        }
        user = repository.save(user);
        //as transient ignores the userRepresentation, we need to set it again
        user.setUserRepresentation(userRepresentation);

        if (shouldSendInviteMail != null && shouldSendInviteMail) {
            new Thread(() -> sendMailToUser(userRepresentation)).start();
        }
        return user;
    }

    private void sendMailToUser(UserRepresentation userRepresentation) {
        mailService.sendSimpleMessage(userRepresentation.getEmail(), "Welcome to Meditrack",
                generateWelcomeMessage(userRepresentation));
    }

    private UserRepresentation createKeycloakUser(UserRepresentation userRepresentation) {
        try (Response response = meditrackRealm.users().create(userRepresentation)) {
            if (response.getStatusInfo().toEnum().getFamily() != SUCCESSFUL) {
                log.error("Error creating user: {}", response.getStatusInfo().getReasonPhrase());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            String id = CreatedResponseUtil.getCreatedId(response);
            setUserRoles(meditrackRealm, id, userRepresentation.getRealmRoles());
            return meditrackRealm.users().get(id).toRepresentation();
        }
    }

    /**
     * Updates a user in the database and in keycloak.
     *
     * @param user      the user to update
     * @param principal the current user's id
     * @return the updated user
     */
    public User update(User user, Principal principal) {
        //checks if employee to delete is part of team
        this.userValidator.teamValidate(user.getId(), principal);
        meditrackRealm.users().get(user.getUserRepresentation().getId()).update(user.getUserRepresentation());
        setUserRoles(meditrackRealm,
                user.getUserRepresentation().getId(), user.getUserRepresentation().getRealmRoles());
        //as transient ignores the userRepresentation, we need to remember and set it again if we want to return it
        UserRepresentation userRepresentation = user.getUserRepresentation();

        //perform partial update: load user from db and update only the fields that are not null
        user = updateChangedAttributes(user);
        user = repository.save(user);

        user.setUserRepresentation(userRepresentation);
        UUID id = user.getId();
        return repository.findById(user.getId()).map(u -> {
            u.setUserRepresentation(meditrackRealm.users().get(u.getId().toString()).toRepresentation());
            log.info("Updating user {}.", u);
            return u;
        }).orElseThrow(() -> new NotFoundException("Could not find user with id: " + id + "!"));
    }

    /**
     * Deletes a user from the database and from keycloak.
     *
     * @param id        the id of the user to delete
     * @param principal the current user's id
     */
    public void delete(UUID id, Principal principal) {
        //checks if employee to delete is part of dms team
        this.userValidator.teamValidate(id, principal);
        try (Response response = meditrackRealm.users().delete(String.valueOf(id))) {
            if (response.getStatusInfo().toEnum().getFamily() != SUCCESSFUL) {
                log.error("Error deleting user: {}", response.getStatusInfo().getReasonPhrase());
                if (response.getStatusInfo().getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                    throw new NotFoundException("Could not find user with id: " + id + "!");
                } else {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            repository.deleteById(id);
        }
    }

    @Transactional
    protected User updateChangedAttributes(User user) {
        User dbUser = repository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Could not find user with id: " + user.getId() + "!"));

        if (user.getRole() != null && user.getRole().getId() != null) {
            dbUser.setRole(user.getRole());
        }
        if (user.getWorkingHoursPercentage() != null) {
            dbUser.setWorkingHoursPercentage(user.getWorkingHoursPercentage());
        }
        if (user.getCurrentOverTime() != null) {
            dbUser.setCurrentOverTime(user.getCurrentOverTime());
        }

        if (user.getPreferences() != null) {
            dbUser.setPreferences(user.getPreferences());
        }
        if (user.getCanWorkShiftTypes() != null) {
            for (ShiftType shiftType : dbUser.getCanWorkShiftTypes()) {
                shiftType.getWorkUsers().remove(dbUser);
            }
            dbUser.getCanWorkShiftTypes().clear();

            // Add new shift types
            for (ShiftType newShiftType : user.getCanWorkShiftTypes()) {
                ShiftType shiftType = shiftTypeRepository.findById(newShiftType.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("ShiftType not found"));
                dbUser.getCanWorkShiftTypes().add(shiftType);
                shiftType.getWorkUsers().add(dbUser);
            }
        }
        if (user.getPreferredShiftTypes() != null) {
            dbUser.getPreferredShiftTypes().clear();
            dbUser.getPreferredShiftTypes().addAll(user.getPreferredShiftTypes());
        }

        return dbUser;
    }

    /**
     * Fetches the user with the id from the principal.
     *
     * @param principal the current user
     * @return user with the id from principal
     */
    public User getPrincipalWithTeam(Principal principal) {
        UUID dmId = UUID.fromString(principal.getName());
        Optional<User> dm = repository.findById(dmId);
        if (dm.isEmpty()) {
            throw new NotFoundException("User doesnt exist");
        }
        if (dm.get().getTeam() == null) {
            throw new NotFoundException("User has no team");
        }
        return dm.get();
    }

    /**
     * Fetches work details from the principal, given a month and year.
     *
     * @param userId of user
     * @param month  of the work details
     * @param year   of the work details
     * @return monthly work details for the user, given the month and year
     */
    public MonthlyWorkDetails findWorkDetailsByIdAndMonthAndYear(UUID userId, Month month, Year year) {
        MonthlyWorkDetails details = monthlyWorkDetailsRepository.findMonthlyWorkDetailsByUserIdAndMonthAndYear(
                userId, month.getValue(), year.getValue());
        return details;
    }

    /**
     * Fetches all users from the database and matches additional attributes from keycloak.
     *
     * @param shiftId the id of the shift
     *
     * @return List of all users
     */
    public List<User> getSickReplacement(UUID shiftId) {
        Optional<Shift> shift = shiftRepository.findById(shiftId);
        if (shift.isEmpty()) {
            throw new NotFoundException("Shift not found");
        }
        User sickUser = shift.get().getUsers().get(0);
        List<User> usersSameRole = repository.findAllByRole(shift.get().getUsers().get(0).getRole()).stream()
                .peek(u -> {
                    u.setUserRepresentation(meditrackRealm.users().get(u.getId().toString()).toRepresentation());
                }).collect(Collectors.toList());
        usersSameRole.remove(sickUser);
        log.info("Users with same role and can work shift types: {}", usersSameRole);
        // remove users that have worked too many days in a row in the past or present
        LocalDate shiftDate = shift.get().getDate();
        LocalDate startDate = shiftDate.minusDays(3);
        LocalDate endDate = shiftDate.plusDays(3);

        usersSameRole.removeIf(user -> {
            List<Shift> userShifts = shiftRepository.findAllByUsersAndDateAfterAndDateBefore(
                    List.of(user.getId()), startDate, endDate);

            // Remove users that have a shift on the same day
            boolean hasShiftOnSameDay = userShifts.stream().anyMatch(s -> s.getDate().equals(shiftDate));
            if (hasShiftOnSameDay) {
                return true;
            }

            // Check for day shift following night shift
            if (hasDayShiftFollowingNightShift(shift.get(), userShifts, shiftDate)) {
                return true;
            }

            // Check for night shift before day shift
            if (hasNightShiftBeforeDayShift(shift.get(), userShifts, shiftDate)) {
                return true;
            }

            // Check if the shift is during a holiday
            boolean isDuringHoliday = user.getHolidays().stream().anyMatch(holiday ->
                    !shiftDate.isBefore(holiday.getStartDate()) && !shiftDate.isAfter(holiday.getEndDate())
            );
            if (isDuringHoliday) {
                return true;
            }

            // Check if there is at least one day off in the specified period
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                LocalDate finalDate = date;
                boolean hasShift = userShifts.stream().anyMatch(s -> s.getDate().equals(finalDate));
                if (!hasShift) {
                    return false; // User has at least one day off
                }
            }
            return true; // User does not have any day off in the period
        });
        return usersSameRole;
    }

    private boolean hasDayShiftFollowingNightShift(Shift shift, List<Shift> userShifts, LocalDate shiftDate) {
        LocalTime dayStart = LocalTime.of(8, 0);
        LocalTime nightStart = LocalTime.of(20, 0);
        LocalTime shiftStartTime = shift.getShiftType().getStartTime();
        boolean isDayShift = !shiftStartTime.isBefore(dayStart) && shiftStartTime.isBefore(nightStart);

        if (isDayShift) {
            return userShifts.stream().anyMatch(s -> {
                LocalTime previousShiftStartTime = s.getShiftType().getStartTime();
                boolean isNightShift = previousShiftStartTime.isBefore(dayStart)
                        || !previousShiftStartTime.isBefore(nightStart);
                return isNightShift && s.getDate().equals(shiftDate.minusDays(1));
            });
        }
        return false;
    }

    private boolean hasNightShiftBeforeDayShift(Shift shift, List<Shift> userShifts, LocalDate shiftDate) {
        LocalTime dayStart = LocalTime.of(8, 0);
        LocalTime nightStart = LocalTime.of(20, 0);
        LocalTime shiftStartTime = shift.getShiftType().getStartTime();
        boolean isNightShift = shiftStartTime.isBefore(dayStart) || !shiftStartTime.isBefore(nightStart);

        if (isNightShift) {
            return userShifts.stream().anyMatch(s -> {
                LocalTime nextShiftStartTime = s.getShiftType().getStartTime();
                boolean isDayShift = !nextShiftStartTime.isBefore(dayStart)
                        && nextShiftStartTime.isBefore(nightStart);
                return isDayShift && s.getDate().equals(shiftDate.plusDays(1));
            });
        }
        return false;
    }

    /**
     * Checks if the principal has the authority to create a user with a specific role.
     *
     * @param roles     string array of system roles
     * @param principal the current user
     * @return ture if the principal has the right authority for creating user with its role, false otherwise
     */
    public boolean isCorrectUserSystemRole(List<String> roles, Principal principal) {
        User dm = getPrincipalWithTeam(principal);

        UserResource user = meditrackRealm.users().get(String.valueOf(dm.getId()));

        if (user.roles().realmLevel().listAll().stream().anyMatch(roleRepresentation
                -> roleRepresentation.getName().equals("admin"))) {
            return true;
        }

        return roles.stream().noneMatch(role
                -> role.equals("admin") || role.equals("dm"));
    }

    /**
     * Checks if the user is in the same team as the principal.
     *
     * @param principal the current user
     * @param userDto   of the user to check
     * @return true if the user and the current user are from the same team, false otherwise
     */
    public boolean isSameTeam(Principal principal, UserDto userDto) {
        User dm = getPrincipalWithTeam(principal);
        if (userDto == null) {
            return false;
        }
        return dm.getTeam().getId().equals(userDto.team());
    }

    /**
     * Checks if the user is in the same team as the principal.
     *
     * @param principal the current user
     * @param userId    of the user to check
     * @return true if the user and the current user are from the same team, false otherwise
     */
    public boolean isSameTeam(Principal principal, UUID userId) {
        User dm = getPrincipalWithTeam(principal);
        User user = findById(userId);
        return dm.getTeam().getId().equals(user.getTeam().getId());
    }

    /**
     * Fetches the team leader of a given user.
     *
     * @param user the user to find the team leader for
     * @return the team leader of the user
     */
    public Optional<User> findTeamLeaderByMember(User user) {
        if (user.getTeam() == null) {
            throw new NotFoundException("User has no team.");
        }
        Team team = teamRepository.findById(user.getTeam().getId())
                .orElseThrow(() -> new NotFoundException("Team not found."));

        if (team.getUsers() != null && !team.getUsers().isEmpty()) {
            for (User u : team.getUsers()) {
                u = findById(u.getId()); //we need to call findById to get the userRepresentation
                List<String> roles = mapper.mapRoles(u);
                if (roles.contains("dm")) {
                    return Optional.of(u);
                }
            }
        }
        return Optional.empty();
    }

    private String generateWelcomeMessage(UserRepresentation userRepresentation) {
        return "Welcome to MediTrack, " + userRepresentation.getFirstName() + " "
                + userRepresentation.getLastName() + "!\n\n"
                + "You have been successfully registered as a user in MediTrack.\n\n"
                + "Your username is: " + userRepresentation.getUsername() + "\n\n"
                + "You can now log in to MediTrack and start using the application.\n\n"
                + "If you have any questions or need help, please contact your team leader.\n\n"
                + "Best regards,\n"
                + "Your MediTrack Team";
    }
}
