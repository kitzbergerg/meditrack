package ase.meditrack.controller;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.ShiftDto;
import ase.meditrack.model.mapper.ShiftMapper;
import ase.meditrack.service.ShiftService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shift")
@Slf4j
public class ShiftController {
    private final ShiftService service;
    private final ShiftMapper mapper;

    public ShiftController(ShiftService service, ShiftMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public List<ShiftDto> findAll() {
        log.info("Fetching shifts");
        return mapper.toDtoList(service.findAll());
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || authentication.name == #id.toString()")
    public ShiftDto findById(@PathVariable UUID id) {
        log.info("Fetching shift {}", id);
        return mapper.toDto(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public ShiftDto create(@Validated(CreateValidator.class) @RequestBody ShiftDto dto) {
        log.info("Creating shift for monthly plan {}", dto.monthlyPlan());
        return mapper.toDto(service.create(mapper.fromDto(dto)));
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || authentication.name == #dto.id().toString()")
    public ShiftDto update(@Validated(UpdateValidator.class) @RequestBody ShiftDto dto) {
        log.info("Updating shift {}", dto.id());
        return mapper.toDto(service.update(mapper.fromDto(dto)));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || authentication.name == #id.toString()")
    public void delete(@PathVariable UUID id) {
        log.info("Deleting shift with id {}", id);
        service.delete(id);
    }
}
