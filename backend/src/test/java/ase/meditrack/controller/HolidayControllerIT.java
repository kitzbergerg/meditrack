package ase.meditrack.controller;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.dto.HolidayDto;
import ase.meditrack.model.entity.Holiday;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.entity.enums.HolidayRequestStatus;
import ase.meditrack.repository.HolidayRepository;
import ase.meditrack.repository.RoleRepository;
import ase.meditrack.repository.TeamRepository;
import ase.meditrack.repository.UserRepository;
import ase.meditrack.service.RoleService;
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

import java.time.LocalDate;
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
class HolidayControllerIT {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TeamRepository teamRepository;

    private User user;
    private Team team;
    private Role role;

    @BeforeEach
    void setup() {
        team = teamRepository.save(new Team(
                null,
                "test team",
                40,
                null,
                null,
                null,
                null,
                null));
        role = roleRepository.save(new Role(
                null,
                "employeeRole",
                null,
                null,
                0,
                0,
                0,
                0,
                null,
                team,
                null));
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
                null,
                null
        ));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_employee")
    void test_getFindAllHolidaysByUser_succeeds() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/holiday"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<HolidayDto> holidays = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertAll(
                () -> assertNotNull(holidays),
                () -> assertEquals(0, holidays.size())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_deleteHoliday_succeeds() throws Exception {
        Holiday holiday = new Holiday();
        holiday.setStartDate(LocalDate.now().plusDays(5));
        holiday.setEndDate(LocalDate.now().plusDays(10));
        holiday.setStatus(HolidayRequestStatus.REQUESTED);
        holiday.setUser(user);
        Holiday savedHoliday = holidayRepository.save(holiday);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/holiday/" + savedHoliday.getId()))
                .andExpect(status().isNoContent());

        assertAll(
                () -> assertFalse(holidayRepository.existsById(savedHoliday.getId())),
                () -> assertEquals(0, holidayRepository.count())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_employee")
    void test_findHolidayByUserAndId_succeeds() throws Exception {
        Holiday holiday = new Holiday();
        holiday.setStartDate(LocalDate.now().plusDays(5));
        holiday.setEndDate(LocalDate.now().plusDays(10));
        holiday.setStatus(HolidayRequestStatus.REQUESTED);
        holiday.setUser(user);
        Holiday savedHoliday = holidayRepository.save(holiday);

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/holiday/" + savedHoliday.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        HolidayDto foundHoliday = objectMapper.readValue(response, HolidayDto.class);

        assertAll(
                () -> assertEquals(savedHoliday.getId(), foundHoliday.id()),
                () -> assertEquals(holiday.getStartDate(), foundHoliday.startDate()),
                () -> assertEquals(holiday.getEndDate(), foundHoliday.endDate()),
                () -> assertEquals(holiday.getStatus().name(), foundHoliday.status()),
                () -> assertEquals(holiday.getUser().getId(), foundHoliday.user())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_employee")
    void test_updateHoliday_succeeds() throws Exception {
        Holiday holiday = new Holiday();
        holiday.setStartDate(LocalDate.now().plusDays(5));
        holiday.setEndDate(LocalDate.now().plusDays(10));
        holiday.setStatus(HolidayRequestStatus.REQUESTED);
        holiday.setUser(user);
        Holiday savedHoliday = holidayRepository.save(holiday);

        HolidayDto updatedHolidayDto = new HolidayDto(
                savedHoliday.getId(),
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(11),
                null,
                user.getId()
        );

        String response = mockMvc.perform(MockMvcRequestBuilders.put("/api/holiday")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updatedHolidayDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        HolidayDto responseHoliday = objectMapper.readValue(response, HolidayDto.class);

        assertAll(
                () -> assertEquals(savedHoliday.getId(), responseHoliday.id()),
                () -> assertEquals(updatedHolidayDto.startDate(), responseHoliday.startDate()),
                () -> assertEquals(updatedHolidayDto.endDate(), responseHoliday.endDate()),
                () -> assertEquals(updatedHolidayDto.status(), responseHoliday.status()),
                () -> assertEquals(updatedHolidayDto.user(), responseHoliday.user()),
                () -> assertEquals(1, holidayRepository.count())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_employee", username = USER_ID)
    void test_createHoliday_succeeds() throws Exception {
        HolidayDto dto = new HolidayDto(
                null,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10),
                null,
                user.getId()
        );

        String response = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/holiday")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        HolidayDto created = objectMapper.readValue(response, HolidayDto.class);

        assertAll(
                () -> assertNotNull(created),
                () -> assertNotNull(created.id()),
                () -> assertEquals(dto.startDate(), created.startDate()),
                () -> assertEquals(dto.endDate(), created.endDate()),
                () -> assertEquals(HolidayRequestStatus.REQUESTED.name(), created.status()),
                () -> assertEquals(dto.user(), created.user()),
                () -> assertEquals(1, holidayRepository.count())
        );
    }
}
