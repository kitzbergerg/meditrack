package ase.meditrack.repository;

import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MonthlyPlanRepository extends JpaRepository<MonthlyPlan, UUID> {
    /**
     * Retrieves the monthly plan for a specific team for a month and year.
     *
     * @param team of the user
     * @param month of the monthly plan
     * @param year  of the monthly plan
     * @return the monthly plan
     */
    MonthlyPlan findMonthlyPlanByTeamAndMonthAndYear(Team team, Integer month, Integer year);
}
