package ase.meditrack.service;

import ase.meditrack.model.dto.HardConstraintsDto;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.TeamRepository;
import ase.meditrack.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TeamService {
    private final TeamRepository repository;
    private final UserRepository userRepository;

    public TeamService(TeamRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
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
     * Fetches a team by id from the database.
     *
     * @param id the id of the team
     * @return the team
     */
    public Team findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Team was not found"));
    }

    /**
     * Creates a team in the database.
     *
     * @param team      the team to create
     * @param principal the current user's id
     * @return the created team
     */
    @Transactional
    public Team create(Team team, Principal principal) {
        UUID creatorId = UUID.fromString(principal.getName());
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<User> users = new ArrayList<>();
        users.add(creator);
        team.setUsers(users);
        creator.setTeam(team);
        team.setDaytimeRequiredPeople(0);
        team.setNighttimeRequiredPeople(0);

        return repository.save(team);
    }

    /**
     * Updates a team in the database.
     *
     * @param team the team to update
     * @return the updated team
     */
    public Team update(Team team) {
        Team dbTeam = findById(team.getId());
        if (team.getName() != null) {
            dbTeam.setName(team.getName());
        }
        if (team.getUsers() != null) {
            dbTeam.setUsers(team.getUsers());
        }
        if (team.getMonthlyPlans() != null) {
            dbTeam.setMonthlyPlans(team.getMonthlyPlans());
        }
        if (team.getShiftTypes() != null) {
            dbTeam.setShiftTypes(team.getShiftTypes());
        }
        if (team.getDaytimeRequiredPeople() != null) {
            dbTeam.setDaytimeRequiredPeople(team.getDaytimeRequiredPeople());
        }
        if (team.getNighttimeRequiredPeople() != null) {
            dbTeam.setNighttimeRequiredPeople(team.getNighttimeRequiredPeople());
        }

        return repository.save(dbTeam);
    }

    /**
     * Deletes a team from the database.
     *
     * @param id the id of the team to delete
     */
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    /**
     * Checks if a user is in the team.
     *
     * @param userId the user to check for
     * @param teamId the team to check
     * @return true if the user is in the team, false otherwise
     */
    public boolean isInTeam(UUID userId, UUID teamId) {
        List<User> users = repository.findById(teamId).get().getUsers();
        User user = userRepository.findById(userId).get();
        return users.contains(user);
    }


    /** update team constraints.
     * @param user user.
     * @param dto dto.
     * @return team.
     */
    public Team updateTeamConstraints(User user, HardConstraintsDto dto) {
        Team team = this.findById(user.getTeam().getId());
        team.setDaytimeRequiredPeople(dto.daytimeRequiredPeople());
        team.setNighttimeRequiredPeople(dto.nighttimeRequiredPeople());
        return update(team);
    }
}
