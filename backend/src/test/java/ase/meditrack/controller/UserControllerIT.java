package ase.meditrack.controller;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.dto.UserDto;
import ase.meditrack.model.entity.User;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisabledIfSystemProperty(named = "spring.profiles.active", matches = "excludeTestcontainers")
class UserControllerIT {

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

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("admin", users.get(0).username());
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

        assertNotNull(created);
        assertNotNull(created.id());
        assertEquals(dto.username(), created.username());
        assertEquals(2, userService.findAll().size());


        // execute request as user test
        String responseGetTestUser = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/user/{id}", created.id())
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + AuthHelper.getAccessToken("test", "testpass"))
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserDto testUser = objectMapper.readValue(responseGetTestUser, UserDto.class);

        assertNotNull(testUser);
        assertEquals(created.id(), testUser.id());
        assertEquals(created.username(), testUser.username());
    }

    @Test
    void test_deleteUser_succeeds() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/" + savedUser.getId()))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.existsById(savedUser.getId()));
        assertEquals(1, userRepository.count()); // admin user still in repository
    }
}
