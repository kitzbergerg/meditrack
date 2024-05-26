package ase.meditrack.config;

import ase.meditrack.model.dto.UserDto;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.mapper.UserMapper;
import ase.meditrack.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration("keycloakConfig")
@Slf4j
public class KeycloakConfig {

    @Bean
    Keycloak keycloak(KeycloakProperties properties) {
        return KeycloakBuilder.builder()
                .serverUrl(properties.getServerUrl())
                .realm("master")
                .clientId("admin-cli")
                .grantType(OAuth2Constants.PASSWORD)
                .username(properties.getUsername())
                .password(properties.getPassword())
                .build();
    }

    @Bean
    RealmResource meditrackRealm(Keycloak keycloak) {
        return keycloak.realm("meditrack");
    }

    @Configuration("postConstruct")
    public static class KeycloakPostConstruct {
        private final RealmResource meditrackRealm;
        private final UserService service;
        private final UserMapper mapper;

        public KeycloakPostConstruct(RealmResource meditrackRealm, UserService service, UserMapper mapper) {
            this.meditrackRealm = meditrackRealm;
            this.service = service;
            this.mapper = mapper;
        }

        /**
         * Creates a default admin user in keycloak and the database on startup, in case none exists.
         */
        @PostConstruct
        public void createAdminUser() {
            if (meditrackRealm.users().count() == 0) {
                log.info("Creating default admin user...");
                service.create(defaultAdminUser());
            }
        }

        private User defaultAdminUser() {
            UserDto user = new UserDto(
                    null,
                    "admin",
                    "admin",
                    "admin@meditrack.com",
                    "admin",
                    "admin",
                    List.of("admin"),
                    null,
                    1.0f,
                    0,
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
            return mapper.fromDto(user);
        }
    }
}
