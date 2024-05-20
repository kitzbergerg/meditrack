package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.Team;
import ase.meditrack.repository.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TeamService {
    private final TeamRepository repository;

    public TeamService(TeamRepository repository) {
        this.repository = repository;
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
     * @param id, the id of the team
     * @return the team
     */
    public Team findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Could not find team with id: " + id + "!"));
    }

    /**
     * Creates a team in the database.
     *
     * @param team, the team to create
     * @return the created team
     */
    public Team create(Team team) {
        return repository.save(team);
    }

    /**
     * Updates a team in the database.
     *
     * @param team, the team to update
     * @return the updated team
     */
    public Team update(Team team) {
        Team dbTeam = findById(team.getId());

        if (team.getName() != null) {
            dbTeam.setName(team.getName());
        }
        if (team.getWorkingHours() != null) {
            dbTeam.setWorkingHours(team.getWorkingHours());
        }
        if (team.getUsers() != null) {
            dbTeam.setUsers(team.getUsers());
        }
        if (team.getHardConstraints() != null) {
            dbTeam.setHardConstraints(team.getHardConstraints());
        }
        if (team.getMonthlyPlans() != null) {
            dbTeam.setMonthlyPlans(team.getMonthlyPlans());
        }
        if (team.getShiftTypes() != null) {
            dbTeam.setShiftTypes(team.getShiftTypes());
        }

        return repository.save(dbTeam);
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
