package ase.meditrack.model;

import ase.meditrack.model.entity.Role;
import ase.meditrack.repository.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class RoleValidator {
    private static final Pattern NAME_PATTERN = Pattern.compile("[A-Z][a-zA-Z]*\\s[A-Z][a-zA-Z]*");

    private RoleRepository repository;
    private List<Role> allRoles;

    public RoleValidator(RoleRepository repository) {
        this.repository = repository;
    }

    public boolean isNameUnique(String name) {
        allRoles = repository.findAll();
        if (allRoles.isEmpty()) {
            return true;
        }
        return !allRoles.contains(name);
    }

    public boolean isValidName(String name) {
        return NAME_PATTERN.matcher(name).matches();
    }

    public boolean validateCreate(String name) {
        return isNameUnique(name) && isValidName(name);
    }

    public boolean validateUpdate(String oldName, String newName) {
        if (oldName.equals(newName)) {
            return true;
        }
        return isNameUnique(newName) && isValidName(newName);
    }
}
