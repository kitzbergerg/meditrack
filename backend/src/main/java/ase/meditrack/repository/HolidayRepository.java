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

    /**
     * Find all holidays for a specific user.
     * @param user the user to find the holidays for
     * @return List of all holidays for a specific user
     */
    List<Holiday> findAllByUser(User user);

    /**
     * Find a holiday by id and user.
     * @param id the id of the holiday
     * @param user the user to find the holiday for
     * @return the holiday with the specific id for the user
     */
    Optional<Holiday> findByIdAndUser(UUID id, User user);

    /**
     * Find all holidays for a list of users.
     * @param users the list of users to find the holidays for
     * @return List of all holidays for a list of users
     */
    List<Holiday> findByUserIn(List<User> users);
}
