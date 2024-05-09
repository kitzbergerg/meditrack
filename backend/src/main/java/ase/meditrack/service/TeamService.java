package ase.meditrack.service;

import ase.meditrack.model.TeamValidator;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.mapper.TeamMapper;
import ase.meditrack.repository.TeamRepository;
import ase.meditrack.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.beans.Transient;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class TeamService {
    private final TeamRepository repository;
    private final UserRepository userRepository;
    private final TeamMapper mapper;
    private final TeamValidator validator;

    public TeamService(TeamRepository repository, TeamMapper mapper, TeamValidator validator, UserRepository userRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
        this.userRepository = userRepository;
    }

    public boolean isTeamLeader(UUID userId, UUID teamId){

        List<User> users = repository.findById(teamId).get().getUsers();
        User user = userRepository.findById(userId).get();
        return users.contains(user);

    }

    /**
     * Fetches all teams from the database.
     *
     * @return List of all teams
     */
    public List<Team> findAll() {
        return repository.findAll();
    }

    /**
     * Fetches a team by id from the database
     *
     * @param id, the id of the team
     * @return the team
     */
    public Team findById(UUID id) {
        Optional<Team> team = repository.findById(id);
        if(!team.isPresent()) {
            throw new NotFoundException("Team was not found");
        }
        return team.get();
    }

    /**
     * Creates a team in the database.
     *
     * @param team, the team to create
     * @return the created team
     */
    @Transactional
    public Team create(Team team, Principal principal) throws ValidationException {
        //validator.teamCreateValidation(team);
        UUID creatorId = UUID.fromString(principal.getName());
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<User> users = new ArrayList<>();
        users.add(creator);
        team.setUsers(users);
        creator.setTeam(team);
        return repository.save(team);
    }

    /**
     * Updates a team in the database.
     *
     * @param teamToUpdate, the team to update
     * @return the updated team
     */
    public Team update(Team teamToUpdate) throws ValidationException {
        //validator.teamUpdateValidation(teamToUpdate);

        Team updatedTeam = new Team();
        updatedTeam.setId(teamToUpdate.getId());
        updatedTeam.setName(teamToUpdate.getName());
        updatedTeam.setUsers(teamToUpdate.getUsers());
        // update other parts of team
        repository.save(updatedTeam);

        return updatedTeam;
    }

    /**
     * Deletes a team from the database.
     *
     * @param id, the id of the team to delete
     */
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
