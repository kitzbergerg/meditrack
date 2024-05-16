package ase.meditrack.service;

import ase.meditrack.model.entity.Team;
import ase.meditrack.repository.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TeamService {
    private final TeamRepository repository;

    public TeamService(TeamRepository repository) {
        this.repository = repository;
    }

    public Team findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<Team> findAll() {
        return repository.findAll();
    }

    public Team create(Team team) {
        team.setRoles(new ArrayList<>());
        return repository.save(team);

    }

    public Team update(Team team) {
        Team existing = repository.findById(team.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (team.getName() != null) {
            existing.setName(team.getName());
        }
        if (team.getWorkingHours() != null) {
            existing.setWorkingHours(team.getWorkingHours());
        }
        if (team.getUsers() != null) {
            existing.setUsers(team.getUsers());
        }
        if (team.getHardConstraints() != null) {
            existing.setHardConstraints(team.getHardConstraints());
        }
        if (team.getMonthlyPlans() != null) {
            existing.setMonthlyPlans(team.getMonthlyPlans());
        }
        if (team.getShiftTypes() != null) {
            existing.setShiftTypes(team.getShiftTypes());
        }

        return repository.save(existing);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
