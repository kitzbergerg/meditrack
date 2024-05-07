package ase.meditrack.controller;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.HardConstraintsDto;
import ase.meditrack.model.mapper.HardConstraintsMapper;
import ase.meditrack.service.HardConstraintsService;
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
@RequestMapping("/api/hardconstraints")
@Slf4j
public class HardConstraintsController {
    private final HardConstraintsService service;
    private final HardConstraintsMapper mapper;

    public HardConstraintsController(HardConstraintsService service, HardConstraintsMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public List<HardConstraintsDto> findAll() {
        log.info("Fetching hard constraints");
        return mapper.toDtoList(service.findAll());
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || authentication.name == #id.toString()")
    public HardConstraintsDto findById(@PathVariable UUID id) {
        log.info("Fetching hard constraints {}", id);
        return mapper.toDto(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public HardConstraintsDto create(@Validated(CreateValidator.class) @RequestBody HardConstraintsDto dto) {
        log.info("Creating hard constraints");
        return mapper.toDto(service.create(mapper.fromDto(dto)));
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || authentication.name == #dto.id().toString()")
    public HardConstraintsDto update(@Validated(UpdateValidator.class) @RequestBody HardConstraintsDto dto) {
        log.info("Updating hard constraints {}", dto.id());
        return mapper.toDto(service.update(mapper.fromDto(dto)));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || authentication.name == #id.toString()")
    public void delete(@PathVariable UUID id) {
        log.info("Deleting hard constraints with id {}", id);
        service.delete(id);
    }
}
