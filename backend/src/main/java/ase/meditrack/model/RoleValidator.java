package ase.meditrack.model;

import ase.meditrack.model.entity.Role;
import ase.meditrack.repository.RoleRepository;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Component
public class RoleValidator {

    private RoleRepository roleRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public RoleValidator(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void roleValidation(Role role) throws ValidationException {
        LOGGER.trace("Makes validation for {}", role);

        List<Role> roles = roleRepository.findAll();

        for (Role st:roles) {
            if (role.getName().equals(st.getName())) {
                throw new ValidationException("Name has to be unique.");
            }
            if (role.getColor().equals(st.getColor())) {
                throw new ValidationException("Color has to be unique.");
            }
            if (role.getAbbreviation().equals(st.getAbbreviation())) {
                throw new ValidationException("Abbreviation has to be unique.");
            }
        }
    }
}