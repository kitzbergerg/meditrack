package ase.meditrack.controller;

import ase.meditrack.model.dto.TestDto;
import ase.meditrack.model.mapper.TestMapper;
import ase.meditrack.service.TestService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final TestService service;

    private final TestMapper mapper;

    public TestController(TestService service, TestMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_user')")
    public List<TestDto> findAll() {
        return mapper.toDtoList(service.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_admin')")
    public TestDto save(@Valid @RequestBody TestDto testDto) {
        return mapper.toDto(service.save(mapper.fromDto(testDto)));
    }
}
