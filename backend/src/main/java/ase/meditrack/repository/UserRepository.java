package ase.meditrack.repository;

import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    List<User> findAllByTeam(Team team);

}
