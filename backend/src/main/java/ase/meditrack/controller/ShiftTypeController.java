package ase.meditrack.controller;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.ShiftTypeDto;
import ase.meditrack.model.mapper.ShiftTypeMapper;
import ase.meditrack.service.ShiftTypeService;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shift-type")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class ShiftTypeController {
    private final ShiftTypeService service;
    private final ShiftTypeMapper mapper;

    public ShiftTypeController(ShiftTypeService service, ShiftTypeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public List<ShiftTypeDto> findAll() {
        log.info("Fetching shift types");
        return mapper.toDtoList(service.findAll());
    }

    @GetMapping("/team")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm', 'SCOPE_employee')")
    public List<ShiftTypeDto> findAllByTeam(Principal principal) {
        log.info("Fetching shift types from team");
        return mapper.toDtoList(service.findAllByTeam(principal));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || "
            + "(hasAnyAuthority('SCOPE_dm') && @teamService.isInTeam(authentication.name, #dto.team()))")
    public ShiftTypeDto create(@Validated(CreateValidator.class) @RequestBody ShiftTypeDto dto, Principal principal) {
        log.info("Creating shift type {}", dto.name());
        return mapper.toDto(service.create(mapper.fromDto(dto), principal));
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || "
            + "(hasAnyAuthority('SCOPE_dm') && @teamService.isInTeam(authentication.name, #dto.team()))")
    public ShiftTypeDto update(@Validated(UpdateValidator.class) @RequestBody ShiftTypeDto dto) {
        log.info("Updating shift type {}", dto.name());
        return mapper.toDto(service.update(mapper.fromDto(dto)));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || "
            + "(hasAnyAuthority('SCOPE_dm') && @shiftTypeService.isShiftTypeInTeam(authentication.name, #id))")
    public void delete(@PathVariable UUID id) {
        log.info("Deleting shift type with id {}", id);
        service.delete(id);
    }
}
