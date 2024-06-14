package ase.meditrack.controller;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.ShiftSwapDto;
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

    @GetMapping("/own-offers")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_employee')")
    public List<ShiftSwapDto> findAllFromUserAndCurrentMonth(Principal principal) {
        log.info("Fetching shift-swaps from a user from the current month");
        return mapper.toDtoList(service.findAllByCurrentMonth(principal));
    }

    @GetMapping("/requests")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_employee')")
    public List<ShiftSwapDto> findAllRequests(Principal principal) {
        log.info("Fetching shift-swaps requests from a user from the current month");
        return mapper.toDtoList(service.findAllRequests(principal));
    }

    @GetMapping("/suggestions")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_employee')")
    public List<ShiftSwapDto> findAllSuggestions(Principal principal) {
        log.info("Fetching shift-swaps suggestions from a user from the current month");
        return mapper.toDtoList(service.findAllSuggestions(principal));
    }

    @GetMapping("/offers")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin', 'SCOPE_employee')")
    public List<ShiftSwapDto> findAllFromCurrentMonth(Principal principal) {
        log.info("Fetching shift-swaps offers from the current month");
        return mapper.toDtoList(service.findAllOffersByCurrentMonth(principal));
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || "
            + "(hasAnyAuthority('SCOPE_employee') && @shiftSwapService.isShiftSwapFromUser(#principal, #id))")
    public ShiftSwapDto findById(@PathVariable UUID id, Principal principal) {
        log.info("Fetching shift-swap with id: {}", id);
        return mapper.toDto(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || "
            + "(hasAnyAuthority('SCOPE_employee') && "
            + "@shiftSwapService.isShiftFromUser(#principal, @shiftSwapMapperImpl.fromDto(#dto)))")
    @ResponseStatus(HttpStatus.CREATED)
    public ShiftSwapDto create(@Validated(CreateValidator.class) @RequestBody ShiftSwapDto dto, Principal principal) {
        log.info("Creating shift-swap {}", dto);
        return mapper.toDto(service.create(mapper.fromDto(dto)));
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || "
            + "(hasAnyAuthority('SCOPE_employee') && @shiftSwapService.isShiftSwapFromUser(#principal, #dto.id()))")
    @ResponseStatus(HttpStatus.OK)
    public ShiftSwapDto update(@Validated(UpdateValidator.class) @RequestBody ShiftSwapDto dto, Principal principal) {
        log.info("Updating shift-swap {}", dto.id());
        return mapper.toDto(service.update(mapper.fromDto(dto)));
    }


    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || "
            + "(hasAnyAuthority('SCOPE_employee') && @shiftSwapService.isShiftSwapFromUser(#principal, #id))")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id, Principal principal) {
        log.info("Deleting shift-swap with id {}", id);
        service.delete(id);
    }

    @DeleteMapping("/retract/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || "
            + "(hasAnyAuthority('SCOPE_employee') && @shiftSwapService.isShiftSwapFromUser(#principal, #id))")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retract(@PathVariable UUID id, Principal principal) {
        log.info("Retract shift-swap request {}", id);
        service.retract(id);
    }
}
