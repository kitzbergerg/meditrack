package ase.meditrack.model;

import ase.meditrack.exception.ValidationException;
import ase.meditrack.model.entity.HardConstraints;
import ase.meditrack.repository.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HardConstraintsValidator {
    private final TeamRepository teamRepository;

    public HardConstraintsValidator(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public void validateHardConstraintsOnCreate(HardConstraints hardConstraints) {
        log.trace("Validating hard constraints on create: {}", hardConstraints);

        if (hardConstraints.getId() == null) {
            throw new ValidationException("Id must be set");
        }
        if (!teamRepository.existsById(hardConstraints.getId())) {
            throw new ValidationException("Related team with id " + hardConstraints.getId() + " does not exist");
        }
        if (hardConstraints.getDaytimeRequiredPeople() == null || hardConstraints.getDaytimeRequiredPeople() < 0) {
            throw new ValidationException("Daytime required people must not be null or negative");
        }
        if (hardConstraints.getNighttimeRequiredPeople() == null || hardConstraints.getNighttimeRequiredPeople() < 0) {
            throw new ValidationException("Nighttime required people must not be null or negative");
        }
        if (hardConstraints.getAllowedFlextimeTotal() == null || hardConstraints.getAllowedFlextimeTotal() < 0) {
            throw new ValidationException("Allowed total flextime must not be null or negative");
        }
        if (hardConstraints.getAllowedFlextimePerMonth() == null || hardConstraints.getAllowedFlextimePerMonth() < 0) {
            throw new ValidationException("Allowed flextime per month must not be null or negative");
        }
    }

    public void validateHardConstraintsOnUpdate(HardConstraints hardConstraints) {
        log.trace("Validating hard constraints on update: {}", hardConstraints);

        if (hardConstraints.getId() == null) {
            throw new ValidationException("Id must be set");
        }
        if (!teamRepository.existsById(hardConstraints.getId())) {
            throw new ValidationException("Related team with id " + hardConstraints.getId() + " does not exist");
        }
        if (hardConstraints.getDaytimeRequiredPeople() != null && hardConstraints.getDaytimeRequiredPeople() < 0) {
            throw new ValidationException("Daytime required people must not be negative");
        }
        if (hardConstraints.getNighttimeRequiredPeople() != null && hardConstraints.getNighttimeRequiredPeople() < 0) {
            throw new ValidationException("Nighttime required people must not be negative");
        }
        if (hardConstraints.getAllowedFlextimeTotal() != null && hardConstraints.getAllowedFlextimeTotal() < 0) {
            throw new ValidationException("Allowed total flextime must not negative");
        }
        if (hardConstraints.getAllowedFlextimePerMonth() != null && hardConstraints.getAllowedFlextimePerMonth() < 0) {
            throw new ValidationException("Allowed flextime per month must not be negative");
        }
    }
}
