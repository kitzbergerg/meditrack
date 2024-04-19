package ase.meditrack.controller;

import ase.meditrack.model.dto.TestDto;
import ase.meditrack.model.mapper.TestMapper;
import ase.meditrack.service.TestService;
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
    public List<TestDto> findAll() {
        return mapper.toDtoList(service.findAll());
    }

    @PostMapping
    public TestDto save(@RequestBody TestDto testDto) {
        return mapper.toDto(service.save(mapper.fromDto(testDto)));
    }
}
