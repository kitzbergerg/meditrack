package ase.meditrack.service;

import ase.meditrack.model.entity.Test;
import ase.meditrack.repository.TestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {
    private final TestRepository repository;

    public TestService(TestRepository repository) {
        this.repository = repository;
    }

    public List<Test> findAll() {
        return repository.findAll();
    }

    public Test save(Test test) {
        return repository.save(test);
    }
}
