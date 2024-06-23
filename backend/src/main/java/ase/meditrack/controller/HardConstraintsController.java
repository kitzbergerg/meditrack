package ase.meditrack.controller;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.dto.HardConstraintsDto;
import ase.meditrack.model.dto.RoleHardConstraintsDto;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.mapper.HardConstraintsMapper;
import ase.meditrack.service.RoleService;
import ase.meditrack.service.TeamService;
import ase.meditrack.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rules")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class HardConstraintsController {
    private final TeamService teamService;
    private final RoleService roleService;
    private final HardConstraintsMapper mapper;
    private final UserService userService;

    public HardConstraintsController(TeamService teamService, RoleService roleService,
                                     HardConstraintsMapper mapper, UserService userService) {
        this.teamService = teamService;
        this.roleService = roleService;
        this.mapper = mapper;
        this.userService = userService;
    }

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm')")
    public HardConstraintsDto findByTeam(Principal principal) {
        log.info("Fetching hardConstraint");
        User dm = userService.getPrincipalWithTeam(principal);
        return mapper.toDto(teamService.findById(dm.getTeam().getId()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm')")
    public HardConstraintsDto create(
            @Validated(CreateValidator.class)
            @RequestBody HardConstraintsDto dto,
            Principal principal) {
        log.info("Updating hardConstraints");
        User dm = userService.getPrincipalWithTeam(principal);
        return mapper.toDto(teamService.updateTeamConstraints(dm, dto));
    }

    @GetMapping("/roleRules")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm')")
    public List<RoleHardConstraintsDto> findRoleHardConstraintsByTeam(Principal principal) {
        log.info("Fetching role hardConstraints");
        return roleService.findAllByTeam(principal)
                .stream().map(mapper::toRoleHardconstraintsDto).collect(Collectors.toList());
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
