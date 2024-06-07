package ase.meditrack.repository;

import ase.meditrack.model.entity.ShiftSwap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftSwapRepository extends JpaRepository<ShiftSwap, UUID> {

    /**
     * Fetches all the shift swaps from a user from the remaining days of the month.
     *
     * @param user from the requested shift swap
     * @param after is the current date
     * @param before is the first date of the next month
     * @return list of shifts
     */

    List<ShiftSwap> findAllBySwapRequestingUserIdAndRequestedShiftDateAfterAndRequestedShiftDateBefore(
            UUID user, LocalDate after, LocalDate before);

    /**
     * Fetches all the shift swaps from the remaining days of the month of all users.
     *
     * @param teamId of the user
     * @param userId whose requests should not be considered
     * @param after is the current date
     * @param before is the first date of the next month
     * @return list of shifts
     */
    @Query("SELECT s FROM shift_swap s WHERE s.swapRequestingUser.id != :userId "
            + "AND s.swapRequestingUser.team.id = :teamId AND s.requestedShift.date > :after "
            + "AND s.requestedShift.date < :before")
    List<ShiftSwap> findAllShiftSwapOffersWithSameRole(
            @Param("teamId") UUID teamId,
            @Param("userId") UUID userId,
            @Param("after") LocalDate after,
            @Param("before") LocalDate before);

}
