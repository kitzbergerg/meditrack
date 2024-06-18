package ase.meditrack.controller;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.dto.MonthlyWorkDetailsDto;
import ase.meditrack.model.dto.UserDto;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.TeamRepository;
import ase.meditrack.repository.UserRepository;
import ase.meditrack.service.UserService;
import ase.meditrack.util.AuthHelper;
import ase.meditrack.util.KeycloakContainer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisabledIfSystemProperty(named = "spring.profiles.active", matches = "excludeTestcontainers")
class UserControllerIT {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";

    @Container
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    private static final KeycloakContainer<?> KEYCLOAK_CONTAINER = new KeycloakContainer<>();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RealmResource meditrackRealm;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private KeycloakConfig.KeycloakPostConstruct keycloakConfigKeycloakPostConstruct;
    @Autowired
    private TeamRepository teamRepository;

    @DynamicPropertySource
    private static void startContainers(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);
    }

    @BeforeEach
    void setUp() {
        keycloakConfigKeycloakPostConstruct.createAdminUser();
    }

    @AfterEach
    void tearDown() {
        meditrackRealm.users()
                .list()
                .forEach(user -> meditrackRealm.users().delete(user.getId()));
    }

    @Test
    void test_getUsers_succeeds() throws Exception {
        String response = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/user")
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + AuthHelper.getAccessToken("admin", "admin"))
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<UserDto> users = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertAll(
                () -> assertNotNull(users),
                () -> assertEquals(1, users.size()),
                () -> assertEquals("admin", users.get(0).username())
        );
    }

    @Test
    void test_getMonthlyDetails_succeeds() throws Exception {
        UUID userId = UUID.randomUUID();
        Year year = Year.of(2023);
        Month month = Month.JUNE;

        String response = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/user/monthly-details")
                                .param("year", String.valueOf(year.getValue()))
                                .param("month", month.name())
                                .param("userId", userId.toString())
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + AuthHelper.getAccessToken("admin", "admin"))
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MonthlyWorkDetailsDto monthlyWorkDetails = objectMapper.readValue(response, MonthlyWorkDetailsDto.class);

        assertAll(
                () -> assertNotNull(monthlyWorkDetails),
                () -> assertEquals(userId, monthlyWorkDetails.userId())
        );
    }

    @Test
    void test_createUser_succeeds() throws Exception {
        UserDto dto = new UserDto(
                null,
                "test",
                "testpass",
                "test@test.test",
                "test",
                "test",
                List.of("employee"),
                null,
                1f,
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

        String response = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/user")
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + AuthHelper.getAccessToken("admin", "admin"))
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        UserDto created = objectMapper.readValue(response, UserDto.class);

        assertAll(
                () -> assertNotNull(created),
                () -> assertNotNull(created.id()),
                () -> assertEquals(dto.username(), created.username()),
                () -> assertEquals(2, userService.findAll().size())
        );

        // execute request as user test
        String responseGetTestUser = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/user/{id}", created.id())
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + AuthHelper.getAccessToken("test", "testpass"))
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserDto testUser = objectMapper.readValue(responseGetTestUser, UserDto.class);

        assertAll(
                () -> assertNotNull(testUser),
                () -> assertEquals(created.id(), testUser.id()),
                () -> assertEquals(created.username(), testUser.username())
        );
    }

    @Test
    void test_findUserById_succeeds() throws Exception {
        User user = new User(
                UUID.fromString(USER_ID),
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
                null,
                null
        );
        userRepository.save(user);
        userRepository.flush();

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/user/{id}" + user.getId())
                        .header(HttpHeaders.AUTHORIZATION,
                                "Bearer " + AuthHelper.getAccessToken("admin", "admin")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UserDto foundUser = objectMapper.readValue(response, UserDto.class);

        assertEquals(user.getId(), foundUser.id());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_updateUser_succeeds() throws Exception {
        User user = new User(
                UUID.fromString(USER_ID),
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
                null,
                null
        );
        userRepository.save(user);
        userRepository.flush();

        UserDto updateUserDto = new UserDto(
                user.getId(),
                null,
                "testpassword",
                "test@test.test",
                "test",
                "testLast",
                List.of("employee"),
                null,
                1f,
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

        String response = mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/user")
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + AuthHelper.getAccessToken("admin", "admin"))
                                .content(objectMapper.writeValueAsString(updateUserDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        UserDto updated = objectMapper.readValue(response, UserDto.class);

        assertAll(
                () -> assertNotNull(updated),
                () -> assertEquals(updated.id(), updateUserDto.id()),
                () -> assertEquals(updated.username(), updateUserDto.username())
        );
    }

    @Test
    void test_deleteUser_succeeds() throws Exception {
        User user = new User(
                UUID.fromString(USER_ID),
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
                null,
                null
        );
        userRepository.save(user);
        userRepository.flush();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/{id}" + user.getId())
                        .header(HttpHeaders.AUTHORIZATION,
                                "Bearer " + AuthHelper.getAccessToken("admin", "admin")))
                .andExpect(status().isNoContent());

        assertAll(
                () -> assertFalse(userRepository.existsById(user.getId())),
                () -> assertEquals(1, userRepository.count()) // admin user still in repository
        );
    }
}
