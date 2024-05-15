package ase.meditrack.controller;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.exception.ValidationException;
import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.TeamDto;
import ase.meditrack.model.mapper.TeamMapper;
import ase.meditrack.service.TeamService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public TeamDto findById(@PathVariable UUID id) {
        log.info("Fetching team with id: {}", id);
        try {
            return mapper.toDto(service.findById(id));
        } catch (NotFoundException e) {
            log.error("NotFoundException: GET /api/team/{}", id, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team with id: " + id + " not found", e);
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    @ResponseStatus(HttpStatus.CREATED)
    public TeamDto create(@Validated(CreateValidator.class) @RequestBody TeamDto dto) {
        log.info("Creating team {}", dto.id());
        try {
            return mapper.toDto(service.create(mapper.fromDto(dto)));
        } catch (ValidationException e) {
            log.error("ValidationException: POST /api/team/{} {}", dto.id(), dto, e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Team with id: " + dto.id() +
                    " not found", e);
        }
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    @ResponseStatus(HttpStatus.OK)
    public TeamDto update(@Validated(UpdateValidator.class) @RequestBody TeamDto dto) {
        log.info("Updating team {}", dto.id());
        try {
            return mapper.toDto(service.update(mapper.fromDto(dto)));
        } catch (ValidationException e) {
            log.error("ValidationException: PUT /api/team/{} {}", dto.id(), dto, e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Team with id: " + dto.id() + " not " +
                    "found", e);
        } catch (NotFoundException e) {
            log.error("NotFoundException: PUT /api/team/{} {}", dto.id(), dto, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team with id: " + dto.id() + " not found", e);
        }
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("Deleting team with id {}", id);
        service.delete(id);
    }
}
