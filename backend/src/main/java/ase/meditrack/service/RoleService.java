package ase.meditrack.service;

import ase.meditrack.model.dto.UserDto;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.mapper.RoleMapper;
import ase.meditrack.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class RoleService {
    private final RoleRepository repository;
    private final RoleMapper mapper;

    public RoleService(RoleRepository repository, RoleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
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
    public Role create(Role role) {
        return repository.save(role);
    }

    /**
     * Updates a role in the database.
     *
     * @param role, the role to update
     * @return the updated role
     */
    public Role update(Role role) {
        Role updatedRole = new Role();
        updatedRole.setId(role.getId());
        updatedRole.setName(role.getName());
        updatedRole.setUsers(role.getUsers());

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
