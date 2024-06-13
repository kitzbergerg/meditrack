package ase.meditrack.repository;

import ase.meditrack.model.entity.MonthlyWorkDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MonthlyWorkDetailsRepository extends JpaRepository<MonthlyWorkDetails, UUID> {

    /**
     * Fetches monthly work details for a user.
     *
     * @param userId of the work detail user
     * @param month of the work details
     * @param year of the work details
     * @return the monthly work details for a user for that month and year
     */
    MonthlyWorkDetails findMonthlyWorkDetailsByUserIdAndMonthAndYear(UUID userId, Integer month, Integer year);
}
