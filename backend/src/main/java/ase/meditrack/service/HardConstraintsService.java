package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.HardConstraints;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.HardConstraintsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class HardConstraintsService {
    private final HardConstraintsRepository repository;
    private final UserService userService;

    public HardConstraintsService(HardConstraintsRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    /**
     * Fetches all hard constraints from the database.
     *
     * @return List of all hard constraints
     */
    public List<HardConstraints> findAll() {
        return repository.findAll();
    }

    /**
     * Fetches a hard constraint by id from the database.
     *
     * @param id the id of the hard constraint
     * @return the hard constraint
     */
    public HardConstraints findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Could not find hard constraints with id: " + id + "!"));
    }

    /**
     * Fetches the hardconstraint from a team from the database
     * @param principal – the current user
     * @return the hardConstraints of the team
     */
    public HardConstraints findByTeam(Principal principal) {
        User dm = userService.getPrincipalWithTeam(principal);
        return repository.findByTeam(dm.getTeam());
    }

    /**
     * Creates a hard constraint in the database.
     *
     * @param hardConstraints the hard constraints to create
     * @param principal - the current user
     * @return the created hard constraints
     */
    public HardConstraints create(HardConstraints hardConstraints, Principal principal) {
        User dm = userService.getPrincipalWithTeam(principal);
        hardConstraints.setTeam(dm.getTeam());
        return repository.save(hardConstraints);
    }

    /**
     * Updates a hard constraints in the database.
     *
     * @param hardConstraints the hard constraints to update
     * @return the updated hard constraints
     */
    public HardConstraints update(HardConstraints hardConstraints) {
        HardConstraints dbHardConstraints = findById(hardConstraints.getId());

        if (hardConstraints.getShiftOffShift() != null) {
            dbHardConstraints.setShiftOffShift(hardConstraints.getShiftOffShift());
        }
        if (hardConstraints.getDaytimeRequiredRoles() != null) {
            dbHardConstraints.setDaytimeRequiredRoles(hardConstraints.getDaytimeRequiredRoles());
        }
        if (hardConstraints.getNighttimeRequiredRoles() != null) {
            dbHardConstraints.setNighttimeRequiredRoles(hardConstraints.getNighttimeRequiredRoles());
        }
        if (hardConstraints.getDaytimeRequiredPeople() != null) {
            dbHardConstraints.setDaytimeRequiredPeople(hardConstraints.getDaytimeRequiredPeople());
        }
        if (hardConstraints.getNighttimeRequiredPeople() != null) {
            dbHardConstraints.setNighttimeRequiredPeople(hardConstraints.getNighttimeRequiredPeople());
        }
        if (hardConstraints.getAllowedFlextimeTotal() != null) {
            dbHardConstraints.setAllowedFlextimeTotal(hardConstraints.getAllowedFlextimeTotal());
        }
        if (hardConstraints.getAllowedFlextimePerMonth() != null) {
            dbHardConstraints.setAllowedFlextimePerMonth(hardConstraints.getAllowedFlextimePerMonth());
        }
        if (hardConstraints.getTeam() != null) {
            dbHardConstraints.setTeam(hardConstraints.getTeam());
        }

        return repository.save(dbHardConstraints);
    }

    /**
     * Deletes a hard constraint from the database.
     *
     * @param id the id of the hard constraints to delete
     */
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
