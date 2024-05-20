package ase.meditrack.controller;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.exception.ValidationException;
import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.RoleDto;
import ase.meditrack.model.mapper.RoleMapper;
import ase.meditrack.service.RoleService;
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
@RequestMapping("/api/role")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class RoleController {
    private final RoleService service;
    private final RoleMapper mapper;

    public RoleController(RoleService service, RoleMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public List<RoleDto> findAll() {
        log.info("Fetching roles");
        return mapper.toDtoList(service.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public RoleDto create(@Validated(CreateValidator.class) @RequestBody RoleDto dto) {
        log.info("Creating role {}", dto.name());
        try {
            return mapper.toDto(service.create(mapper.fromDto(dto)));
        } catch (ValidationException e) {
            log.error("ValidationException: POST /api/role/{} {}", dto.id(), dto, e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Error during creating role: " + e.getMessage(), e);
        }
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public RoleDto update(@Validated(UpdateValidator.class) @RequestBody RoleDto dto) {
        log.info("Updating role {}", dto.name());
        try {
            return mapper.toDto(service.update(mapper.fromDto(dto)));
        } catch (ValidationException e) {
            log.error("ValidationException: PUT /api/role/{} {}", dto.id(), dto, e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Error during editing role: " + e.getMessage(), e);
        } catch (NotFoundException e) {
            log.error("NotFoundException: PUT /api/role/{} {}", dto.id(), dto, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during editing role: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public void delete(@PathVariable UUID id) {
        log.info("Deleting role with id {}", id);
        service.delete(id);
    }
}
