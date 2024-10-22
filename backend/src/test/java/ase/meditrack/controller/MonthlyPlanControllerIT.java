package ase.meditrack.controller;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.dto.*;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.Role;
import ase.meditrack.repository.MonthlyPlanRepository;
import ase.meditrack.repository.ShiftRepository;
import ase.meditrack.repository.UserRepository;
import ase.meditrack.service.MailService;
import ase.meditrack.service.ShiftTypeService;
import ase.meditrack.util.DefaultTestCreator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
class MonthlyPlanControllerIT {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    private User user;
    private Team team;
    @Autowired
    private ShiftTypeService shiftTypeService;
    @Autowired
    private MonthlyPlanRepository repository;
    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private DefaultTestCreator defaultTestCreator;
    private Role role;
    @MockBean
    private RealmResource realmResource;
    @MockBean
    private UsersResource usersResource;

    @BeforeEach
    void setup() {
        team = defaultTestCreator.createDefaultTeam();
        role = defaultTestCreator.createDefaultRole(team);

        user = userRepository.save(new User(
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

    // @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_createMonthlyPlan_succeeds() throws Exception {
        ShiftType shiftType = new ShiftType(
                null,
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

        Team finalTeam = team;
        assertAll(
                () -> assertNotNull(created),
                () -> assertNotNull(created.id()),
                () -> assertFalse(created.shifts().isEmpty()),
                () -> assertEquals(Year.of(2024), created.year()),
                () -> assertEquals(Month.APRIL, created.month()),
                () -> assertEquals(finalTeam.getId(), created.team()),
                () -> assertEquals(1, repository.count()),
                () -> assertTrue(shiftRepository.count() > 0)
        );
    }

    // @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_getMonthlyPlanByTeamMonthYear_succeeds() throws Exception {
        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setTeam(team);
        monthlyPlan.setYear(2024);
        monthlyPlan.setMonth(6);
        monthlyPlan.setPublished(false);
        repository.save(monthlyPlan);
        MonthlyPlan savedMonthlyPlan = repository.findById(monthlyPlan.getId()).get();

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/monthly-plan/team")
                        .param("year", Year.of(2024).toString())
                        .param("month", Month.JUNE.toString())
                        .param("teamId", team.getId().toString()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MonthlyPlanDto foundMonthlyPlan = objectMapper.readValue(response, MonthlyPlanDto.class);

        assertAll(
                () -> assertEquals(savedMonthlyPlan.getId(), foundMonthlyPlan.id()),
                () -> assertEquals(Month.JUNE, foundMonthlyPlan.month()),
                () -> assertEquals(Year.of(2024), foundMonthlyPlan.year()),
                () -> assertEquals(savedMonthlyPlan.getPublished(), foundMonthlyPlan.published())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_getMonthlyPlans_succeeds() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/monthly-plan"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<MonthlyPlanDto> monthlyPlans = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertAll(
                () -> assertNotNull(monthlyPlans),
                () -> assertEquals(0, monthlyPlans.size())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_deleteMonthlyPlan_succeeds() throws Exception {
        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setTeam(team);
        monthlyPlan.setYear(2024);
        monthlyPlan.setMonth(6);
        monthlyPlan.setPublished(false);
        repository.save(monthlyPlan);
        MonthlyPlan savedMonthlyPlan = repository.findById(monthlyPlan.getId()).get();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/monthly-plan/" + savedMonthlyPlan.getId()))
                .andExpect(status().isNoContent());

        assertAll(
                () -> assertFalse(repository.existsById(savedMonthlyPlan.getId())),
                () -> assertEquals(0, repository.count())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_findMonthlyPlanById_succeeds() throws Exception {
        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setTeam(team);
        monthlyPlan.setYear(2024);
        monthlyPlan.setMonth(6);
        monthlyPlan.setPublished(false);
        repository.save(monthlyPlan);
        MonthlyPlan savedMonthlyPlan = repository.findById(monthlyPlan.getId()).get();

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/monthly-plan/" + savedMonthlyPlan.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MonthlyPlanDto foundMonthlyPlan = objectMapper.readValue(response, MonthlyPlanDto.class);

        assertAll(
                () -> assertEquals(savedMonthlyPlan.getId(), foundMonthlyPlan.id()),
                () -> assertEquals(Month.JUNE, foundMonthlyPlan.month()),
                () -> assertEquals(Year.of(2024), foundMonthlyPlan.year()),
                () -> assertEquals(savedMonthlyPlan.getPublished(), foundMonthlyPlan.published())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_updateMonthlyPlan_succeeds() throws Exception {
        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setTeam(team);
        monthlyPlan.setYear(2024);
        monthlyPlan.setMonth(6);
        monthlyPlan.setPublished(false);
        repository.save(monthlyPlan);

        MonthlyPlan savedMonthlyPlan = repository.findById(monthlyPlan.getId()).get();

        List<UUID> users = new ArrayList<>();
        users.add(user.getId());
        ShiftDto shiftDto = new ShiftDto(null, null, false, null, null, users, null, null);
        List<ShiftDto> shifts = new ArrayList<>();
        shifts.add(shiftDto);
        MonthlyPlanDto updatedMonthlyPlanDto = new MonthlyPlanDto(
                savedMonthlyPlan.getId(),
                Month.of(5),
                Year.of(2024),
                true,
                team.getId(),
                shifts,
                null
        );

        String response = mockMvc.perform(MockMvcRequestBuilders.put("/api/monthly-plan")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updatedMonthlyPlanDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MonthlyPlanDto responseMonthlyPlan = objectMapper.readValue(response, MonthlyPlanDto.class);

        assertAll(
                () -> assertEquals(savedMonthlyPlan.getId(), responseMonthlyPlan.id()),
                () -> assertEquals(updatedMonthlyPlanDto.month(), responseMonthlyPlan.month()),
                () -> assertEquals(updatedMonthlyPlanDto.year(), responseMonthlyPlan.year()),
                () -> assertEquals(updatedMonthlyPlanDto.published(), responseMonthlyPlan.published())
        );
    }
}
