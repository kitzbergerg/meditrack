package ase.meditrack.controller;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.dto.RoleDto;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.RoleRepository;
import ase.meditrack.repository.ShiftTypeRepository;
import ase.meditrack.repository.UserRepository;
import ase.meditrack.service.TeamService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalTime;
import java.util.ArrayList;
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
@MockBean(KeycloakConfig.class)
@MockBean(KeycloakConfig.KeycloakPostConstruct.class)
@MockBean(RealmResource.class)
class RoleControllerIT {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ShiftTypeRepository shiftTypeRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;
    @Autowired
    private TeamService teamService;
    private Team team;

    @BeforeEach
    void setup() {
        user = userRepository.save(new User(
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
                null
        ));
        team = teamService.create(
                new Team(null, "test team", 40, null, null, null, null, null),
                () -> USER_ID
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_getRoles_succeeds() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/role"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<RoleDto> roles = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertAll(
                () -> assertNotNull(roles),
                () -> assertEquals(0, roles.size())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_getRolesByTeam_succeeds() throws Exception {
        Role roleInTeam = new Role();
        roleInTeam.setName("Role One");
        roleInTeam.setColor("FF0000");
        roleInTeam.setAbbreviation("TR");
        roleInTeam.setUsers(null);
        roleInTeam.setTeam(team);
        roleRepository.save(roleInTeam);

        // other team creation
        String otherUserId = "11111111-1111-1111-1111-111111111111";
        userRepository.save(new User(
                UUID.fromString(otherUserId),
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
        ));

        Team otherTeam = teamService.create(
                new Team(null, "other test team", 40, null, null, null, null, null),
                () -> otherUserId
        );

        Role roleNotInTeam = new Role();
        roleNotInTeam.setName("Role Two");
        roleNotInTeam.setColor("FF0000");
        roleNotInTeam.setAbbreviation("TR");
        roleNotInTeam.setUsers(null);
        roleNotInTeam.setTeam(otherTeam);
        roleRepository.save(roleNotInTeam);

        String responseOnlyTeam = mockMvc.perform(MockMvcRequestBuilders.get("/api/role/team"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<RoleDto> rolesInTeam = objectMapper.readValue(responseOnlyTeam, new TypeReference<>() {
        });

        String responseAll = mockMvc.perform(MockMvcRequestBuilders.get("/api/role"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<RoleDto> allRoles = objectMapper.readValue(responseAll, new TypeReference<>() {
        });

        assertAll(
                () -> assertNotNull(rolesInTeam),
                () -> assertEquals(1, rolesInTeam.size()),
                () -> assertNotNull(allRoles),
                () -> assertEquals(2, allRoles.size())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_deleteRole_succeeds() throws Exception {
        Role role = new Role();
        role.setName("Test Role");
        role.setColor("FF0000");
        role.setAbbreviation("TR");
        role.setUsers(null);
        role.setTeam(team);
        roleRepository.save(role);
        Role savedRole = roleRepository.findById(role.getId()).get();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/role/" + savedRole.getId()))
                .andExpect(status().isNoContent());

        assertAll(
                () -> assertFalse(roleRepository.existsById(savedRole.getId())),
                () -> assertEquals(0, roleRepository.count())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_findRoleById_succeeds() throws Exception {
        Role role = new Role();
        role.setName("Test Role");
        role.setColor("FF0000");
        role.setAbbreviation("TR");
        role.setUsers(null);
        role.setTeam(team);
        roleRepository.save(role);
        Role savedRole = roleRepository.findById(role.getId()).get();

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/role/" + savedRole.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        RoleDto foundRole = objectMapper.readValue(response, RoleDto.class);

        assertAll(
                () -> assertEquals(savedRole.getId(), foundRole.id()),
                () -> assertEquals(role.getName(), foundRole.name()),
                () -> assertEquals(role.getColor(), foundRole.color()),
                () -> assertEquals(role.getAbbreviation(), foundRole.abbreviation())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_updateRole_succeeds() throws Exception {
        Role role = new Role();
        role.setName("Role");
        role.setColor("FF0000");
        role.setAbbreviation("TR");
        role.setUsers(null);
        role.setTeam(team);
        roleRepository.save(role);
        Role savedRole = roleRepository.findById(role.getId()).get();

        RoleDto updatedRoleDto = new RoleDto(savedRole.getId(), "Updated Role", "#000000", "UR",
                null, null, null);

        String response = mockMvc.perform(MockMvcRequestBuilders.put("/api/role")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updatedRoleDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        RoleDto responseRole = objectMapper.readValue(response, RoleDto.class);

        assertAll(
                () -> assertEquals(savedRole.getId(), responseRole.id()),
                () -> assertEquals(updatedRoleDto.name(), responseRole.name()),
                () -> assertEquals(updatedRoleDto.color(), responseRole.color()),
                () -> assertEquals(updatedRoleDto.abbreviation(), responseRole.abbreviation()),
                () -> assertEquals(1, roleRepository.count())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_createRole_succeeds() throws Exception {
        List<UUID> users = new ArrayList<>();
        users.add(user.getId());

        ShiftType shiftType = new ShiftType();
        shiftType.setName("ShiftType");
        shiftType.setStartTime(LocalTime.of(8, 0, 0, 0));
        shiftType.setEndTime(LocalTime.of(16, 0, 0, 0));
        shiftType.setBreakStartTime(LocalTime.of(12, 0, 0, 0));
        shiftType.setBreakEndTime(LocalTime.of(12, 30, 0, 0));
        shiftType.setColor("FF0000");
        shiftType.setAbbreviation("TR");
        shiftType.setTeam(team);
        shiftTypeRepository.save(shiftType);
        List<UUID> shiftTypes = new ArrayList<>();
        shiftTypes.add(shiftType.getId());

        RoleDto dto = new RoleDto(
                null,
                "testRole",
                "#000000",
                "TR",
                users,
                team.getId(),
                shiftTypes
        );

        String response = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/role")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        RoleDto created = objectMapper.readValue(response, RoleDto.class);

        assertAll(
                () -> assertNotNull(created),
                () -> assertNotNull(created.id()),
                () -> assertEquals(dto.name(), created.name()),
                () -> assertEquals(1, roleRepository.count())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_createRoleWithExistingColor_returns422() throws Exception {
        RoleDto firstDto = new RoleDto(
                null,
                "testRole",
                "#FF0000",
                "TR",
                null,
                team.getId(),
                null
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/role")
                                .content(objectMapper.writeValueAsString(firstDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        RoleDto secondDto = new RoleDto(
                null,
                "Role Two",
                "#FF0000",
                "RT",
                null,
                team.getId(),
                null
        );

        MockHttpServletResponse secondResponse = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/role")
                                .content(objectMapper.writeValueAsString(secondDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn().getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), secondResponse.getStatus());
    }
}
