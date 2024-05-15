package ase.meditrack.controller;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.exception.ValidationException;
import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.MonthlyPlanDto;
import ase.meditrack.model.mapper.MonthlyPlanMapper;
import ase.meditrack.service.MonthlyPlanService;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/monthly-plan")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
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
        log.info("Fetching monthly-plans");
        return mapper.toDtoList(service.findAll());
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public MonthlyPlanDto findById(@PathVariable UUID id) {
        log.info("Fetching monthly-plan with id: {}", id);
        try {
            return mapper.toDto(service.findById(id));
        } catch (NotFoundException e) {
            log.error("NotFoundException: GET /api/monthly-plan/{}", id, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Monthly plan with id: " + id + " not found", e);
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    @ResponseStatus(HttpStatus.CREATED)
    public MonthlyPlanDto create(@Validated(CreateValidator.class) @RequestBody MonthlyPlanDto dto) {
        log.info("Creating monthly-plan {}", dto.id());
        try {
            return mapper.toDto(service.create(mapper.fromDto(dto)));
        } catch (ValidationException e) {
            log.error("ValidationException: POST /api/monthly-plan/{} {}", dto.id(), dto, e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Error during creating monthly-plan: " + e.getMessage(), e);
        }
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    @ResponseStatus(HttpStatus.OK)
    public MonthlyPlanDto update(@Validated(UpdateValidator.class) @RequestBody MonthlyPlanDto dto) {
        log.info("Updating monthly-plan {}", dto.id());
        try {
            return mapper.toDto(service.update(mapper.fromDto(dto)));
        } catch (ValidationException e) {
            log.error("ValidationException: PUT /api/monthly-plan/{} {}", dto.id(), dto, e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Error during updating monthly-plan: " + e.getMessage(), e);
        } catch (NotFoundException e) {
            log.error("NotFoundException: PUT /api/monthly-plan/{} {}", dto.id(), dto, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Monthly plan with id: " + dto.id() + " not found",
                    e);
        }
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("Deleting monthly-plan with id {}", id);
        service.delete(id);
    }
}
