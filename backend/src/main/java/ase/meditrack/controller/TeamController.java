package ase.meditrack.controller;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.TeamDto;
import ase.meditrack.model.dto.UserDto;
import ase.meditrack.model.mapper.TeamMapper;
import ase.meditrack.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.xml.bind.ValidationException;
import java.lang.invoke.MethodHandles;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/team")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class TeamController {
    private final TeamService service;
    private final TeamMapper mapper;

    public TeamController(TeamService service, TeamMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public List<TeamDto> findAll() {
        log.info("Fetching teams");
        return mapper.toDtoList(service.findAll());
    }

    @GetMapping("{id}")
    @PreAuthorize("(hasAnyAuthority('SCOPE_dm') && @teamService.isTeamLeader(authentication.name, #id)) || hasAnyAuthority('SCOPE_admin')")
    public TeamDto findById(@PathVariable UUID id) {
        log.info("Fetching team {}", id);
        return mapper.toDto(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm')")
    @ResponseStatus(HttpStatus.CREATED)
    public TeamDto create(@Validated(CreateValidator.class) @RequestBody TeamDto dto, Principal principal) {
        log.info("Creating team {}", dto.id());
        return mapper.toDto(service.create(mapper.fromDto(dto), principal));
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    @ResponseStatus(HttpStatus.OK)
    public TeamDto update(@Validated(UpdateValidator.class) @RequestBody TeamDto dto) {
        log.info("Updating team {}", dto.id());
        return mapper.toDto(service.update(mapper.fromDto(dto)));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("Deleting team with id {}", id);
        service.delete(id);
    }
}
