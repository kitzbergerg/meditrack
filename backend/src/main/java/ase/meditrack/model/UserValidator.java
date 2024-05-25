package ase.meditrack.model;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class UserValidator {
    private UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void teamValidate(UUID userId, Principal principal) {
        log.trace("Validate if team of dm {} and user {} match.", userId, principal.getName());
        Optional<User> dm = userRepository.findById(UUID.fromString(principal.getName()));
        if (dm.isEmpty()) {
            throw new NotFoundException("DM account could not be found");
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User to delete could not be found");
        }
        if (!user.get().getTeam().equals(dm.get().getTeam())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You are not authorized to modify an account that is not part of your team");
        }
    }
}
