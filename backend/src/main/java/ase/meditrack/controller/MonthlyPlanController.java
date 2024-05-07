package ase.meditrack.controller;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.MonthlyPlanDto;
import ase.meditrack.model.mapper.MonthlyPlanMapper;
import ase.meditrack.service.MonthlyPlanService;
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
@RequestMapping("/api/monthlyplan")
@Slf4j
public class MonthlyPlanController {
    private final MonthlyPlanService service;
    private final MonthlyPlanMapper mapper;

    public MonthlyPlanController(MonthlyPlanService service, MonthlyPlanMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public List<MonthlyPlanDto> findAll() {
        log.info("Fetching monthly plans");
        return mapper.toDtoList(service.findAll());
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || authentication.name == #id.toString()")
    public MonthlyPlanDto findById(@PathVariable UUID id) {
        log.info("Fetching monthly plan {}", id);
        return mapper.toDto(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public MonthlyPlanDto create(@Validated(CreateValidator.class) @RequestBody MonthlyPlanDto dto) {
        log.info("Creating monthly plan for team {}", dto.team());
        return mapper.toDto(service.create(mapper.fromDto(dto)));
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || authentication.name == #dto.id().toString()")
    public MonthlyPlanDto update(@Validated(UpdateValidator.class) @RequestBody MonthlyPlanDto dto) {
        log.info("Updating monthly plan {}", dto.id());
        return mapper.toDto(service.update(mapper.fromDto(dto)));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || authentication.name == #id.toString()")
    public void delete(@PathVariable UUID id) {
        log.info("Deleting monthly plan with id {}", id);
        service.delete(id);
    }
}
