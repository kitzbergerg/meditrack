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

        String name = role.getName();

        if (role.getId() != null) {
            throw new ValidationException("Id should not be set");
        }
        if (name == null || name.isBlank()) {
            throw new ValidationException("Role has to have a name");
        }
        if (!isNameUnique(role.getName())) {
            throw new ValidationException("Role with this name already exists");
        }
        if (name.length() >= 40) {
            throw new ValidationException("Name is too long");
        }
    }

    public void roleUpdateValidation(Role roleToUpdate) throws ValidationException {
        LOGGER.trace("Makes update validation for {}", roleToUpdate);

        String name = roleToUpdate.getName();
        UUID id = roleToUpdate.getId();

        if (!repository.existsById(id)){
            throw new ValidationException("Id is not correct");
        }
        if (name == null || name.isBlank()) {
            throw new ValidationException("Role has to have a name");
        }
        if (!isNameUnique(roleToUpdate.getName())) {
            throw new ValidationException("Role with this name already exists");
        }
        if (name.length() >= 40) {
            throw new ValidationException("Name is too long");
        }
    }

    public boolean isNameUnique(String name) {
        List<Role> allRoles = repository.findAll();
        for (Role role : allRoles) {
            if (role.getName().equals(name)) {
                return false;
            }
        }
        return true;
    }

}