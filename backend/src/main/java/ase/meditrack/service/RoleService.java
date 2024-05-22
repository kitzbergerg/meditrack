package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.RoleRepository;
import ase.meditrack.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class RoleService {
    private final RoleRepository repository;
    private final UserRepository userRepository;

    public RoleService(RoleRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    private User getPrincipalWithTeam(Principal principal) {
        UUID dmId = UUID.fromString(principal.getName());
        Optional<User> dm = userRepository.findById(dmId);
        if (dm.isEmpty()) {
            throw new NotFoundException("User doesnt exist");
        }
        if (dm.get().getTeam() == null) {
            throw new NotFoundException("User has no team");
        }
        return dm.get();
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
     * Fetches all roles from a team from the database.
     *
     * @return List of all roles
     */
    public List<Role> findAllByTeam(Principal principal) {
        User dm = getPrincipalWithTeam(principal);
        return repository.findAllByTeam(dm.getTeam());
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
    @Transactional
    public Role create(Role role, Principal principal) {
        User dm = getPrincipalWithTeam(principal);
        List<Role> roles = new ArrayList<>();
        if (dm.getTeam().getRoles() != null){
            roles = dm.getTeam().getRoles();
        }
        roles.add(role);
        dm.getTeam().setRoles(roles);
        role.setTeam(dm.getTeam());
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
