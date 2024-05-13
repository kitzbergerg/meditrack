package ase.meditrack.repository;

import ase.meditrack.model.entity.MonthlyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MonthlyPlanRepository extends JpaRepository<MonthlyPlan, UUID> {
}
