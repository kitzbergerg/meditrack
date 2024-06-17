package ase.meditrack.controller;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.dto.ShiftSwapDto;
import ase.meditrack.model.dto.SimpleShiftDto;
import ase.meditrack.model.dto.SimpleShiftSwapDto;
import ase.meditrack.model.dto.SimpleShiftTypeDto;
import ase.meditrack.model.entity.*;
import ase.meditrack.model.mapper.ShiftMapper;
import ase.meditrack.repository.*;
import ase.meditrack.service.TeamService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.Assert;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
class ShiftSwapControllerIT {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ShiftSwapRepository shiftSwapRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private ShiftTypeRepository shiftTypeRepository;
    @Autowired
    private ShiftMapper shiftMapper;
    private User user;
    private Shift shift;
    @Autowired
    private TeamService teamService;
    private Team team;
    @Autowired
    private MonthlyPlanRepository monthlyPlanRepository;
    @Autowired
    private TeamRepository teamRepository;

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
                null,
                null
        ));
        shift = shiftRepository.save(new Shift());
        team = teamService.create(
                new Team(null, "test team", 40, null, null, null, null, null),
                () -> USER_ID
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_getShiftSwaps_succeeds() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/shift-swap"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<ShiftSwapDto> shiftSwaps = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertAll(
                () -> assertNotNull(shiftSwaps),
                () -> assertEquals(0, shiftSwaps.size())
        );
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_admin", "SCOPE_employee"}, username = USER_ID)
    void test_findShiftSwapsByUserAndCurrentMonth_succeeds() throws Exception {
        userRepository.flush();
        Shift shiftBefore = new Shift();
        shiftBefore.setDate(LocalDate.now().minusDays(40));
        List<User> usersBefore = new ArrayList<>();
        usersBefore.add(user);
        shiftBefore.setUsers(usersBefore);
        shiftRepository.save(shiftBefore);

        ShiftSwap shiftSwapBefore = new ShiftSwap();
        shiftSwapBefore.setSwapRequestingUser(user);
        shiftSwapBefore.setRequestedShift(shiftBefore);
        shiftSwapRepository.save(shiftSwapBefore);

        Shift shiftComing = new Shift();
        shiftComing.setDate(LocalDate.now().plusDays(1));
        List<User> usersComing = new ArrayList<>();
        usersComing.add(user);
        shiftComing.setUsers(usersComing);
        shiftRepository.save(shiftComing);

        ShiftSwap shiftSwap = new ShiftSwap();
        shiftSwap.setSwapRequestingUser(user);
        shiftSwap.setRequestedShift(shiftComing);
        shiftSwapRepository.save(shiftSwap);

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/shift-swap/own-offers"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<ShiftSwapDto> shiftSwaps = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertAll(
                () -> Assert.assertEquals(2, shiftSwapRepository.count()),
                () -> Assert.assertEquals(1, shiftSwaps.size())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_deleteShiftSwap_succeeds() throws Exception {
        ShiftSwap shiftSwap = new ShiftSwap();
        shiftSwap.setSwapRequestingUser(user);
        shiftSwap.setRequestedShift(shift);
        shiftSwapRepository.save(shiftSwap);

        ShiftSwap savedShiftSwap = shiftSwapRepository.findById(shiftSwap.getId()).get();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/shift-swap/" + savedShiftSwap.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_findShiftSwapById_succeeds() throws Exception {
        ShiftSwap shiftSwap = new ShiftSwap();
        shiftSwap.setSwapRequestingUser(user);
        shiftSwap.setRequestedShift(shift);
        shiftSwapRepository.save(shiftSwap);
        ShiftSwap savedShiftSwap = shiftSwapRepository.findById(shiftSwap.getId()).get();

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/shift-swap/" + savedShiftSwap.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ShiftSwapDto foundShiftSwap = objectMapper.readValue(response, ShiftSwapDto.class);

        assertAll(
                () -> assertEquals(savedShiftSwap.getId(), foundShiftSwap.id()),
                () -> assertEquals(shiftSwap.getSwapRequestingUser().getId(), foundShiftSwap.swapRequestingUser())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_createShiftSwap_succeeds() throws Exception {
        userRepository.flush();
        teamRepository.flush();
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
        shift.setShiftType(shiftType);

        SimpleShiftDto shiftDto = shiftMapper.toSimpleShiftDto(shift);
        SimpleShiftSwapDto shiftSwapDto = new SimpleShiftSwapDto(
                null,
                user.getId(),
                shiftDto.id(),
                null,
                null,
                null,
                null
        );

        String response = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/shift-swap")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(shiftSwapDto))
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ShiftSwapDto created = objectMapper.readValue(response, ShiftSwapDto.class);

        assertAll(
                () -> assertNotNull(created),
                () -> assertNotNull(created.id()),
                () -> assertEquals(shiftSwapDto.swapRequestingUser(), created.swapRequestingUser()),
                () -> assertEquals(shiftSwapDto.requestedShift(), created.requestedShift().id()),
                () -> assertEquals(1, shiftSwapRepository.count())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_updateShiftSwap_succeeds() throws Exception {
        userRepository.flush();
        ShiftSwap shiftSwap = new ShiftSwap();
        shiftSwap.setSwapRequestingUser(user);
        shiftSwap.setRequestedShift(shift);
        shiftSwapRepository.save(shiftSwap);
        ShiftSwap savedShiftSwap = shiftSwapRepository.findById(shiftSwap.getId()).get();

        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setTeam(team);
        monthlyPlan.setYear(2024);
        monthlyPlan.setMonth(6);
        monthlyPlan.setPublished(false);
        monthlyPlanRepository.save(monthlyPlan);
        List<UUID> users = new ArrayList<>();
        users.add(user.getId());
        SimpleShiftTypeDto simpleShiftTypeDto = new SimpleShiftTypeDto(null,
                "Simple",
                LocalTime.of(8, 0, 0),
                LocalTime.of(16, 0, 0),
                LocalTime.of(12, 0, 0),
                LocalTime.of(12, 30, 0),
                "Day",
                "#FF0000",
                "SD",
                team.getId()
        );
        Shift newRequest = shiftRepository.save(new Shift());
        SimpleShiftDto newRequestDto = new SimpleShiftDto(newRequest.getId(),
                newRequest.getDate(),
                monthlyPlan.getId(),
                simpleShiftTypeDto,
                users);
        ShiftSwapDto updatedShiftSwapDto = new ShiftSwapDto(
                savedShiftSwap.getId(),
                user.getId(),
                newRequestDto,
                null,
                null,
                null,
                null);

        String response = mockMvc.perform(MockMvcRequestBuilders.put("/api/shift-swap")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updatedShiftSwapDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ShiftSwapDto responseShiftSwap = objectMapper.readValue(response, ShiftSwapDto.class);

        assertAll(
                () -> assertEquals(savedShiftSwap.getId(), responseShiftSwap.id()),
                () -> assertEquals(updatedShiftSwapDto.swapRequestingUser(), responseShiftSwap.swapRequestingUser()),
                () -> assertEquals(updatedShiftSwapDto.requestedShift(), responseShiftSwap.requestedShift()),
                () -> assertEquals(1, shiftSwapRepository.count())
        );
    }
}
