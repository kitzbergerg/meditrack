package ase.meditrack.controller;

import ase.meditrack.model.dto.RoleDto;
import ase.meditrack.service.RoleService;
import ase.meditrack.service.UserService;
import ase.meditrack.util.AuthHelper;
import ase.meditrack.util.KeycloakContainer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RoleControllerIT {

    @Container
    private final static PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    private final static KeycloakContainer<?> KEYCLOAK_CONTAINER = new KeycloakContainer<>();

    @DynamicPropertySource
    private static void startContainers(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RealmResource meditrackRealm;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        userService.createAdminUser();
    }

    @AfterEach
    void tearDown() {
        meditrackRealm.users()
                .list()
                .forEach(user -> meditrackRealm.users().delete(user.getId()));
    }

    @Test
    void test_getRoles_succeeds() throws Exception {
        String response = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/role")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthHelper.getAccessToken("admin", "admin"))
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<RoleDto> roles = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertNotNull(roles);
        assertEquals(0, roles.size());
    }

    @Test
    void test_createRole_succeeds() throws Exception {
        RoleDto dto = new RoleDto(
                null,
                "testRole",
                null
        );

        String response = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/role")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthHelper.getAccessToken("admin", "admin"))
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        RoleDto created = objectMapper.readValue(response, RoleDto.class);

        assertNotNull(created);
        assertNotNull(created.id());
        assertEquals(dto.name(), created.name());
        assertEquals(1, roleService.findAll().size());
    }
}
