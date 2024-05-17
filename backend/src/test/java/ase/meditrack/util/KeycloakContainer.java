package ase.meditrack.util;

import jakarta.ws.rs.core.Response;
import lombok.SneakyThrows;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.ClientRepresentation;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.InternetProtocol;
import org.testcontainers.containers.Network;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Paths;
import java.util.Map;

import static jakarta.ws.rs.core.Response.Status.Family.SUCCESSFUL;

public class KeycloakContainer<SELF extends KeycloakContainer<SELF>> extends GenericContainer<SELF> {

    private final Network network = Network.newNetwork();
    private final GenericContainer<?> keycloakConfigContainer = new GenericContainer<>(DockerImageName.parse("adorsys/keycloak-config-cli:latest-24.0.1"))
            .withEnv(Map.of(
                    "KEYCLOAK_URL", "http://keycloak:8080/",
                    "KEYCLOAK_USER", "admin",
                    "KEYCLOAK_PASSWORD", "admin",
                    "KEYCLOAK_AVAILABILITYCHECK_ENABLED", "true",
                    "KEYCLOAK_AVAILABILITYCHECK_TIMEOUT", "120s",
                    "IMPORT_FILES_LOCATIONS", "/config/meditrack.json"
            ))
            .withCopyFileToContainer(MountableFile.forHostPath("../keycloak/config"), "/config")
            .withNetwork(network);

    public KeycloakContainer() {
        super(new ImageFromDockerfile().withFileFromPath(".", Paths.get("../keycloak")));
        this.withNetwork(network);
        this.withNetworkAliases("keycloak");
        this.withEnv(Map.of(
                "KEYCLOAK_ADMIN", "admin",
                "KEYCLOAK_ADMIN_PASSWORD", "admin"
        ));
        this.withFixedExposedPort(8080, 8080);
    }

    public SELF withFixedExposedPort(int hostPort, int containerPort) {
        return this.withFixedExposedPort(hostPort, containerPort, InternetProtocol.TCP);
    }

    public SELF withFixedExposedPort(int hostPort, int containerPort, InternetProtocol protocol) {
        super.addFixedExposedPort(hostPort, containerPort, protocol);
        return this.self();
    }

    @SneakyThrows
    @Override
    public void start() {
        super.start();
        keycloakConfigContainer.start();

        // wait for keycloak config to be loaded
        while (keycloakConfigContainer.isRunning()) {
            Thread.sleep(500);
        }

        createTestClient();
    }

    private void createTestClient() {
        try (Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl("http://localhost:8080")
                .realm("master")
                .clientId("admin-cli")
                .grantType(OAuth2Constants.PASSWORD)
                .username("admin")
                .password("admin")
                .build()) {

            ClientRepresentation clientRepresentation = new ClientRepresentation();
            clientRepresentation.setClientId("testclient");
            clientRepresentation.setPublicClient(true);
            clientRepresentation.setDirectAccessGrantsEnabled(true);

            try (Response response = keycloak.realm("meditrack").clients().create(clientRepresentation)) {
                if (response.getStatusInfo().toEnum().getFamily() != SUCCESSFUL) {
                    throw new RuntimeException("error creating testclient");
                }
            }
        }
    }
}
