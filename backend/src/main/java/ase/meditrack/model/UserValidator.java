package ase.meditrack.model;

import ase.meditrack.exception.ValidationException;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.RoleRepository;
import ase.meditrack.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import org.keycloak.representations.idm.UserRepresentation;
import java.util.List;
import java.util.UUID;

@Component
public class UserValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public UserValidator() {
    }

    public void createValidate(User user) throws ValidationException {
        LOGGER.trace("Makes validation for {}", user);

        UserRepresentation userRepresentation = user.getUserRepresentation();

        String username = userRepresentation.getUsername();
        String first = userRepresentation.getFirstName();
        String last = userRepresentation.getLastName();
        String email = userRepresentation.getEmail();
        Float workingHoursPercentage = user.getWorkingHoursPercentage();

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
}