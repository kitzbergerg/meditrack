package ase.meditrack.controller;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.UserDto;
import ase.meditrack.model.mapper.UserMapper;
import ase.meditrack.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class UserController {
    private final UserService service;
    private final UserMapper mapper;

    public UserController(UserService service, UserMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public List<UserDto> findAll() {
        log.info("Fetching users");
        return mapper.toDtoList(service.findAll());
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || authentication.name == #id.toString()")
    public UserDto findById(@PathVariable UUID id) {
        log.info("Fetching user {}", id);
        return mapper.toDto(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public UserDto create(@Validated(CreateValidator.class) @RequestBody UserDto dto) {
        log.info("Creating user {}", dto.username());
        return mapper.toDto(service.create(mapper.fromDto(dto)));
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || (authentication.name == #dto.id().toString() && #dto.roles() == null)")
    public UserDto update(@Validated(UpdateValidator.class) @RequestBody UserDto dto) {
        log.info("Updating user {}", dto.username());
        return mapper.toDto(service.update(mapper.fromDto(dto)));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || authentication.name == #id.toString()")
    public void delete(@PathVariable UUID id) {
        log.info("Deleting user with id {}", id);
        service.delete(id);
    }
}
