package ase.meditrack.controller;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.dto.ShiftTypeDto;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalTime;
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
@MockBean(KeycloakConfig.class)
@MockBean(KeycloakConfig.KeycloakPostConstruct.class)
@MockBean(RealmResource.class)
class ShiftTypeControllerIT {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ShiftTypeRepository shiftTypeRepository;
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
    @WithMockUser(authorities = "SCOPE_admin")
    void test_getShiftTypes_succeeds() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/shift-type"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<ShiftTypeDto> shiftTypes = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertNotNull(shiftTypes);
        assertEquals(0, shiftTypes.size());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_getShiftTypesByTeam_succeeds() throws Exception {
        ShiftType shiftTypeInTeam = new ShiftType();
        shiftTypeInTeam.setName("ShiftType One");
        shiftTypeInTeam.setStartTime(LocalTime.of(8, 0, 0, 0));
        shiftTypeInTeam.setEndTime(LocalTime.of(16, 0, 0, 0));
        shiftTypeInTeam.setBreakStartTime(LocalTime.of(12, 0, 0, 0));
        shiftTypeInTeam.setBreakEndTime(LocalTime.of(12, 30, 0, 0));
        shiftTypeInTeam.setColor("FF0000");
        shiftTypeInTeam.setAbbreviation("TR");
        shiftTypeInTeam.setTeam(team);
        shiftTypeRepository.save(shiftTypeInTeam);

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

        ShiftType shiftTypeNotInTeam = new ShiftType();
        shiftTypeNotInTeam.setName("ShiftType Two");
        shiftTypeNotInTeam.setStartTime(LocalTime.of(8, 0, 0, 0));
        shiftTypeNotInTeam.setEndTime(LocalTime.of(16, 0, 0, 0));
        shiftTypeNotInTeam.setBreakStartTime(LocalTime.of(12, 0, 0, 0));
        shiftTypeNotInTeam.setBreakEndTime(LocalTime.of(12, 30, 0, 0));
        shiftTypeNotInTeam.setColor("FF0000");
        shiftTypeNotInTeam.setAbbreviation("TR");
        shiftTypeInTeam.setTeam(otherTeam);
        shiftTypeRepository.save(shiftTypeNotInTeam);

        String responseOnlyTeam = mockMvc.perform(MockMvcRequestBuilders.get("/api/shift-type/team"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<ShiftTypeDto> shiftTypesInTeam = objectMapper.readValue(responseOnlyTeam, new TypeReference<>() {
        });

        String responseAll = mockMvc.perform(MockMvcRequestBuilders.get("/api/shift-type"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<ShiftTypeDto> allShiftTypes = objectMapper.readValue(responseAll, new TypeReference<>() {
        });

        assertNotNull(shiftTypesInTeam);
        assertEquals(1, shiftTypesInTeam.size());

        assertNotNull(allShiftTypes);
        assertEquals(2, allShiftTypes.size());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_deleteShiftType_succeeds() throws Exception {
        ShiftType shiftType = new ShiftType();
        shiftType.setName("Test ShiftType");
        shiftType.setStartTime(LocalTime.of(8, 0, 0, 0));
        shiftType.setEndTime(LocalTime.of(16, 0, 0, 0));
        shiftType.setBreakStartTime(LocalTime.of(12, 0, 0, 0));
        shiftType.setBreakEndTime(LocalTime.of(12, 30, 0, 0));
        shiftType.setColor("FF0000");
        shiftType.setAbbreviation("TR");
        shiftType.setTeam(team);
        shiftTypeRepository.save(shiftType);

        ShiftType savedShiftType = shiftTypeRepository.findById(shiftType.getId()).get();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/shift-type/" + savedShiftType.getId()))
                .andExpect(status().isNoContent());

        assertFalse(shiftTypeRepository.existsById(savedShiftType.getId()));
        assertEquals(0, shiftTypeRepository.count());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_findShiftTypeById_succeeds() throws Exception {
        ShiftType shiftType = new ShiftType();
        shiftType.setName("Test ShiftType");
        shiftType.setStartTime(LocalTime.of(8, 0, 0, 0));
        shiftType.setEndTime(LocalTime.of(16, 0, 0, 0));
        shiftType.setBreakStartTime(LocalTime.of(12, 0, 0, 0));
        shiftType.setBreakEndTime(LocalTime.of(12, 30, 0, 0));
        shiftType.setColor("FF0000");
        shiftType.setAbbreviation("TR");
        shiftType.setTeam(team);
        shiftTypeRepository.save(shiftType);
        ShiftType savedShiftType = shiftTypeRepository.findById(shiftType.getId()).get();

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/shift-type/" + savedShiftType.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ShiftTypeDto foundShiftType = objectMapper.readValue(response, ShiftTypeDto.class);

        assertEquals(savedShiftType.getId(), foundShiftType.id());
        assertEquals(shiftType.getName(), foundShiftType.name());
        assertEquals(shiftType.getColor(), foundShiftType.color());
        assertEquals(shiftType.getAbbreviation(), foundShiftType.abbreviation());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_createShiftType_succeeds() throws Exception {
        ShiftTypeDto shiftTypeDto = new ShiftTypeDto(
                null,
                "Shift Type",
                LocalTime.of(8, 0, 0, 0),
                LocalTime.of(16, 0, 0, 0),
                LocalTime.of(12, 0, 0, 0),
                LocalTime.of(12, 30, 0, 0),
                "Day",
                "#ff0000",
                "ST",
                team.getId(),
                null,
                null,
                null);

        String response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/shift-type")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(shiftTypeDto))
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ShiftTypeDto created = objectMapper.readValue(response, ShiftTypeDto.class);

        assertNotNull(created);
        assertNotNull(created.id());
        assertEquals(shiftTypeDto.name(), created.name());
        assertEquals(shiftTypeDto.startTime(), created.startTime());
        assertEquals(shiftTypeDto.endTime(), created.endTime());
        assertEquals(shiftTypeDto.breakStartTime(), created.breakStartTime());
        assertEquals(shiftTypeDto.breakEndTime(), created.breakEndTime());
        assertEquals(shiftTypeDto.color(), created.color());
        assertEquals(shiftTypeDto.abbreviation(), created.abbreviation());
        assertEquals(1, shiftTypeRepository.count());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_updateShiftType_succeeds() throws Exception {
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
        ShiftType savedShiftType = shiftTypeRepository.findById(shiftType.getId()).get();

        ShiftTypeDto updatedShiftTypeDto = new ShiftTypeDto(
                savedShiftType.getId(),
                "Updated ShiftType",
                LocalTime.of(8, 0, 0, 0),
                LocalTime.of(17, 0, 0, 0),
                LocalTime.of(12, 0, 0, 0),
                LocalTime.of(13, 0, 0, 0),
                "Day",
                "#000000",
                "STD",
                team.getId(),
                null,
                null,
                null);

        String response = mockMvc.perform(MockMvcRequestBuilders.put("/api/shift-type")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updatedShiftTypeDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ShiftTypeDto responseShiftType = objectMapper.readValue(response, ShiftTypeDto.class);

        assertEquals(savedShiftType.getId(), responseShiftType.id());
        assertEquals(updatedShiftTypeDto.name(), responseShiftType.name());
        assertEquals(updatedShiftTypeDto.startTime(), responseShiftType.startTime());
        assertEquals(updatedShiftTypeDto.endTime(), responseShiftType.endTime());
        assertEquals(updatedShiftTypeDto.breakStartTime(), responseShiftType.breakStartTime());
        assertEquals(updatedShiftTypeDto.breakEndTime(), responseShiftType.breakEndTime());
        assertEquals(updatedShiftTypeDto.color(), responseShiftType.color());
        assertEquals(updatedShiftTypeDto.abbreviation(), responseShiftType.abbreviation());
        assertEquals(1, shiftTypeRepository.count());
    }
}
