package ase.meditrack.controller;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.dto.ShiftDto;
import ase.meditrack.model.dto.SimpleShiftDto;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.ShiftRepository;
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

import java.time.LocalDate;
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
class ShiftControllerIT {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ShiftRepository shiftRepository;
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
    void test_getShifts_succeeds() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/shift"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<ShiftDto> shifts = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertAll(
                () -> assertNotNull(shifts),
                () -> assertEquals(0, shifts.size())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_deleteShift_succeeds() throws Exception {
        Shift shift = new Shift();
        shift.setDate(LocalDate.now().plusDays(70));
        shiftRepository.save(shift);

        Shift savedShift = shiftRepository.findById(shift.getId()).get();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/shift/" + savedShift.getId()))
                .andExpect(status().isNoContent());

        assertAll(
                () -> assertFalse(shiftRepository.existsById(savedShift.getId())),
                () -> assertEquals(0, shiftRepository.count())
        );
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_admin", "SCOPE_employee"}, username = USER_ID)
    void test_findShiftByCurrentMonth_succeeds() throws Exception {
        Shift shiftNextMonth = new Shift();
        shiftNextMonth.setDate(LocalDate.now().plusDays(30));
        List<User> usersNextMonth = new ArrayList<>();
        usersNextMonth.add(user);
        shiftNextMonth.setUsers(usersNextMonth);
        shiftRepository.save(shiftNextMonth);

        Shift shift = new Shift();
        shift.setDate(LocalDate.now().plusDays(1));
        List<User> users = new ArrayList<>();
        users.add(user);
        shift.setUsers(users);
        shiftRepository.save(shift);

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/shift/month"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<SimpleShiftDto> shifts = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertAll(
                () -> assertEquals(2, shiftRepository.count()),
                () -> assertEquals(1, shifts.size()),
                () -> assertEquals(shift.getDate().getMonth(), shifts.get(0).date().getMonth())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_findShiftById_succeeds() throws Exception {
        Shift shift = new Shift();
        shift.setDate(LocalDate.now().plusDays(70));
        shiftRepository.save(shift);
        Shift savedShift = shiftRepository.findById(shift.getId()).get();

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/shift/" + savedShift.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ShiftDto foundShift = objectMapper.readValue(response, ShiftDto.class);

        assertAll(
                () -> assertEquals(savedShift.getId(), foundShift.id()),
                () -> assertEquals(shift.getDate(), foundShift.date())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_createShift_succeeds() throws Exception {
        ShiftDto shiftDto = new ShiftDto(
                null,
                LocalDate.now().plusDays(70),
                null,
                null,
                null,
                null,
                null);

        String response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/shift")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(shiftDto))
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ShiftDto created = objectMapper.readValue(response, ShiftDto.class);

        assertAll(
                () -> assertNotNull(created),
                () -> assertNotNull(created.id()),
                () -> assertEquals(shiftDto.date(), created.date()),
                () -> assertEquals(1, shiftRepository.count())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_updateShift_succeeds() throws Exception {
        Shift shift = new Shift();
        shift.setDate(LocalDate.now().plusDays(70));
        shiftRepository.save(shift);
        Shift savedShift = shiftRepository.findById(shift.getId()).get();

        ShiftDto updatedShiftDto = new ShiftDto(
                savedShift.getId(),
                LocalDate.now().plusDays(80),
                null,
                null,
                null,
                null,
                null);

        String response = mockMvc.perform(MockMvcRequestBuilders.put("/api/shift")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updatedShiftDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ShiftDto responseShift = objectMapper.readValue(response, ShiftDto.class);

        assertAll(
                () -> assertEquals(savedShift.getId(), responseShift.id()),
                () -> assertEquals(updatedShiftDto.date(), responseShift.date()),
                () -> assertEquals(1, shiftRepository.count())
        );
    }
}
