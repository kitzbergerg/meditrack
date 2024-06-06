package ase.meditrack.controller;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.dto.HardConstraintsDto;
import ase.meditrack.model.entity.HardConstraints;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.HardConstraintsRepository;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockBean(KeycloakConfig.class)
@MockBean(KeycloakConfig.KeycloakPostConstruct.class)
@MockBean(RealmResource.class)
class HardConstraintsControllerIT {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";
    private static final String OTHER_USER_ID = "00000000-1111-0000-0000-000000000000";
    private static final UUID RULE_ID = UUID.fromString("11111111-1111-0000-0000-000000000000");

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
    void test_getHardConstraintssByTeam_succeeds() throws Exception {
        Team savedTeam = teamService.findById(team.getId());

        System.out.println(savedTeam);

        HardConstraints hardConstraintInTeam = new HardConstraints();
        hardConstraintInTeam.setTeam(savedTeam);
        repository.save(hardConstraintInTeam);

        String responseOnlyTeam = mockMvc.perform(MockMvcRequestBuilders.get("/api/rules"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<HardConstraintsDto> hardConstraintsInTeam = objectMapper.readValue(responseOnlyTeam, new TypeReference<>() {
        });

        assertNotNull(hardConstraintsInTeam);
        assertEquals(1, hardConstraintsInTeam.size());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_createHardConstraint() throws Exception {
        HardConstraintsDto firstDto = new HardConstraintsDto(
                RULE_ID,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/rules")
                                .content(objectMapper.writeValueAsString(firstDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
    }
}
