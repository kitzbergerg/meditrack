package ase.meditrack.controller;

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

import java.security.Principal;
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

    @GetMapping("/team")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm', 'SCOPE_employee')")
    public List<RoleDto> findAllByTeam(Principal principal) {
        log.info("Fetching roles for team");
        return mapper.toDtoList(service.findAllByTeam(principal));
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') ||"
            + "(hasAnyAuthority('SCOPE_dm', 'SCOPE_employee') && @roleService.isRoleFromTeam(#principal, #id))")
    public RoleDto findById(@PathVariable UUID id, Principal principal) {
        log.info("Fetching role {}", id);
        return mapper.toDto(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') ||"
            + "(hasAnyAuthority('SCOPE_dm') && @roleService.isUserTeamSameAsRoleTeam(#principal, #dto.team()))")
    public RoleDto create(@Validated(CreateValidator.class) @RequestBody RoleDto dto, Principal principal) {
        log.info("Creating role {}", dto.name());
        return mapper.toDto(service.create(mapper.fromDto(dto), principal));
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') ||"
            + "(hasAnyAuthority('SCOPE_dm') && @roleService.isRoleFromTeam(#principal, #dto.id()))")
    public RoleDto update(@Validated(UpdateValidator.class) @RequestBody RoleDto dto, Principal principal) {
        log.info("Updating role {}", dto.name());
        return mapper.toDto(service.update(mapper.fromDto(dto)));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') ||"
            + "(hasAnyAuthority('SCOPE_dm') && @roleService.isRoleFromTeam(#principal, #id))")
    public void delete(@PathVariable UUID id, Principal principal) {
        log.info("Deleting role with id {}", id);
        service.delete(id);
    }
}
