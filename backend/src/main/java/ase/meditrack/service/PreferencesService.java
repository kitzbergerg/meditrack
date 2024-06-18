package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.Preferences;
import ase.meditrack.repository.PreferencesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.validation.ValidationException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class PreferencesService {
    private final PreferencesRepository repository;

    public PreferencesService(PreferencesRepository repository) {
        this.repository = repository;
    }

    /**
     * Fetches all preferences from the database.
     *
     * @return List of all preferences
     */
    public List<Preferences> findAll() {
        return repository.findAll();
    }

    /**
     * Fetches a preference by id from the database.
     *
     * @param id the id of the preference
     * @return the preference
     */
    public Preferences findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Could not find preference with id: " + id + "!"));
    }

    /**
     * Creates a preference in the database.
     *
     * @param preference the preference to create
     * @return the created preference
     */
    public Preferences create(Preferences preference) {
        if (preference.getOffDays().size() > 3) {
            throw new ValidationException("The maximum number of preferred off days can be 3.");
        }
        Set<LocalDate> uniqueOffDays = new HashSet<>(preference.getOffDays());
        if (uniqueOffDays.size() < preference.getOffDays().size()) {
            throw new ValidationException("All off days must be unique.");
        }
        return repository.save(preference);
    }

    /**
     * Updates a preference in the database.
     *
     * @param preference the preference to update
     * @return the updated preference
     */
    public Preferences update(Preferences preference) {
        Preferences dbPreferences = findById(preference.getId());

        if (preference.getOffDays() != null) {
            if (preference.getOffDays().size() > 3) {
                throw new ValidationException("The maximum number of preferred off days can be 3.");
            }
            Set<LocalDate> uniqueOffDays = new HashSet<>(preference.getOffDays());
            if (uniqueOffDays.size() < preference.getOffDays().size()) {
                throw new ValidationException("All off days must be unique.");
            }
            dbPreferences.setOffDays(preference.getOffDays());
        }

        return repository.save(dbPreferences);
    }

    /**
     * Deletes a preference from the database.
     *
     * @param id the id of the preference to delete
     */
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
