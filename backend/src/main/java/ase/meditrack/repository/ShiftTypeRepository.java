package ase.meditrack.repository;

import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftTypeRepository extends JpaRepository<ShiftType, UUID> {

    /**
     * Retrieves a list of shift types associated with a specific team.
     *
     * @param team The team for which shift types are to be retrieved.
     * @return A list of shift types associated with the specified team.
     */
    List<ShiftType> findAllByTeam(Team team);
}
