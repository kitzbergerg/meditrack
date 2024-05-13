package ase.meditrack.service;

import ase.meditrack.model.entity.Preferences;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.PreferencesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PreferencesService {
    private final PreferencesRepository repository;
    private final UserService userService;

    public PreferencesService(PreferencesRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public Preferences findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<Preferences> findAll() {
        return repository.findAll();
    }

    public Preferences create(Preferences preferences) {
        //if the user (mapped id) does not exist an error will be thrown
        User user = userService.findById(preferences.getId());
        preferences.setUser(user);
        return repository.save(preferences);
    }

    public Preferences update(Preferences preferences) {
        Preferences existing = repository.findById(preferences.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (preferences.getOffDays() != null) {
            existing.setOffDays(preferences.getOffDays());
        }
        if (preferences.getUser() != null) {
            existing.setUser(preferences.getUser());
        }

        return repository.save(existing);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
