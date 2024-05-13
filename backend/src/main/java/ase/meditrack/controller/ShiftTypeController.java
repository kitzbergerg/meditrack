package ase.meditrack.controller;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.ShiftTypeDto;
import ase.meditrack.model.mapper.ShiftTypeMapper;
import ase.meditrack.service.ShiftTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public ShiftTypeDto findById(@PathVariable UUID id) {
        log.info("Fetching shift type {}", id);
        return mapper.toDto(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public ShiftTypeDto create(@Validated(CreateValidator.class) @RequestBody ShiftTypeDto dto) {
        log.info("Creating shift type {}", dto.name());
        return mapper.toDto(service.create(mapper.fromDto(dto)));
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public ShiftTypeDto update(@Validated(UpdateValidator.class) @RequestBody ShiftTypeDto dto) {
        log.info("Updating shift type {}", dto.name());
        return mapper.toDto(service.update(mapper.fromDto(dto)));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public void delete(@PathVariable UUID id) {
        log.info("Deleting shift type with id {}", id);
        service.delete(id);
    }
}
