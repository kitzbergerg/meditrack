package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.dto.RoleHardConstraintsDto;
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
import java.util.UUID;

@Service
@Slf4j
public class RoleService {
    private final RoleRepository repository;
    private final UserRepository userRepository;
    private final UserService userService;

    public RoleService(RoleRepository repository, UserRepository userRepository, UserService userService) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.userService = userService;
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
     * @param principal the current user
     * @return List of all roles
     */
    public List<Role> findAllByTeam(Principal principal) {
        User dm = userService.getPrincipalWithTeam(principal);
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
     * @param principal the current user
     * @param role the role to create
     * @return the created role
     */
    @Transactional
    public Role create(Role role, Principal principal) {
        User dm = userService.getPrincipalWithTeam(principal);
        List<Role> roles = new ArrayList<>();
        if (dm.getTeam().getRoles() != null) {
            roles = dm.getTeam().getRoles();
        }

        role.setDaytimeRequiredPeople(0);
        role.setNighttimeRequiredPeople(0);
        role.setAllowedFlextimeTotal(0);
        role.setAllowedFlextimePerMonth(0);

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
        if (role.getColor() != null) {
            dbRole.setColor(role.getColor());
        }
        if (role.getAbbreviation() != null) {
            dbRole.setAbbreviation(role.getAbbreviation());
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


    /**
     * @param dto for which to update role hard constraints
     * @return updated role
     */
    public Role updateRoleConstraints(RoleHardConstraintsDto dto) {
        Role  role = findById(dto.roleId());
        role.setAllowedFlextimeTotal(dto.allowedFlextimeTotal());
        role.setAllowedFlextimePerMonth(dto.allowedFlextimePerMonth());
        role.setDaytimeRequiredPeople(dto.daytimeRequiredPeople());
        role.setNighttimeRequiredPeople(dto.nighttimeRequiredPeople());
        repository.save(role);
        return role;
    }
}
