package ase.meditrack.controller;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.ShiftDto;
import ase.meditrack.model.dto.SimpleShiftDto;
import ase.meditrack.model.mapper.ShiftMapper;
import ase.meditrack.service.ShiftService;
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

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shift")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
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

    @GetMapping("/month")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_dm', 'SCOPE_employee')")
    public List<SimpleShiftDto> findAllByCurrentMonth(Principal principal) {
        log.info("Fetching shifts by current month");
        return mapper.toSimpleShiftDtoList(service.findAllByCurrentMonth(principal));
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || hasAnyAuthority('SCOPE_dm')")
    public ShiftDto findById(@PathVariable UUID id) {
        log.info("Fetching shift with id: {}", id);
        return mapper.toDto(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') ||"
            + "(hasAnyAuthority('SCOPE_dm')"
            + "&& @monthlyPlanService.isUserInTeam(authentication.name, #dto.monthlyPlan()))")
    @ResponseStatus(HttpStatus.CREATED)
    public ShiftDto create(@Validated(CreateValidator.class) @RequestBody ShiftDto dto) {
        log.info("Creating shift {}", dto.id());
        return mapper.toDto(service.create(mapper.fromDto(dto)));
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') ||"
            + "(hasAnyAuthority('SCOPE_dm')"
            + "&& @monthlyPlanService.isUserInTeam(authentication.name, #dto.monthlyPlan()))")
    @ResponseStatus(HttpStatus.OK)
    public ShiftDto update(@Validated(UpdateValidator.class) @RequestBody ShiftDto dto) {
        log.info("Updating shift {}", dto.id());
        return mapper.toDto(service.update(mapper.fromDto(dto)));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') ||"
            + "hasAnyAuthority('SCOPE_dm') && @monthlyPlanService.isShiftFromTeam(authentication.name, #id)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("Deleting shift with id {}", id);
        service.delete(id);
    }
}
