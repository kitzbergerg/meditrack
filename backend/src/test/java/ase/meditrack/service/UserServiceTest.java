package ase.meditrack.service;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockBean(KeycloakConfig.class)
@MockBean(KeycloakConfig.KeycloakPostConstruct.class)
@MockBean(RealmResource.class)
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllReturnsAllUsers() {
        User user = new User(
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                null,
                1f,
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        userRepository.save(user);

        List<User> result = userService.findAll();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.size()),
                () -> assertEquals(user, result.get(0))
        );
    }

    @Test
    void findByIdReturnsUser() {
        User user = new User(
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                null,
                1f,
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        userRepository.save(user);
        User savedUser = userRepository.findById(user.getId()).get();

        User result = userService.findById(savedUser.getId());

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(savedUser.getId(), result.getId()),
                () -> assertEquals(user, result)
        );
    }

    @Test
    void findByIdThrowsNotFoundExceptionWhenNotFound() {
        UUID userId = UUID.randomUUID();

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.findById(userId));

        assertEquals("Could not find user with id: " + userId + "!", exception.getMessage());
    }

    @Test
    void createUser() {
        User user = new User(
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                null,
                1f,
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        User result = userService.create(user);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(user, result)
        );
    }

    @Test
    void updateUser() {
        User user = new User(
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                null,
                1f,
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        userRepository.save(user);

        User updatedUser = new User(
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                null,
                1f,
                2,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Principal principal = mock(Principal.class);

        User result = userService.update(updatedUser, principal);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result, updatedUser)
        );
    }

    @Test
    void deleteUser() {
        User user = new User(
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                null,
                1f,
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        userRepository.save(user);
        User savedUser = userRepository.findById(user.getId()).get();

        List<User> resultList = userService.findAll();
        assertEquals(1, resultList.size());

        Principal principal = mock(Principal.class);
        userService.delete(savedUser.getId(), principal);

        resultList = userService.findAll();
        assertEquals(0, resultList.size());
    }
}