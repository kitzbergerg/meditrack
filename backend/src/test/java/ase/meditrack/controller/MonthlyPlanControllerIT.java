package ase.meditrack.controller;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.dto.MonthlyPlanDto;
import ase.meditrack.model.entity.*;
import ase.meditrack.repository.MonthlyPlanRepository;
import ase.meditrack.repository.ShiftRepository;
import ase.meditrack.repository.UserRepository;
import ase.meditrack.service.MailService;
import ase.meditrack.service.RoleService;
import ase.meditrack.service.ShiftTypeService;
import ase.meditrack.service.TeamService;
import ase.meditrack.util.DefaultTestCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockBean(KeycloakConfig.class)
@MockBean(KeycloakConfig.KeycloakPostConstruct.class)
@MockBean(RealmResource.class)
@MockBean(MailService.class)
class MonthlyPlanControllerIT {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamService teamService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ShiftTypeService shiftTypeService;
    @Autowired
    private MonthlyPlanRepository monthlyPlanRepository;
    @Autowired
    private ShiftRepository shiftRepository;
    @MockBean
    private RealmResource realmResource;
    @MockBean
    private UsersResource usersResource;
    @Autowired
    private DefaultTestCreator defaultTestCreator;

    private Team team;
    private Role role;
    private User user;

    @BeforeEach
    void setup() {
        team = defaultTestCreator.createDefaultTeam();
        role = defaultTestCreator.createDefaultRole(team);

        user = userRepository.save(new User(
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
                null
        ));
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

    //@Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_createPlan_succeeds() throws Exception {
        ShiftType shiftType = new ShiftType(null,
                "test shift type",
                LocalTime.of(8, 0, 0),
                LocalTime.of(12, 0, 0),
                LocalTime.of(10, 0, 0),
                LocalTime.of(10, 30, 0),
                "#000000",
                "t",
                team,
                null,
                null,
                null

        );
        shiftTypeService.create(shiftType, () -> USER_ID);

        String response = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/monthly-plan")
                                .param("year", Year.of(2024).toString())
                                .param("month", Month.APRIL.toString())
                                .param("teamId", team.getId().toString())
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        MonthlyPlanDto created = objectMapper.readValue(response, MonthlyPlanDto.class);

        assertNotNull(created);
        assertNotNull(created.id());
        assertFalse(created.shifts().isEmpty());
        assertEquals(Year.of(2024), created.year());
        assertEquals(Month.APRIL, created.month());
        assertEquals(team.getId(), created.team());

        assertEquals(1, monthlyPlanRepository.count());
        assertTrue(shiftRepository.count() > 0);
    }
}
