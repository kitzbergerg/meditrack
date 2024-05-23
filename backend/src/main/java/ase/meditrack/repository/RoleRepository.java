package ase.meditrack.repository;

import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    /**
     * Retrieves a list of roles associated with a specific team.
     *
     * @param team The team for which roles are to be retrieved.
     * @return A list of roles associated with the specified team.
     */
    List<Role> findAllByTeam(Team team);
}
