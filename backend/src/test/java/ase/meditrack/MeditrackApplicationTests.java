package ase.meditrack;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.util.Map;

@SpringBootTest
class MeditrackApplicationIT {

    private final static PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>("postgres:16-alpine");

    private final static Network NETWORK = Network.newNetwork();
    private final static FixedHostPortGenericContainer<?> KEYCLOAK_CONTAINER = new FixedHostPortGenericContainer<>("meditrack-keycloak:latest")
            .withEnv(Map.of(
                    "KEYCLOAK_ADMIN", "admin",
                    "KEYCLOAK_ADMIN_PASSWORD", "admin"
            ))
            .withFixedExposedPort(8080, 8080)
            .withNetworkAliases("keycloak")
            .withNetwork(NETWORK);
    private final static GenericContainer<?> KEYCLOAK_CONFIG_CONTAINER = new GenericContainer<>(DockerImageName.parse("adorsys/keycloak-config-cli:latest-24.0.1"))
            .withEnv(Map.of(
                    "KEYCLOAK_URL", "http://keycloak:8080/",
                    "KEYCLOAK_USER", "admin",
                    "KEYCLOAK_PASSWORD", "admin",
                    "KEYCLOAK_AVAILABILITYCHECK_ENABLED", "true",
                    "KEYCLOAK_AVAILABILITYCHECK_TIMEOUT", "120s",
                    "IMPORT_FILES_LOCATIONS", "/config/meditrack.json"
            ))
            .withCopyFileToContainer(MountableFile.forHostPath("C:/Dev/Uni/24ss-ase-pr-qse-03/keycloak/config"), "/config")
            .withNetwork(NETWORK);

    @DynamicPropertySource
    static void startContainers(DynamicPropertyRegistry registry) throws InterruptedException {
        POSTGRE_SQL_CONTAINER.start();
        registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);

        KEYCLOAK_CONTAINER.start();
        KEYCLOAK_CONFIG_CONTAINER.start();

        // wait for keycloak config to be loaded
        while (KEYCLOAK_CONFIG_CONTAINER.isRunning()) {
            Thread.sleep(500);
        }
    }

    @Test
    void contextLoads() {
    }

}
