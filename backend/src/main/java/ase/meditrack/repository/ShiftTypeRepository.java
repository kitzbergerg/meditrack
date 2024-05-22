package ase.meditrack.repository;

import ase.meditrack.model.entity.ShiftType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShiftTypeRepository extends JpaRepository<ShiftType, UUID> {
}
