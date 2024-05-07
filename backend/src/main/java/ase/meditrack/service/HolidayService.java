package ase.meditrack.service;

import ase.meditrack.model.entity.Holiday;
import ase.meditrack.repository.HolidayRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class HolidayService {
    private final HolidayRepository repository;

    public HolidayService(HolidayRepository repository) {
        this.repository = repository;
    }

    public Holiday findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<Holiday> findAll() {
        return repository.findAll();
    }

    public Holiday create(Holiday holiday) {
        return repository.save(holiday);
    }

    public Holiday update(Holiday holiday) {
        Holiday existing = repository.findById(holiday.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (holiday.getStartDate() != null) {
            existing.setStartDate(holiday.getStartDate());
        }
        if (holiday.getEndDate() != null) {
            existing.setEndDate(holiday.getEndDate());
        }
        if (holiday.getIsApproved() != null) {
            existing.setIsApproved(holiday.getIsApproved());
        }
        if (holiday.getUser() != null) {
            existing.setUser(holiday.getUser());
        }

        return repository.save(existing);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
