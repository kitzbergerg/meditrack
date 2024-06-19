package ase.meditrack.model;

import ase.meditrack.model.entity.Holiday;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.entity.enums.HolidayRequestStatus;
import ase.meditrack.repository.HolidayRepository;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class HolidayValidator {
    private final HolidayRepository holidayRepository;

    public HolidayValidator(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    public void validateHoliday(Holiday holiday, User user) {
        //check if start date is in the future and before end date
        if (holiday.getStartDate().isBefore(LocalDate.now()) || holiday.getStartDate().isAfter(holiday.getEndDate())) {
            throw new ValidationException("Start date must be in the future and before end date!");
        }
        //check if there is already a holiday defined for the same date
        List<Holiday> existingHolidays = holidayRepository.findAllByUser(user);
        for (Holiday existingHoliday : existingHolidays) {
            if (!holiday.getStartDate().isBefore(existingHoliday.getStartDate())
                    && !holiday.getStartDate().isAfter(existingHoliday.getEndDate())) {
                throw new ValidationException("Start date is already defined for a holiday!");
            }
            if (!holiday.getEndDate().isBefore(existingHoliday.getStartDate())
                    && !holiday.getEndDate().isAfter(existingHoliday.getEndDate())) {
                throw new ValidationException("End date is already defined for a holiday!");
            }
        }
    }

    public void validateHolidayOnUpdate(Holiday holidayToValidate, String userId, Holiday dbHoliday) {
        //check if the user is editing his own holiday
        if (!dbHoliday.getUser().getId().equals(UUID.fromString(userId))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You are not allowed to update this holiday! Only the owner can update it.");
        }
        //check if the holiday status is REQUESTED
        if (!dbHoliday.getStatus().equals(HolidayRequestStatus.REQUESTED)) {
            throw new ValidationException("Only holidays with status REQUESTED can be updated!");
        }

        validateHoliday(holidayToValidate, dbHoliday.getUser());
    }

    public void validateHolidayOnDelete(Holiday holiday) {
        //check if the holiday status is REJECTED or CANCELLED
        if (holiday.getStatus() == HolidayRequestStatus.APPROVED
                || holiday.getStatus() == HolidayRequestStatus.REQUESTED) {
            throw new ValidationException("Only holidays with status 'REJECTED' or 'CANCELLED' can be deleted!");
        }
    }
}
