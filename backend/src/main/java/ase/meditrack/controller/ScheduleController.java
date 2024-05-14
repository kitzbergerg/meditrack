package ase.meditrack.controller;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.MonthlyPlanDto;
import ase.meditrack.model.dto.RoleDto;
import ase.meditrack.model.mapper.RoleMapper;
import ase.meditrack.model.mapper.ScheduleMapper;
import ase.meditrack.service.RoleService;
import ase.meditrack.service.algorithm.MonthlyPlanCreator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.xml.bind.ValidationException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;

@RestController
@RequestMapping("/api/schedule")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class ScheduleController {
    private final MonthlyPlanCreator service;
    private final ScheduleMapper mapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public ScheduleController(MonthlyPlanCreator service, ScheduleMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public MonthlyPlanDto create() {
        log.info("Creating schedule");
        return mapper.toDto(service.createMonthlyPlan(6, 2024, UUID.randomUUID()));
    }
}
