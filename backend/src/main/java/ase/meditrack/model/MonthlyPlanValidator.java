package ase.meditrack.model;

import ase.meditrack.exception.ValidationException;
import ase.meditrack.model.entity.MonthlyPlan;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
@Slf4j
@NoArgsConstructor
public class MonthlyPlanValidator {

    public void validateMonthlyPlanOnCreate(MonthlyPlan monthlyPlan) {
        log.trace("Validating monthly plan on create: {}", monthlyPlan);
        if (monthlyPlan.getId() != null) {
            throw new ValidationException("Id must be null");
        }
        validateDateAndPublishedFields(monthlyPlan);
    }

    public void validateMonthlyPlanOnUpdate(MonthlyPlan monthlyPlan) {
        log.trace("Validating monthly plan on update: {}", monthlyPlan);
        if (monthlyPlan.getId() == null) {
            throw new ValidationException("Id must not be null");
        }
        validateDateAndPublishedFields(monthlyPlan);
    }

    private void validateDateAndPublishedFields(MonthlyPlan monthlyPlan) {
        if (monthlyPlan.getYear() == null) {
            throw new ValidationException("Year must not be null");
        }
        if (monthlyPlan.getMonth() == null) {
            throw new ValidationException("Month must not be null");
        }
        if (monthlyPlan.getPublished() == null) {
            throw new ValidationException("Published must not be null");
        }
        if (monthlyPlan.getYear() < Year.now().getValue()) {
            throw new ValidationException("Year must not be in the past");
        }
        if (monthlyPlan.getMonth() < 1 || monthlyPlan.getMonth() > 12) {
            throw new ValidationException("Month must be between 1 and 12");
        }
    }
}
