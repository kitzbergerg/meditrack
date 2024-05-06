package ase.meditrack.repository;

import ase.meditrack.model.entity.ShiftSwap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShiftSwapRepository extends JpaRepository<ShiftSwap, UUID> {
}
