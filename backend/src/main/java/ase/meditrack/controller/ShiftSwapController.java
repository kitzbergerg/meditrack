package ase.meditrack.controller;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.ShiftSwapDto;
import ase.meditrack.model.dto.SimpleShiftSwapDto;
import ase.meditrack.model.mapper.ShiftSwapMapper;
import ase.meditrack.service.ShiftSwapService;
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
@RequestMapping("/api/shift-swap")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class ShiftSwapController {
    private final ShiftSwapService service;
    private final ShiftSwapMapper mapper;

    public ShiftSwapController(ShiftSwapService service, ShiftSwapMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public List<ShiftSwapDto> findAll() {
        log.info("Fetching shift-swaps");
        return mapper.toDtoList(service.findAll());
    }

    @GetMapping("/month")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_employee')")
    public List<ShiftSwapDto> findAllFromUserAndCurrentMonth(Principal principal) {
        log.info("Fetching shift-swaps from a user from the current month");
        return mapper.toDtoList(service.findAllByCurrentMonth(principal));
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_employee')")
    public ShiftSwapDto findById(@PathVariable UUID id) {
        log.info("Fetching shift-swap with id: {}", id);
        return mapper.toDto(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_employee')")
    @ResponseStatus(HttpStatus.CREATED)
    public ShiftSwapDto create(@Validated(CreateValidator.class) @RequestBody SimpleShiftSwapDto dto) {
        log.info("Creating shift-swap {}", dto.id());
        return mapper.toDto(service.create(mapper.fromSimpleShiftSwapDto(dto)));
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_employee')")
    @ResponseStatus(HttpStatus.OK)
    public ShiftSwapDto update(@Validated(UpdateValidator.class) @RequestBody ShiftSwapDto dto) {
        log.info("Updating shift-swap {}", dto.id());
        return mapper.toDto(service.update(mapper.fromDto(dto)));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("Deleting shift-swap with id {}", id);
        service.delete(id);
    }
}
