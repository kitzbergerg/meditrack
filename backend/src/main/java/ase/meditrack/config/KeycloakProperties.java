package ase.meditrack.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("meditrack.keycloak")
@Data
public class KeycloakProperties {
    private String serverUrl;
    private String username;
    private String password;
}
