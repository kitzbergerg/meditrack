package ase.meditrack.controller;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.RoleDto;
import ase.meditrack.model.mapper.RoleMapper;
import ase.meditrack.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.xml.bind.ValidationException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;

@RestController
@RequestMapping("/api/role")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class RoleController {
    private final RoleService service;
    private final RoleMapper mapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
            LOGGER.error("ValidationException: POST /api/role/{} {}", dto.id(), dto, e);
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
            LOGGER.error("ValidationException: PUT /api/role/{} {}", dto.id(), dto, e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Error during editing role: " + e.getMessage(), e);
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
