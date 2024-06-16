package ase.meditrack.controller;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.dto.HolidayDto;
import ase.meditrack.model.entity.Holiday;
import ase.meditrack.model.entity.Preferences;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.entity.enums.HolidayRequestStatus;
import ase.meditrack.repository.HolidayRepository;
import ase.meditrack.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockBean(KeycloakConfig.class)
@MockBean(KeycloakConfig.KeycloakPostConstruct.class)
@MockBean(RealmResource.class)
@Disabled //disabled for now, because cannot mock keycloak properly
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
    private User user;

    @BeforeEach
    void setup() {
        User user = new User(
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
        );
        Preferences preferences = new Preferences(null, List.of(), user);
        user.setPreferences(preferences);
        userRepository.save(user);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_employee", username = USER_ID)
    void test_findAllByUser_succeeds() throws Exception {
        Holiday holiday = createAndSaveHoliday(HolidayRequestStatus.REQUESTED);

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/holiday"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<HolidayDto> holidays = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertAll(
                () -> assertNotNull(holidays),
                () -> assertEquals(1, holidays.size()),
                () -> assertEquals(holiday.getId(), holidays.get(0).id()),
                () -> assertEquals(user.getId(), holidays.get(0).user())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_employee", username = USER_ID)
    void test_findByIdAndUser_succeeds() throws Exception {
        Holiday holiday = createAndSaveHoliday(HolidayRequestStatus.REQUESTED);

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/holiday/" + holiday.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        HolidayDto holidayDto = objectMapper.readValue(response, HolidayDto.class);

        assertAll(
                () -> assertNotNull(holidayDto),
                () -> assertEquals(holiday.getId(), holidayDto.id()),
                () -> assertEquals(holiday.getStartDate(), holidayDto.startDate()),
                () -> assertEquals(holiday.getEndDate(), holidayDto.endDate()),
                () -> assertEquals(holiday.getStatus().name(), holidayDto.status()),
                () -> assertEquals(holiday.getUser().getId(), holidayDto.user())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_dm", username = USER_ID)
    void test_findAllByTeam_succeeds() throws Exception {
        Holiday holiday = createAndSaveHoliday(HolidayRequestStatus.REQUESTED);

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/holiday/team"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<HolidayDto> holidays = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertAll(
                () -> assertNotNull(holidays),
                () -> assertEquals(1, holidays.size()),
                () -> assertEquals(holiday.getId(), holidays.get(0).id())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_employee", username = USER_ID)
    void test_findAllByTeam_withEmployee_fails() throws Exception {
         mockMvc.perform(MockMvcRequestBuilders.get("/api/holiday/team"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_findAll_succeeds() throws Exception {
        Holiday holiday = createAndSaveHoliday(HolidayRequestStatus.REQUESTED);

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/holiday/all"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<HolidayDto> holidays = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertAll(
                () -> assertNotNull(holidays),
                () -> assertEquals(1, holidays.size()),
                () -> assertEquals(holiday.getId(), holidays.get(0).id())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_employee", username = USER_ID)
    void test_createHoliday_succeeds() throws Exception {
        HolidayDto dto = getHolidayDto(null, LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));

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
                () -> assertEquals(dto.status(), created.status()),
                () -> assertEquals(USER_ID, created.user().toString()),
                () -> assertEquals(1, holidayRepository.count())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_delete_succeeds() throws Exception {
        Holiday holiday = createAndSaveHoliday(HolidayRequestStatus.REJECTED);

        assertAll(
                () -> assertEquals(1, holidayRepository.count()),
                () -> assertTrue(holidayRepository.existsById(holiday.getId()))
        );

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/holiday/" + holiday.getId()))
                .andExpect(status().isNoContent());

        assertAll(
                () -> assertFalse(holidayRepository.existsById(holiday.getId())),
                () -> assertEquals(0, holidayRepository.count())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_delete_whenStatusApproved_fails() throws Exception {
        Holiday holiday = createAndSaveHoliday(HolidayRequestStatus.APPROVED);

        assertAll(
                () -> assertEquals(1, holidayRepository.count()),
                () -> assertTrue(holidayRepository.existsById(holiday.getId()))
        );

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/holiday/" + holiday.getId()))
                .andExpect(status().is4xxClientError());

        assertAll(
                () -> assertTrue(holidayRepository.existsById(holiday.getId())),
                () -> assertEquals(1, holidayRepository.count())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_employee", username = USER_ID)
    void test_updateHoliday_succeeds() throws Exception {
        Holiday holiday = createAndSaveHoliday(HolidayRequestStatus.APPROVED);

        HolidayDto holidayDto = getHolidayDto(holiday.getId(), LocalDate.now().plusDays(6),
                LocalDate.now().plusDays(10));

        String response = mockMvc.perform(MockMvcRequestBuilders.put("/api/holiday")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(holidayDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        HolidayDto responseHoliday = objectMapper.readValue(response, HolidayDto.class);

        assertAll(
                () -> assertEquals(holiday.getId(), responseHoliday.id()),
                () -> assertEquals(holidayDto.startDate(), responseHoliday.startDate()),
                () -> assertEquals(holidayDto.endDate(), responseHoliday.endDate()),
                () -> assertEquals(HolidayRequestStatus.REQUESTED.name(), responseHoliday.status()),
                () -> assertEquals(holiday.getUser().getId(), responseHoliday.user()),
                () -> assertEquals(1, holidayRepository.count())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_dm", username = USER_ID)
    void test_updateStatus_succeeds() throws Exception {
        Holiday holiday = createAndSaveHoliday(HolidayRequestStatus.REQUESTED);

        String response = mockMvc.perform(MockMvcRequestBuilders.put("/api/holiday/" + holiday.getId()
                        + "/" + HolidayRequestStatus.APPROVED.name()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        HolidayDto updatedHoliday = objectMapper.readValue(response, HolidayDto.class);

        assertAll(
                () -> assertNotNull(updatedHoliday),
                () -> assertEquals(holiday.getId(), updatedHoliday.id()),
                () -> assertEquals(HolidayRequestStatus.APPROVED.name(), updatedHoliday.status())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_employee", username = USER_ID)
    void test_updateStatus_fails() throws Exception {
        Holiday holiday = createAndSaveHoliday(HolidayRequestStatus.REQUESTED);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/holiday/" + holiday.getId()
                        + "/" + HolidayRequestStatus.APPROVED.name()))
                .andExpect(status().is4xxClientError());
    }

    private Holiday createAndSaveHoliday(HolidayRequestStatus status) {
        Holiday holiday = new Holiday();
        holiday.setStartDate(LocalDate.now().plusDays(5));
        holiday.setEndDate(LocalDate.now().plusDays(10));
        holiday.setUser(user);
        holiday.setStatus(Objects.requireNonNullElse(status, HolidayRequestStatus.REQUESTED));
        return holidayRepository.save(holiday);
    }

    private HolidayDto getHolidayDto(UUID id, LocalDate startDate, LocalDate endDate) {
        return new HolidayDto(
                id,
                startDate,
                endDate,
                null,
                null
        );
    }
}
