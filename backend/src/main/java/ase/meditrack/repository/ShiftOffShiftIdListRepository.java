package ase.meditrack.repository;

import ase.meditrack.model.entity.ShiftOffShiftIdList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShiftOffShiftIdListRepository extends JpaRepository<ShiftOffShiftIdList, UUID> {
}
