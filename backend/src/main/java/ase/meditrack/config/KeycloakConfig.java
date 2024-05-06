package ase.meditrack.config;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class KeycloakConfig {

    private final KeycloakProperties properties;

    public KeycloakConfig(KeycloakProperties properties) {
        this.properties = properties;
    }

    @Bean
    Keycloak keycloak() {
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

}
