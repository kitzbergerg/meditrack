package ase.meditrack.model;

import ase.meditrack.exception.ValidationException;
import ase.meditrack.model.entity.Preferences;
import ase.meditrack.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
public class PreferencesValidator {
    private final UserRepository repository;

    public PreferencesValidator(UserRepository repository) {
        this.repository = repository;
    }

    public void validatePreferences(Preferences preferences) {
        log.trace("Validating preferences: {}", preferences);

        if (preferences.getId() == null) {
            throw new ValidationException("Id must not be null");
        }
        if (preferences.getOffDays() == null) {
            throw new ValidationException("Off days must not be null");
        }
        if (preferences.getUser() == null) {
            throw new ValidationException("User must not be null");
        }
        if (!repository.existsById(preferences.getId())) {
            throw new ValidationException("Related User with id " + preferences.getId() + " does not exist");
        }
        preferences.getOffDays().forEach(offDay -> {
            if (offDay.isBefore(LocalDate.now())) {
                throw new ValidationException("Off day must not be in the past");
            }
        });
    }
}
