package ase.meditrack.controller;

import ase.meditrack.model.CreateValidator;
import ase.meditrack.model.UpdateValidator;
import ase.meditrack.model.dto.ShiftOffShiftIdListDto;
import ase.meditrack.model.mapper.ShiftOffShiftIdListMapper;
import ase.meditrack.service.ShiftOffShiftIdListService;
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
@RequestMapping("/api/shiftoffshiftidlist")
@Slf4j
public class ShiftOffShiftIdListController {
    private final ShiftOffShiftIdListService service;
    private final ShiftOffShiftIdListMapper mapper;

    public ShiftOffShiftIdListController(ShiftOffShiftIdListService service, ShiftOffShiftIdListMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public List<ShiftOffShiftIdListDto> findAll() {
        log.info("Fetching shift off shift id lists");
        return mapper.toDtoList(service.findAll());
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || authentication.name == #id.toString()")
    public ShiftOffShiftIdListDto findById(@PathVariable UUID id) {
        log.info("Fetching shift off shift id list {}", id);
        return mapper.toDto(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public ShiftOffShiftIdListDto create(@Validated(CreateValidator.class) @RequestBody ShiftOffShiftIdListDto dto) {
        log.info("Creating shift off shift id list");
        return mapper.toDto(service.create(mapper.fromDto(dto)));
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || authentication.name == #dto.id().toString()")
    public ShiftOffShiftIdListDto update(@Validated(UpdateValidator.class) @RequestBody ShiftOffShiftIdListDto dto) {
        log.info("Updating shift off shift id list {}", dto.id());
        return mapper.toDto(service.update(mapper.fromDto(dto)));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin') || authentication.name == #id.toString()")
    public void delete(@PathVariable UUID id) {
        log.info("Deleting shift off shift id list with id {}", id);
        service.delete(id);
    }
}
