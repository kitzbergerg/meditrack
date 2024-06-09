package ase.meditrack.controller;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.dto.PreferencesDto;
import ase.meditrack.model.entity.Preferences;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.PreferencesRepository;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
class PreferencesControllerIT {
    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PreferencesRepository preferencesRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;

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
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_getPreferences_succeeds() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/preferences"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<PreferencesDto> preferences = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertAll(
                () -> assertNotNull(preferences),
                () -> assertEquals(0, preferences.size())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_deletePreferences_succeeds() throws Exception {
        Preferences preferences = new Preferences();
        preferences.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        preferences.setOffDays(Arrays.asList(LocalDate.now(), LocalDate.now().plusDays(1)));
        preferences.setUser(user);
        preferencesRepository.save(preferences);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/preferences/" + preferences.getId()))
                .andExpect(status().isNoContent());

        assertFalse(preferencesRepository.existsById(preferences.getId()));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_findPreferencesById_succeeds() throws Exception {
        Preferences preferences = new Preferences();
        preferences.setId(UUID.fromString(USER_ID));
        preferences.setOffDays(Arrays.asList(LocalDate.now(), LocalDate.now().plusDays(1)));
        preferences.setUser(user);
        preferencesRepository.save(preferences);
        Preferences savedPreferences = preferencesRepository.findById(preferences.getId()).get();

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/preferences/" + savedPreferences.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PreferencesDto foundPreferences = objectMapper.readValue(response, PreferencesDto.class);

        assertAll(
                () -> assertEquals(savedPreferences.getId(), foundPreferences.id()),
                () -> assertEquals(preferences.getOffDays().size(), foundPreferences.offDays().size())
        );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_updatePreferences_succeeds() throws Exception {
        Preferences preferences = new Preferences();
        preferences.setId(UUID.fromString(USER_ID));
        preferences.setOffDays(Arrays.asList(LocalDate.now(), LocalDate.now().plusDays(1)));
        preferences.setUser(user);
        preferencesRepository.save(preferences);
        Preferences savedPreferences = preferencesRepository.findById(preferences.getId()).get();

        PreferencesDto updatedPreferencesDto = new PreferencesDto(savedPreferences.getId(),
                Arrays.asList(LocalDate.now(),  LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(2)));

        String response = mockMvc.perform(MockMvcRequestBuilders.put("/api/preferences")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updatedPreferencesDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PreferencesDto responsePreferences = objectMapper.readValue(response, PreferencesDto.class);

        assertAll(
                () -> assertEquals(savedPreferences.getId(), responsePreferences.id()),
                () -> assertEquals(updatedPreferencesDto.offDays().size(), responsePreferences.offDays().size()),
                () -> assertEquals(1, preferencesRepository.count())
        );
    }
/*
    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_createPreferences_succeeds() throws Exception {
        PreferencesDto dto = new PreferencesDto(
                UUID.fromString(USER_ID),
                Arrays.asList(LocalDate.now(), LocalDate.now().plusDays(1)));

        String response = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/preferences")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        PreferencesDto created = objectMapper.readValue(response, PreferencesDto.class);

        assertNotNull(created);
        assertNotNull(created.id());
        assertEquals(dto.id(), created.id());
        assertEquals(dto.offDays().size(), created.offDays().size());
        assertEquals(1, preferencesRepository.count());
    }

 */
}
