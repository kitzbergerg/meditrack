package ase.meditrack.repository;

import ase.meditrack.model.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID> {

    /**
     * Fetches all the shifts from a user from the remaining days of the month.
     *
     * @param users  with the shift
     * @param after  is the current date
     * @param before is the first date of the next month
     * @return list of shifts
     */

    List<Shift> findAllByUsersAndDateAfterAndDateBefore(List<UUID> users, LocalDate after, LocalDate before);

    /**
     * Fetches all the shifts from a user from the current month.
     *
     * @param users with the shift
     * @param date  is the current date
     * @return list of shifts
     */
    List<Shift> findAllByUsersAndDate(List<UUID> users, LocalDate date);

    /**
     * Fetches all shifts of a team between the date range.
     *
     * @param teamId    the id of the team
     * @param startDate the start date (inclusive)
     * @param endDate   the end date (inclusive)
     * @return list of shifts
     */
    @Query("SELECT s FROM shift s WHERE s.monthlyPlan.team.id = :teamId AND (s.date BETWEEN :startDate AND :endDate)")
    List<Shift> findAllByTeamAndDateAfterAndDateBefore(
            @Param("teamId") UUID teamId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
