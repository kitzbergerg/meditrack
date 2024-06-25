package ase.meditrack.controller;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.dto.RoleDto;
import ase.meditrack.model.entity.Preferences;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.RoleRepository;
import ase.meditrack.repository.UserRepository;
import ase.meditrack.service.MailService;
import ase.meditrack.util.DefaultTestCreator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.admin.client.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockBean(KeycloakConfig.class)
@MockBean(KeycloakConfig.KeycloakPostConstruct.class)
@MockBean(MailService.class)
class RoleControllerIT {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DefaultTestCreator defaultTestCreator;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @MockBean
    private RealmResource realmResource;
    @MockBean
    private UsersResource usersResource;

    private Team team;

    @BeforeEach
    void setup() {
        team = defaultTestCreator.createDefaultTeam();
        Role role = defaultTestCreator.createDefaultRole(team);

        User user = new User(
                UUID.fromString(USER_ID),
                role,
                1f,
                0,
                null,
                team,
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
        Preferences preferences = new Preferences(null, List.of(), user);
        user.setPreferences(preferences);
        userRepository.save(user);

        // Mock the realmResource and usersResource behavior
        when(realmResource.users()).thenReturn(usersResource);

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(USER_ID);
        userRepresentation.setUsername("testUser");

        UserResource userResource = mock(UserResource.class);
        when(usersResource.get(USER_ID)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_getRoles_succeeds() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/role"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<RoleDto> roles = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertNotNull(roles);
        assertEquals(1, roles.size());
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_admin", "SCOPE_employee"}, username = USER_ID)
    void test_findRoleById_succeeds() throws Exception {
        Role role = new Role();
        role.setWorkingHours(10);
        role.setAllowedFlextimePerMonth(2);
        role.setAllowedFlextimeTotal(8);
        role.setDaytimeRequiredPeople(3);
        role.setNighttimeRequiredPeople(3);
        role.setMaxConsecutiveShifts(2);
        role.setMaxWeeklyHours(40);
        role.setName("Test");
        role.setColor("#000000");
        role.setAbbreviation("TR");
        role.setTeam(team);
        roleRepository.save(role);
        roleRepository.flush();

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/role/" + role.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        RoleDto roleDto = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertAll(
                () -> assertEquals(role.getName(), roleDto.name()),
                () -> assertEquals(role.getColor(), roleDto.color()),
                () -> assertEquals(role.getAbbreviation(), roleDto.abbreviation())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_createRole_succeeds() throws Exception {
        RoleDto dto = new RoleDto(
                null,
                "testRole",
                "#000000",
                "TR",
                null,
                team.getId(),
                null
        );

        String response = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/role")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        RoleDto created = objectMapper.readValue(response, RoleDto.class);

        assertNotNull(created);
        assertNotNull(created.id());
        assertEquals(dto.name(), created.name());
        assertEquals(2, roleRepository.count());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_updateRole_succeeds() throws Exception {
        Role role = new Role();
        role.setWorkingHours(10);
        role.setAllowedFlextimePerMonth(2);
        role.setAllowedFlextimeTotal(8);
        role.setDaytimeRequiredPeople(3);
        role.setNighttimeRequiredPeople(3);
        role.setMaxConsecutiveShifts(2);
        role.setMaxWeeklyHours(40);
        role.setName("Test");
        role.setColor("#000000");
        role.setAbbreviation("TR");
        role.setTeam(team);
        roleRepository.save(role);
        roleRepository.flush();
        RoleDto dto = new RoleDto(
                role.getId(),
                "testRole",
                "#000000",
                "TR",
                null,
                team.getId(),
                null
        );

        String response = mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/role")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        RoleDto updated = objectMapper.readValue(response, RoleDto.class);

        assertNotNull(updated);
        assertNotNull(updated.id());
        assertEquals(dto.name(), updated.name());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_deleteRole_succeeds() throws Exception {
        Role role = new Role();
        role.setWorkingHours(10);
        role.setAllowedFlextimePerMonth(2);
        role.setAllowedFlextimeTotal(8);
        role.setDaytimeRequiredPeople(3);
        role.setNighttimeRequiredPeople(3);
        role.setMaxConsecutiveShifts(2);
        role.setMaxWeeklyHours(40);
        role.setName("Test");
        role.setColor("#000000");
        role.setAbbreviation("TR");
        role.setTeam(team);
        roleRepository.save(role);
        roleRepository.flush();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/role/" + role.getId()))
                .andExpect(status().isNoContent());

        assertFalse(roleRepository.existsById(role.getId()));
    }
}
