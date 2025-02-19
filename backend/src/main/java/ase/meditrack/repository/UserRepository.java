package ase.meditrack.repository;

import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Retrieves a list of users associated with a specific team.
     *
     * @param team The team for which users are to be retrieved.
     * @return A list of users associated with the specified team.
     */
    List<User> findAllByTeam(Team team);

    /**
     * Retrieves a list of users associated with a specific role.
     *
     * @param role The role for which users are to be retrieved.
     * @return A list of users associated with the specified role.
     */
    List<User> findAllByRole(Role role);

}
