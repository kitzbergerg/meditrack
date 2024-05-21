package ase.meditrack.repository;

import ase.meditrack.model.entity.HardConstraints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HardConstraintsRepository extends JpaRepository<HardConstraints, UUID> {
}
