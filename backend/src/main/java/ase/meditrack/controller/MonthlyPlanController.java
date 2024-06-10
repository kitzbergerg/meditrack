package ase.meditrack.controller;

import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.MonthlyPlanDto;
import ase.meditrack.model.mapper.MonthlyPlanMapper;
import ase.meditrack.service.MonthlyPlanService;
import ase.meditrack.service.algorithm.MonthlyPlanCreator;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/monthly-plan")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class MonthlyPlanController {
    private final MonthlyPlanService service;
    private final MonthlyPlanCreator monthlyPlanCreator;
    private final MonthlyPlanMapper mapper;

    public MonthlyPlanController(MonthlyPlanService service, MonthlyPlanCreator monthlyPlanCreator,
                                 MonthlyPlanMapper mapper) {
        this.service = service;
        this.monthlyPlanCreator = monthlyPlanCreator;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public List<MonthlyPlanDto> findAll() {
        log.info("Fetching monthly-plans");
        return mapper.toDtoList(service.findAll());
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm')")
    public MonthlyPlanDto findById(@PathVariable UUID id) {
        // TODO #92: add check if dm can view/edit plan
        log.info("Fetching monthly-plan with id: {}", id);
        return mapper.toDto(service.findById(id));
    }

    @GetMapping("/team")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm')")
    public MonthlyPlanDto findByTeamMonthYear(@RequestParam Year year, @RequestParam Month month, Principal principal) {
        // TODO #92: add check if dm can view/edit plan
        log.info("Fetching monthly-plan for user : {}, for date: {}, {}", principal.getName(), month, year);
        return mapper.toDto(service.getMonthlyPlan(month.getValue(), year.getValue(), principal));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm')")
    @ResponseStatus(HttpStatus.CREATED)
    public MonthlyPlanDto create(@RequestParam Year year, @RequestParam Month month, Principal principal) {
        // TODO #92: add check if dm can view/edit plan
        log.info("Creating monthly-plan for user {}, {} {}", principal.getName(), year, month);
        return mapper.toDto(monthlyPlanCreator.createMonthlyPlan(month.getValue(), year.getValue(), principal));
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm')")
    @ResponseStatus(HttpStatus.OK)
    public MonthlyPlanDto update(@Validated(UpdateValidator.class) @RequestBody MonthlyPlanDto dto) {
        // TODO #92: add check if dm can view/edit plan
        log.info("Updating monthly-plan {}", dto.id());
        return mapper.toDto(service.update(mapper.fromDto(dto)));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        // TODO #92: add check if dm can view/edit plan
        log.info("Deleting monthly-plan with id {}", id);
        service.delete(id);
    }
}
