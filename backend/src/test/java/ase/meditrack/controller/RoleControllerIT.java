package ase.meditrack.controller;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.model.dto.RoleDto;
import ase.meditrack.repository.RoleRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockBean(KeycloakConfig.class)
@MockBean(KeycloakConfig.PostCostruct.class)
@MockBean(RealmResource.class)
class RoleControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RoleRepository roleService;

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_getRoles_succeeds() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/role"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<RoleDto> roles = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertNotNull(roles);
        assertEquals(0, roles.size());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_admin")
    void test_createRole_succeeds() throws Exception {
        RoleDto dto = new RoleDto(
                null,
                "testRole",
                null,
                null
        );

        String response = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/role")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        RoleDto created = objectMapper.readValue(response, RoleDto.class);

        assertNotNull(created);
        assertNotNull(created.id());
        assertEquals(dto.name(), created.name());
        assertEquals(1, roleService.count());
    }
}
