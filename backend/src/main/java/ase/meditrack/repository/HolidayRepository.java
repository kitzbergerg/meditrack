package ase.meditrack.repository;

import ase.meditrack.model.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface HolidayRepository extends JpaRepository<Holiday, UUID> {

    /**
     * Find all holidays for a given user in the given timeframe.
     *
     * @param userId    the users id
     * @param startDate the start date
     * @param endDate   the end date
     * @return a list of holidays that have days between startDate and endDate
     */
    @Query("SELECT h FROM holiday h WHERE h.user.id = :userId "
            + "AND h.isApproved "
            + "AND (h.startDate BETWEEN :startDate AND :endDate "
            + "OR h.endDate BETWEEN :startDate AND :endDate "
            + "OR (h.startDate <= :startDate AND h.endDate >= :endDate))")
    List<Holiday> findHolidaysForUserInCurrentMonth(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
