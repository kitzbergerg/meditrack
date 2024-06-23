package ase.meditrack.controller;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.HolidayDto;
import ase.meditrack.model.entity.enums.HolidayRequestStatus;
import ase.meditrack.model.mapper.HolidayMapper;
import ase.meditrack.service.HolidayService;
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
@RequestMapping("/api/holiday")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class HolidayController {
    private final HolidayService service;
    private final HolidayMapper mapper;

    public HolidayController(HolidayService service, HolidayMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_employee')")
    @ResponseStatus(HttpStatus.CREATED)
    public HolidayDto create(@Validated(CreateValidator.class) @RequestBody HolidayDto dto, Principal principal) {
        log.info("Creating holiday for user: {}", principal.getName());
        return mapper.toDto(service.create(mapper.fromDto(dto), principal.getName()));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_employee')")
    public List<HolidayDto> findAllByUser(Principal principal) {
        log.info("Fetching holidays for user: {}", principal.getName());
        return mapper.toDtoList(service.findAllByUser(principal.getName()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_employee')")
    public HolidayDto findByIdAndUser(@PathVariable UUID id, Principal principal) {
        log.info("Fetching holiday with id: {}", id);
        return mapper.toDto(service.findByIdAndUser(id, principal.getName()));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public List<HolidayDto> findAll() {
        log.info("Fetching all holidays");
        return mapper.toDtoList(service.findAll());
    }

    @GetMapping("/team")
    @PreAuthorize("hasAnyAuthority('SCOPE_dm')")
    public List<HolidayDto> findAllByTeam(Principal principal) {
        log.info("Fetching all holidays for team");
        return mapper.toDtoList(service.findAllByTeam(principal));
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_employee')")
    @ResponseStatus(HttpStatus.OK)
    public HolidayDto update(@Validated(UpdateValidator.class) @RequestBody HolidayDto dto, Principal principal) {
        log.info("Updating holiday {}", dto.id());
        return mapper.toDto(service.update(mapper.fromDto(dto), principal.getName()));
    }

    @PutMapping("/{id}/{status}")
    @PreAuthorize("hasAnyAuthority('SCOPE_dm')")
    @ResponseStatus(HttpStatus.OK)
    public HolidayDto updateStatus(@PathVariable UUID id,
                                   @PathVariable HolidayRequestStatus status, Principal principal) {
        log.info("Updating status of holiday with id: {} to: {}", id, status.name());
        return mapper.toDto(service.updateStatus(id, status, principal));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("Deleting holiday with id {}", id);
        service.delete(id);
    }
}
