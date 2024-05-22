package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.Role;
import ase.meditrack.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class RoleService {
    private final RoleRepository repository;

    public RoleService(RoleRepository repository) {
        this.repository = repository;
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
     * Fetches a role by id from the database.
     *
     * @param id the id of the role
     * @return the role
     */
    public Role findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Could not find user with id: " + id + "!"));
    }

    /**
     * Creates a role in the database.
     *
     * @param role the role to create
     * @return the created role
     */
    public Role create(Role role) {
        return repository.save(role);
    }

    /**
     * Updates a role in the database.
     *
     * @param role the role to update
     * @return the updated role
     */
    public Role update(Role role) {
        Role dbRole = repository.findById(role.getId())
                .orElseThrow(() -> new NotFoundException("Role not found"));

        if (role.getName() != null) {
            dbRole.setName(role.getName());
        }
        if (role.getUsers() != null) {
            dbRole.setUsers(role.getUsers());
        }

        return repository.save(dbRole);
    }

    /**
     * Deletes a role from the database.
     *
     * @param id the id of the role to delete
     */
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
