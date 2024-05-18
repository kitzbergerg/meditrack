package ase.meditrack.model;

import ase.meditrack.exception.ValidationException;
import ase.meditrack.model.entity.Role;
import ase.meditrack.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.UUID;

@Component
public class RoleValidator {

    private final RoleRepository repository;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public RoleValidator(RoleRepository repository) {
        this.repository = repository;
    }

    public void roleCreateValidation(Role role) throws ValidationException {
        LOGGER.trace("Makes create validation for {}", role);

        if (role.getId() != null) {
            throw new ValidationException("Id should not be set");
        }

        validateStringInput(role.getName(), "name");
        validateStringInput(role.getColor(), "color");
        validateStringInput(role.getAbbreviation(), "abbreviation");
    }

    public void roleUpdateValidation(Role roleToUpdate) throws ValidationException {
        LOGGER.trace("Makes update validation for {}", roleToUpdate);

        UUID id = roleToUpdate.getId();

        if (id == null) {
            throw new ValidationException("Id should be set");
        }
        if (!repository.existsById(id)){
            throw new ValidationException("Id is not correct");
        }

        Role mergedRole = repository.getById(id);

        if (roleToUpdate.getName() != null) {
            if (!(mergedRole.getName().equals(roleToUpdate.getName()))) {
                validateStringInput(roleToUpdate.getName(), "name");
            }
            mergedRole.setName(roleToUpdate.getName());
        }
        if (roleToUpdate.getColor() != null) {
            if (!(mergedRole.getColor().equals(roleToUpdate.getColor()))) {
                validateStringInput(roleToUpdate.getColor(), "color");
            }
            mergedRole.setColor(roleToUpdate.getColor());
        }
        if (roleToUpdate.getAbbreviation() != null) {
            if (!(mergedRole.getAbbreviation().equals(roleToUpdate.getAbbreviation()))) {
                validateStringInput(roleToUpdate.getAbbreviation(), "abbreviation");
            }
            mergedRole.setAbbreviation(roleToUpdate.getAbbreviation());
        }
    }

    public boolean isUnique(String input, String attribute) {
        List<Role> allRoles = repository.findAll();
        for (Role role : allRoles) {
            switch (attribute) {
                case "name" -> {
                    if (role.getName().equals(input)) {
                        return false;
                    }
                }
                case "abbreviation" -> {
                    if (role.getAbbreviation().equals(input)) {
                        return false;
                    }
                }
                case "color" -> {
                    if (role.getColor().equals(input)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void validateStringInput(String input, String attribute) {
        if (input == null || input.isBlank()) {
            throw new ValidationException("Role has to have a " + attribute);
        }
        if (!attribute.equals("abbreviation") && input.length() >= 40) {
            throw new ValidationException("Role " + attribute + " is too long");
        }
        if (attribute.equals("abbreviation")) {
            if (input.length() > 4) {
                throw new ValidationException("Role " + attribute + " is too long");
            }
            if (input.matches(".*\\d.*")) {
                throw new ValidationException("Role " + attribute + " cannot contain digits");
            }
            if (!isUnique(input, "abbreviation")) {
                throw new ValidationException("Role with this abbreviation already exists");
            }
        }
        if (attribute.equals("name") && !isUnique(input, "name")) {
            throw new ValidationException("Role with this name already exists");
        }
        if (attribute.equals("color") && !isUnique(input, "color")) {
            throw new ValidationException("Role with this color already exists");
        }

        // TODO: validate color (?)
    }

}