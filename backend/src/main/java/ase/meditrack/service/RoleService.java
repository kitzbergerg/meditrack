package ase.meditrack.service;

import ase.meditrack.model.RoleValidator;
import ase.meditrack.model.dto.UserDto;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.mapper.RoleMapper;
import ase.meditrack.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class RoleService {
    private final RoleRepository repository;
    private final RoleMapper mapper;
    private final RoleValidator validator;

    public RoleService(RoleRepository repository, RoleMapper mapper, RoleValidator validator) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
    }

    /**
     * Fetches all roles from the database.
     *
     * @return List of all roles
     */
    public List<Role> findAll() {
        return repository.findAll();
    }

    /**
     * Creates a role in the database.
     *
     * @param role, the role to create
     * @return the created role
     */
    public Role create(Role role) throws ValidationException {
        if (!validator.validateCreate(role.getName())) {
            throw new ValidationException("Role creation failed.");
        };
        return repository.save(role);
    }

    /**
     * Updates a role in the database.
     *
     * @param roleToUpdate, the role to update
     * @return the updated role
     */
    public Role update(Role roleToUpdate) throws ValidationException {
        Optional<Role> currentRole = repository.findById(roleToUpdate.getId());
        Role role = null;
        if (currentRole.isPresent()) {
            role = currentRole.get();
        };
        if (!validator.validateUpdate(role.getName(), roleToUpdate.getName())) {
            throw new ValidationException("Role updating failed.");
        };
        Role updatedRole = new Role();
        updatedRole.setId(roleToUpdate.getId());
        updatedRole.setName(roleToUpdate.getName());
        updatedRole.setUsers(roleToUpdate.getUsers());

        repository.save(updatedRole);

        return updatedRole;
    }

    /**
     * Deletes a role from the database.
     *
     * @param id, the id of the role to delete
     */
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
