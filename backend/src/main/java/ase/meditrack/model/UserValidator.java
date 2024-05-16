package ase.meditrack.model;

import ase.meditrack.exception.ValidationException;
import ase.meditrack.model.dto.UserDto;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.mapper.UserMapper;
import ase.meditrack.repository.UserRepository;
import ase.meditrack.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.*;

@Component
@Slf4j
public class UserValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private UserRepository userRepository;
    private final RealmResource meditrackRealm;
    private final UserMapper userMapper;

    public UserValidator(UserRepository userRepository, RealmResource meditrackRealm, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.meditrackRealm = meditrackRealm;
        this.userMapper = userMapper;
    }

    public void createValidate(User user, Principal principal) throws ValidationException {
        LOGGER.trace("Makes validation for {}", user);

        UserRepresentation userRepresentation = user.getUserRepresentation();

        String username = userRepresentation.getUsername();
        String first = userRepresentation.getFirstName();
        String last = userRepresentation.getLastName();
        String email = userRepresentation.getEmail();
        Float workingHoursPercentage = user.getWorkingHoursPercentage();


        if (principal != null) {
            principalValidate(user, principal);
        }

        if (username == null || username.isBlank()) {
            throw new ValidationException("Employee has to have a username");
        }
        if (first == null || first.isBlank()) {
            throw new ValidationException("Employee has to have a first name");
        }
        if (last == null || last.isBlank()) {
            throw new ValidationException("Employee has to have a last name");
        }
        if (username.length() >= 30) {
            throw new ValidationException("username is too long");
        }
        if (first.length() >= 256) {
            throw new ValidationException("firstName is too long");
        }
        if (last.length() >= 256) {
            throw new ValidationException("lastName is too long");
        }
        if (email != null && !email.isBlank()) {
            String[] splitMail = email.split("@");

            if (email.length() >= 256) {
                throw new ValidationException("Email is too long");
            } else if (splitMail.length == 2) {
                if (splitMail[1].split("\\.").length < 2) {
                    throw new ValidationException("Email has the wrong format");
                }
            } else {
                throw new ValidationException("Email is invalid");
            }
        }
        if(workingHoursPercentage <= 0 || workingHoursPercentage > 100) {
            throw new ValidationException("Working hours percentage cannot be negative or more than 100");
        }
    }

    private void principalValidate(User user, Principal principal) {
        User dm = getTeamValidate(principal);
        log.info("Logging dm {}", userMapper.toDto(dm).roles());
        UserDto dmDto = userMapper.toDto(dm);
        if (!user.getTeam().equals(dm.getTeam())) {
            throw new ValidationException("Team from principal and user is not the same");
        }

        if (dmDto.roles() == null) {
            throw new ValidationException("Principal has no roles");
        }

        // If the principal is only a 'dm' (not an admin), restrict creating 'dm' or 'admin'
        if (!dmDto.roles().contains("admin")) {
            // If the principal is neither an admin, nor a dm, throw error
            if (!dmDto.roles().contains("dm")) {
                throw new ValidationException("Principal does not have the required 'dm' role");
            }
            if (user.getUserRepresentation().getRealmRoles() == null) {
                throw new ValidationException("User has no roles");
            }
            // Check if the user to be created has only the 'employee' role
            if (!user.getUserRepresentation().getRealmRoles().contains("employee")) {
                throw new ValidationException("Dm can not create any other role than employee");
            }

            // Ensure the user being created does not have 'admin' or 'dm' roles
            if (user.getUserRepresentation().getRealmRoles().contains("admin") ||
                    user.getUserRepresentation().getRealmRoles().contains("dm")) {
                throw new ValidationException("Creating 'dm' or 'admin' roles is forbidden for non-admin");
            }
        }
    }

    public User getTeamValidate(Principal principal) throws NoSuchElementException {
        LOGGER.trace("Validation for dm");
        UUID dmId = UUID.fromString(principal.getName());
        User dm = userRepository.findById(dmId).map(u -> {
            u.setUserRepresentation(meditrackRealm.users().get(u.getId().toString()).toRepresentation());
            return u;
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dm.getTeam() == null) {
            throw new NoSuchElementException("Principal has no team");
        }
        return dm;
    }
}