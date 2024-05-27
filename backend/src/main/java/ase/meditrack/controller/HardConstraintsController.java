package ase.meditrack.controller;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.dto.HardConstraintsDto;
import ase.meditrack.model.mapper.HardConstraintsMapper;
import ase.meditrack.service.HardConstraintsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

@RestController
@RequestMapping("/api/rules")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class HardConstraintsController {
    private final HardConstraintsService service;
    private final HardConstraintsMapper mapper;

    public HardConstraintsController(HardConstraintsService service, HardConstraintsMapper mapper) {
        this.service = service;
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
        log.info("Creating hardConstraints");
        return mapper.toDto(service.create(mapper.fromDto(dto), principal));
    }
}
