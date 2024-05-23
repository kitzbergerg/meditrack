package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.Holiday;
import ase.meditrack.repository.HolidayRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class HolidayService {
    private final HolidayRepository repository;

    public HolidayService(HolidayRepository repository) {
        this.repository = repository;
    }

    /**
     * Fetches all holidays from the database.
     *
     * @return List of all holidays
     */
    public List<Holiday> findAll() {
        return repository.findAll();
    }

    /**
     * Fetches a holiday by id from the database.
     *
     * @param id the id of the holiday
     * @return the holiday
     */
    public Holiday findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Could not find holiday with id: " + id + "!"));
    }

    /**
     * Creates a holiday in the database.
     *
     * @param holiday the holiday to create
     * @return the created holiday
     */
    public Holiday create(Holiday holiday) {
        return repository.save(holiday);
    }

    /**
     * Updates a holiday in the database.
     *
     * @param holiday the holiday to update
     * @return the updated holiday
     */
    public Holiday update(Holiday holiday) {
        Holiday dbHoliday = findById(holiday.getId());

        if (holiday.getStartDate() != null) {
            dbHoliday.setStartDate(holiday.getStartDate());
        }
        if (holiday.getEndDate() != null) {
            dbHoliday.setEndDate(holiday.getEndDate());
        }
        if (holiday.getIsApproved() != null) {
            dbHoliday.setIsApproved(holiday.getIsApproved());
        }
        if (holiday.getUser() != null) {
            dbHoliday.setUser(holiday.getUser());
        }

        return repository.save(dbHoliday);
    }

    /**
     * Deletes a holiday from the database.
     *
     * @param id the id of the holiday
     */
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
