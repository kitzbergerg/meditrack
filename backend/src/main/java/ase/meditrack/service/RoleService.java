package ase.meditrack.service;

import ase.meditrack.model.RoleValidator;
import ase.meditrack.model.dto.UserDto;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.mapper.RoleMapper;
import ase.meditrack.repository.RoleRepository;
import ase.meditrack.repository.TeamRepository;
import ase.meditrack.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class RoleService {
    private final RoleRepository repository;

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final RoleMapper mapper;
    private final RoleValidator validator;

    public RoleService(RoleRepository repository, RoleMapper mapper, RoleValidator validator, TeamRepository teamRepository, UserRepository userRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
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
    public Role create(Role role, Principal principal) throws ValidationException {
        Team team = getTeamFromPrincipal(principal);
        validator.roleCreateValidation(role);
        role.setTeam(team);
        team.getRoles().add(role);
        return repository.save(role);
    }

    /**
     * Updates a role in the database.
     *
     * @param roleToUpdate, the role to update
     * @return the updated role
     */
    public Role update(Role roleToUpdate, Principal principal) throws ValidationException {

        Team team = getTeamFromPrincipal(principal);

        validator.roleUpdateValidation(roleToUpdate);

        Role updatedRole = new Role();
        updatedRole.setId(roleToUpdate.getId());
        updatedRole.setName(roleToUpdate.getName());
        updatedRole.setUsers(roleToUpdate.getUsers());
        updatedRole.setTeam(team);

        repository.save(updatedRole);

        return updatedRole;
    }

    /**
     * Gets Team of principal
     *
     * @param principal Principal of the dm
     * @return Team of the principal
     * @throws ValidationException when team or user cannot be found
     */
    private Team getTeamFromPrincipal(Principal principal) throws ValidationException {
        UUID userId = UUID.fromString(principal.getName());
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            throw new ValidationException("User not found");
        }
        Team team = user.get().getTeam();
        if(team == null) {
            throw new ValidationException("Role has to have a team");
        }
        return team;
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
