package ase.meditrack.controller;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.dto.HardConstraintsDto;
import ase.meditrack.model.dto.RoleHardConstraintsDto;
import ase.meditrack.model.mapper.HardConstraintsMapper;
import ase.meditrack.service.HardConstraintsService;
import ase.meditrack.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rules")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class HardConstraintsController {
    private final HardConstraintsService service;
    private final RoleService roleService;
    private final HardConstraintsMapper mapper;

    public HardConstraintsController(HardConstraintsService service, RoleService roleService, HardConstraintsMapper mapper) {
        this.service = service;
        this.roleService = roleService;
        this.mapper = mapper;
    }

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm')")
    public HardConstraintsDto findByTeam(Principal principal) {
        log.info("Fetching hardConstraint");
        return mapper.toDto(service.findByTeam(principal));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm')")
    public HardConstraintsDto create(
            @Validated(CreateValidator.class)
            @RequestBody HardConstraintsDto dto,
            Principal principal) {
        log.info("Updating hardConstraints");
        return mapper.toDto(service.update(mapper.fromDto(dto), principal));
    }

    @GetMapping("/roleRules")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm')")
    public List<RoleHardConstraintsDto> findRoleHardConstraintsByTeam(Principal principal) {
        log.info("Fetching role hardConstraints");
        return roleService.findAll().stream().map(mapper::toRoleHardconstraintsDto).collect(Collectors.toList());
    }

    @PutMapping("/roleRules")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm')")
    public RoleHardConstraintsDto updateRoleHardConstraints(
            @Validated(CreateValidator.class)
            @RequestBody RoleHardConstraintsDto dto,
            Principal principal) {
        log.info("Creating hardConstraints");
        return mapper.toRoleHardconstraintsDto(roleService.updateRoleConstraints(dto));
    }
}
