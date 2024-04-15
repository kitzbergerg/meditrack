package ase.meditrack.controller;

import ase.meditrack.model.entity.Test;
import ase.meditrack.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final TestService service;

    public TestController(TestService service) {
        this.service = service;
    }

    @GetMapping
    public List<Test> findAll() {
        // TODO: add mapping to DTOs using mapstruct
        return service.findAll();
    }
}
