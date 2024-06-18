package ase.meditrack.controller;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.MonthlyWorkDetailsDto;
import ase.meditrack.model.dto.UserDto;
import ase.meditrack.model.mapper.MonthlyWorkDetailsMapper;
import ase.meditrack.model.mapper.UserMapper;
import ase.meditrack.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.Month;
import java.time.Year;
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
    private final MonthlyWorkDetailsMapper monthlyWorkDetailsMapper;

    public UserController(UserService service, UserMapper mapper, MonthlyWorkDetailsMapper monthlyWorkDetailsMapper) {
        this.service = service;
        this.mapper = mapper;
        this.monthlyWorkDetailsMapper = monthlyWorkDetailsMapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public List<UserDto> findAll() {
        log.info("Fetching users");
        return mapper.toDtoList(service.findAll());
    }

    @GetMapping("/team")
    @PreAuthorize("hasAnyAuthority('SCOPE_dm', 'SCOPE_employee')")
    public List<UserDto> findByTeam(Principal principal) {
        log.info("Fetching users from dm team");
        try {
            return mapper.toDtoList(service.findByTeam(principal));
        } catch (NoSuchElementException e) {
            log.error("NoSuchElementException: GET /api/user/team", e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Error during getting users by team: " + e.getMessage(), e);
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
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || (hasAnyAuthority('SCOPE_dm') && "
            + "@userService.isCorrectUserSystemRole(#dto.roles(), #principal))")
    public UserDto create(@Validated(CreateValidator.class) @RequestBody UserDto dto, Principal principal) {
        log.info("Creating user {}", dto.username());
        return mapper.toDto(service.create(mapper.fromDto(dto)));
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') "
            + "|| (authentication.name == #dto.id().toString() && #dto.roles() == null) "
            + "|| (hasAnyAuthority('SCOPE_dm') && @userService.isSameTeam(#principal, #dto.id()))")
    public UserDto update(@Validated(UpdateValidator.class) @RequestBody UserDto dto, Principal principal) {
        log.info("Updating user {}", dto.username());
        return mapper.toDto(service.update(mapper.fromDto(dto), principal));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')"
            + "|| (hasAnyAuthority('SCOPE_dm') && @userService.isSameTeam(#principal, #id))")
    public void delete(@PathVariable UUID id, Principal principal) {
        log.info("Deleting user with id {}", id);
        service.delete(id, principal);
    }

    @GetMapping("/monthly-details")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || (hasAnyAuthority('SCOPE_dm', 'SCOPE_employee') && "
            + "@userService.isSameTeam(#principal, #userId))")
    public MonthlyWorkDetailsDto getMonthlyWorkDetails(@RequestParam Year year,
                                                       @RequestParam Month month, @RequestParam UUID userId,
                                                       Principal principal) {
        log.info("Fetching monthly work details from user");
        try {
            return monthlyWorkDetailsMapper.toDto(service.findWorkDetailsByIdAndMonthAndYear(userId, month, year));
        } catch (NoSuchElementException e) {
            log.error("NoSuchElementException: GET /api/user/monthly-details", e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Error getting monthly details from user: " + e.getMessage(), e);
        }
    }
}
