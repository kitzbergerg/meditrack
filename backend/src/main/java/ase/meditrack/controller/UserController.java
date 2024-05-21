package ase.meditrack.controller;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.TeamDto;
import ase.meditrack.model.dto.UserDto;
import ase.meditrack.model.mapper.UserMapper;
import ase.meditrack.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class UserController {
    private final UserService service;
    private final UserMapper mapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public UserController(UserService service, UserMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm')")
    public List<UserDto> findAll() {
        log.info("Fetching users");
        return mapper.toDtoList(service.findAll());
    }

    @GetMapping("/team")
    @PreAuthorize("hasAnyAuthority('SCOPE_dm')")
    public List<UserDto> findByTeam(Principal principal) {
        log.info("Fetching users from dm team");
        try {
            return mapper.toDtoList(service.findByTeam(principal));
        } catch (NoSuchElementException e) {
            LOGGER.error("NoSuchElementException: GET /api/user/team", e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Error during getting users by team: " + e.getMessage(), e);
        }
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || authentication.name == #id.toString()")
    public UserDto findById(@PathVariable UUID id) {
        log.info("Fetching user {}", id);
        return mapper.toDto(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm')")
    public UserDto create(@Validated(CreateValidator.class) @RequestBody UserDto dto, Principal principal) {
        log.info("Creating user {}", dto.username());
        return mapper.toDto(service.create(mapper.fromDto(dto), principal));
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm') || (authentication.name == #dto.id().toString() && #dto.roles() == null)")
    public UserDto update(@Validated(UpdateValidator.class) @RequestBody UserDto dto, Principal principal) {
        log.info("Updating user {}", dto.username());
        return mapper.toDto(service.update(mapper.fromDto(dto), principal));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm') || authentication.name == #id.toString()")
    public void delete(@PathVariable UUID id, Principal principal) {
        log.info("Deleting user with id {}", id);
        service.delete(id, principal);
    }
}
