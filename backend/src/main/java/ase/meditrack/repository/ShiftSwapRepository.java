package ase.meditrack.repository;

import ase.meditrack.model.entity.ShiftSwap;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
