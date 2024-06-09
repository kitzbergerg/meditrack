package ase.meditrack.controller;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.dto.ShiftDto;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.repository.ShiftRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockBean(KeycloakConfig.class)
@MockBean(KeycloakConfig.KeycloakPostConstruct.class)
@MockBean(RealmResource.class)
class ShiftControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ShiftRepository shiftRepository;

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
