package ase.meditrack.repository;

import ase.meditrack.model.entity.Holiday;
import ase.meditrack.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, UUID> {
    List<Holiday> findAllByUser(User user);
    Optional<Holiday> findByIdAndUser(UUID id, User user);
    List<Holiday> findByUserIn(List<User> users);
}
