package ase.meditrack.controller;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.dto.ShiftSwapDto;
import ase.meditrack.model.dto.SimpleShiftDto;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftSwap;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.mapper.ShiftMapper;
import ase.meditrack.repository.ShiftRepository;
import ase.meditrack.repository.ShiftSwapRepository;
import ase.meditrack.repository.UserRepository;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
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
    private ShiftMapper shiftMapper;
    private User user;
    private Shift shift;

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
        shift = shiftRepository.save(new Shift());
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
/*
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

        assertFalse(shiftSwapRepository.existsById(savedShiftSwap.getId()));
    }
*/
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
                () -> assertEquals(shiftSwap.getSwapRequestingUser().getId(), foundShiftSwap.swapRequestingUser()),
                () -> assertEquals(shiftSwap.getRequestedShift().getId(), foundShiftSwap.requestedShift())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_createShiftSwap_succeeds() throws Exception {
        SimpleShiftDto shiftDto = shiftMapper.toSimpleShiftDto(shift);
        ShiftSwapDto shiftSwapDto = new ShiftSwapDto(
                null,
                user.getId(),
                shiftDto,
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
                () -> assertEquals(shiftSwapDto.requestedShift(), created.requestedShift()),
                () -> assertEquals(1, shiftSwapRepository.count())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin", username = USER_ID)
    void test_updateShiftSwap_succeeds() throws Exception {
        ShiftSwap shiftSwap = new ShiftSwap();
        shiftSwap.setSwapRequestingUser(user);
        shiftSwap.setRequestedShift(shift);
        shiftSwapRepository.save(shiftSwap);
        ShiftSwap savedShiftSwap = shiftSwapRepository.findById(shiftSwap.getId()).get();

        Shift newRequest = shiftRepository.save(new Shift());
        SimpleShiftDto newRequestDto = shiftMapper.toSimpleShiftDto(newRequest);
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
