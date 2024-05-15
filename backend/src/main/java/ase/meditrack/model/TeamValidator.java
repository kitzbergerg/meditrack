package ase.meditrack.model;

import ase.meditrack.exception.ValidationException;
import ase.meditrack.model.entity.Team;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@NoArgsConstructor
public class TeamValidator {

    public void validateTeamOnCreate(Team team) {
        log.trace("Validating team on create: {}", team);
        if (team.getId() != null) {
            throw new ValidationException("Id must be null");
        }
        if (team.getName() == null || team.getName().isBlank()) {
            throw new ValidationException("Name must not be null and not blank");
        }
        if (team.getWorkingHours() == null || team.getWorkingHours() < 0) {
            throw new ValidationException("Working hours must not be null and not negative");
        }
    }

    public void validateTeamOnUpdate(Team team) {
        log.trace("Validating team on update: {}", team);
        if (team.getId() == null) {
            throw new ValidationException("Id must not be null");
        }
        if (team.getName() != null && team.getName().isBlank()) {
            throw new ValidationException("Name must not be blank");
        }
        if (team.getWorkingHours() != null && team.getWorkingHours() < 0) {
            throw new ValidationException("Working hours must not be negative");
        }
    }
}
