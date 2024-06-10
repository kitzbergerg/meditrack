package ase.meditrack.controller;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.dto.TeamDto;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.repository.TeamRepository;
import ase.meditrack.repository.UserRepository;
import ase.meditrack.repository.MonthlyPlanRepository;
import ase.meditrack.repository.ShiftTypeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.RealmResource;
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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
class TeamControllerIT {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MonthlyPlanRepository monthlyPlanRepository;
    @Autowired
    private ShiftTypeRepository shiftTypeRepository;

    @BeforeEach
    void setup() {
        userRepository.save(new User(
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
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_getTeams_succeeds() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/team"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<TeamDto> teams = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertAll(
                () -> assertNotNull(teams),
                () -> assertEquals(0, teams.size())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_deleteTeam_succeeds() throws Exception {
        Team team = new Team();
        team.setId(null);
        team.setName("testTeam");
        team.setWorkingHours(1);
        teamRepository.save(team);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/team/" + team.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_findTeamById_succeeds() throws Exception {
        TeamDto dto = new TeamDto(
                null,
                "testTeam",
                0,
                null,
                null,
                null,
                null,
                null
        );

        String responseCreate = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/team")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        TeamDto created = objectMapper.readValue(responseCreate, TeamDto.class);

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/team/" + created.id()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        TeamDto foundTeam = objectMapper.readValue(response, TeamDto.class);

        assertAll(
                () -> assertEquals(created.id(), foundTeam.id()),
                () -> assertEquals(created.name(), foundTeam.name())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_updateTeam_succeeds() throws Exception {
        TeamDto dto = new TeamDto(
                null,
                "testTeam",
                0,
                null,
                null,
                null,
                null,
                null
        );

        String responseCreate = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/team")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        TeamDto created = objectMapper.readValue(responseCreate, TeamDto.class);

        TeamDto updatedTeamDto = new TeamDto(
                created.id(),
                "Updated Team",
                2,
                null,
                null,
                null,
                null,
                null);

        String response = mockMvc.perform(MockMvcRequestBuilders.put("/api/team")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updatedTeamDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        TeamDto responseTeam = objectMapper.readValue(response, TeamDto.class);

        assertAll(
                () -> assertEquals(updatedTeamDto.id(), responseTeam.id()),
                () -> assertEquals(updatedTeamDto.name(), responseTeam.name()),
                () -> assertEquals(1, teamRepository.count())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_createTeam_succeeds() throws Exception {
        List<UUID> users = new ArrayList<>();
        users.add(UUID.fromString(USER_ID));

        ShiftType shiftType = new ShiftType();
        shiftType.setName("Test ShiftType");
        shiftType.setColor("#FF0000");
        shiftType.setAbbreviation("TS");
        shiftType.setStartTime(LocalTime.of(8, 0, 0, 0));
        shiftType.setEndTime(LocalTime.of(16, 0, 0, 0));
        shiftType.setBreakStartTime(LocalTime.of(12, 0, 0, 0));
        shiftType.setBreakEndTime(LocalTime.of(12, 30, 0, 0));
        shiftTypeRepository.save(shiftType);
        List<UUID> shiftTypes = new ArrayList<>();
        shiftTypes.add(shiftType.getId());

        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setYear(2024);
        monthlyPlan.setMonth(6);
        monthlyPlan.setPublished(false);
        monthlyPlanRepository.save(monthlyPlan);
        List<UUID> monthlyPlans = new ArrayList<>();
        monthlyPlans.add(monthlyPlan.getId());

        List<UUID> roles = new ArrayList<>();
        roles.add(UUID.randomUUID());

        TeamDto dto = new TeamDto(
                null,
                "testTeam",
                0,
                roles,
                users,
                null,
                monthlyPlans,
                shiftTypes
        );

        String response = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/team")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        TeamDto created = objectMapper.readValue(response, TeamDto.class);

        assertAll(
                () -> assertNotNull(created),
                () -> assertNotNull(created.id()),
                () -> assertEquals(dto.name(), created.name()),
                () -> assertEquals(1, teamRepository.count())
        );
    }
}
