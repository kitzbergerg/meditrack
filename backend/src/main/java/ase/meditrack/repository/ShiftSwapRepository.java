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
     * Deletes all shift swaps by the shift id of the requesting user.
     *
     * @param shiftId from the shift
     */
    void deleteAllByRequestedShiftId(UUID shiftId);

    /**
     * Fetches all the shift swap offers from a user from the remaining days of the month.
     *
     * @param userId from the requested shift swap
     * @param after is the current date
     * @param before is the first date of the next month
     * @return list of shift swaps
     */

    @Query("SELECT s From shift_swap s WHERE s.swapRequestingUser.id = :userId "
            + "AND s.requestedShift.date > :after AND s.requestedShift.date < :before "
            + "AND s.requestedShiftSwapStatus = 'ACCEPTED' "
            + "AND s.suggestedShift IS NULL AND s.swapSuggestingUser IS NULL")
    List<ShiftSwap> findAllCreatedShiftSwapOffers(
            @Param("userId") UUID userId,
            @Param("after") LocalDate after,
            @Param("before") LocalDate before);

    /**
     * Fetches all the shift swap requests from a user from the remaining days of the month.
     *
     * @param userId the user from the shift swap requests
     * @param after is the current date
     * @param before is the first date of the next month
     * @return list of shift swap requests
     */
    @Query("SELECT s From shift_swap s WHERE s.swapRequestingUser.id = :userId "
            + "AND s.requestedShift.date > :after AND s.requestedShift.date < :before "
            + "AND s.requestedShiftSwapStatus = 'ACCEPTED' AND s.suggestedShiftSwapStatus = 'PENDING' "
            + "AND s.suggestedShift IS NOT NULL AND s.swapSuggestingUser IS NOT NULL")
    List<ShiftSwap> findAllShiftSwapRequests(
            @Param("userId") UUID userId,
            @Param("after") LocalDate after,
            @Param("before") LocalDate before);

    /**
     * Fetches all the shift swap suggestions from a user from the remaining days of the month.
     *
     * @param userId the user from the shift swap suggestions
     * @param after is the current date
     * @param before is the first date of the next month
     * @return list of shift swap suggestions
     */
    @Query("SELECT s FROM shift_swap s WHERE s.swapSuggestingUser.id = :userId "
            + "AND s.requestedShift.date > :after AND s.requestedShift.date < :before "
            + "AND s.requestedShiftSwapStatus = 'ACCEPTED' AND s.suggestedShiftSwapStatus = 'PENDING' ")
    List<ShiftSwap> findAllShiftSwapSuggestions(
            @Param("userId") UUID userId,
            @Param("after") LocalDate after,
            @Param("before") LocalDate before);

    /**
     * Fetches all the shift swaps from the remaining days of the month of all users in a team.
     *
     * @param teamId of the user
     * @param roleId of the user
     * @param userId whose requests should not be considered
     * @param after is the current date
     * @param before is the first date of the next month
     * @return list of shift swaps
     */
    @Query("SELECT s FROM shift_swap s WHERE s.swapRequestingUser.id != :userId "
            + "AND s.swapRequestingUser.team.id = :teamId AND s.requestedShift.date > :after "
            + "AND s.requestedShift.date < :before "
            + "AND s.suggestedShift IS NULL AND s.swapSuggestingUser IS NULL "
            + "AND s.swapRequestingUser.role.id = :roleId")
    List<ShiftSwap> findAllShiftSwapOffersWithSameRole(
            @Param("teamId") UUID teamId,
            @Param("roleId") UUID roleId,
            @Param("userId") UUID userId,
            @Param("after") LocalDate after,
            @Param("before") LocalDate before);

}
