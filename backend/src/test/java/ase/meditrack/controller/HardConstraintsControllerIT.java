package ase.meditrack.controller;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.dto.HardConstraintsDto;
import ase.meditrack.model.entity.HardConstraints;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.entity.Team;
import ase.meditrack.repository.HardConstraintsRepository;
import ase.meditrack.repository.UserRepository;
import ase.meditrack.service.TeamService;
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
public class HardConstraintsControllerIT {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private HardConstraintsRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamService teamService;
    private Team team;

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
        team = teamService.create(
                new Team(null, "test team", 40, null, null, null, null, null),
                () -> USER_ID
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void findByTeamReturnsHardConstraints() throws Exception {
        HardConstraints hardConstraint = new HardConstraints();
        hardConstraint.setId(team.getId());
        hardConstraint.setMandatoryOffDays(2);
        repository.save(hardConstraint);

        String responseOnlyTeam = mockMvc.perform(MockMvcRequestBuilders.get("/api/rules"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        HardConstraintsDto ruleInTeam = objectMapper.readValue(responseOnlyTeam, HardConstraintsDto.class);

        assertAll(
                () -> assertNotNull(ruleInTeam),
                () -> assertEquals(hardConstraint.getMandatoryOffDays(), ruleInTeam.mandatoryOffDays())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void createHardConstraints() throws Exception {
        HardConstraintsDto dto = new HardConstraintsDto(
                team.getId(),
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
                        MockMvcRequestBuilders.post("/api/rules")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        HardConstraintsDto created = objectMapper.readValue(response, HardConstraintsDto.class);

        assertAll(
                () -> assertNotNull(created),
                () -> assertNotNull(created.id()),
                () -> assertEquals(dto.id(), created.id()),
                () -> assertEquals(1, repository.count())
        );
    }
}
