package ase.meditrack.controller;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.ShiftSwapDto;
import ase.meditrack.model.mapper.ShiftSwapMapper;
import ase.meditrack.service.ShiftSwapService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shiftswap")
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
        log.info("Fetching shift swaps");
        return mapper.toDtoList(service.findAll());
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public ShiftSwapDto findById(@PathVariable UUID id) {
        log.info("Fetching shift swap {}", id);
        return mapper.toDto(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public ShiftSwapDto create(@Validated(CreateValidator.class) @RequestBody ShiftSwapDto dto) {
        log.info("Creating shift swap for user {}", dto.swapRequestingUser());
        return mapper.toDto(service.create(mapper.fromDto(dto)));
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public ShiftSwapDto update(@Validated(UpdateValidator.class) @RequestBody ShiftSwapDto dto) {
        log.info("Updating shift swap {}", dto.id());
        return mapper.toDto(service.update(mapper.fromDto(dto)));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public void delete(@PathVariable UUID id) {
        log.info("Deleting shift swap with id {}", id);
        service.delete(id);
    }
}
