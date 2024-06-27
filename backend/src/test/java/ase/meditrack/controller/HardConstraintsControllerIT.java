package ase.meditrack.controller;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.dto.HardConstraintsDto;
import ase.meditrack.model.dto.RoleHardConstraintsDto;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.entity.Team;
import ase.meditrack.repository.UserRepository;
import ase.meditrack.service.MailService;
import ase.meditrack.util.DefaultTestCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
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

@Transactional
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockBean(KeycloakConfig.class)
@MockBean(KeycloakConfig.KeycloakPostConstruct.class)
@MockBean(RealmResource.class)
@MockBean(MailService.class)
public class HardConstraintsControllerIT {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DefaultTestCreator defaultTestCreator;
    private Role role;

    @BeforeEach
    void setup() {
        Team team = defaultTestCreator.createDefaultTeam();
        role = defaultTestCreator.createDefaultRole(team);

        userRepository.save(new User(
                UUID.fromString(USER_ID),
                role,
                1f,
                0,
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
        ));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void findByTeamReturnsHardConstraints() throws Exception {
        String responseOnlyTeam = mockMvc.perform(MockMvcRequestBuilders.get("/api/rules"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        HardConstraintsDto rulesInTeam = objectMapper.readValue(responseOnlyTeam, HardConstraintsDto.class);

        assertNotNull(rulesInTeam);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void createHardConstraints() throws Exception {
        HardConstraintsDto dto = new HardConstraintsDto(
                3,
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
                () -> assertEquals(dto.daytimeRequiredPeople(), created.daytimeRequiredPeople())
        );
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_admin"})
    void test_updateRoleHardConstraints_succeeds() throws Exception {
        RoleHardConstraintsDto roleHardConstraintsDto = new RoleHardConstraintsDto(
                role.getId(),
                4,
                4,
                6,
                2,
                8,
                40,
                2
        );

        String response = mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/rules/roleRules")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(roleHardConstraintsDto))
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        RoleHardConstraintsDto responseDto = objectMapper.readValue(response, RoleHardConstraintsDto.class);

        assertAll(
                () -> assertNotNull(responseDto),
                () -> assertEquals(roleHardConstraintsDto.roleId(), responseDto.roleId()),
                () -> assertEquals(roleHardConstraintsDto.nighttimeRequiredPeople(), responseDto.nighttimeRequiredPeople()),
                () -> assertEquals(roleHardConstraintsDto.daytimeRequiredPeople(), responseDto.daytimeRequiredPeople()),
                () -> assertEquals(roleHardConstraintsDto.allowedFlextimePerMonth(), responseDto.allowedFlextimePerMonth()),
                () -> assertEquals(roleHardConstraintsDto.allowedFlextimeTotal(), responseDto.allowedFlextimeTotal())
        );
    }
}
