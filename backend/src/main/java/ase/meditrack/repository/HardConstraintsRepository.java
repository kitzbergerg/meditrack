package ase.meditrack.repository;

import ase.meditrack.model.entity.HardConstraints;
import ase.meditrack.model.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HardConstraintsRepository extends JpaRepository<HardConstraints, UUID> {
    /**
     * Retrieves the hardConstraints associated with a specific team.
     *
     * @param team The team for which the hardConstraints is to be retrieved.
     * @return The hardConstraints associated with the specified team.
     */
    HardConstraints findByTeam(Team team);
}
