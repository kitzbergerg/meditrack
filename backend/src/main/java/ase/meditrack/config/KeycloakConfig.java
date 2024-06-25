package ase.meditrack.config;

import ase.meditrack.model.dto.UserDto;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.mapper.UserMapper;
import ase.meditrack.repository.RoleRepository;
import ase.meditrack.repository.TeamRepository;
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
        private final UserService userService;
        private final TeamRepository teamRepository;
        private final RoleRepository roleRepository;
        private final UserMapper mapper;

        public KeycloakPostConstruct(RealmResource meditrackRealm, UserService userService,
                                     TeamRepository teamRepository,
                                     RoleRepository roleRepository, UserMapper mapper) {
            this.meditrackRealm = meditrackRealm;
            this.userService = userService;
            this.teamRepository = teamRepository;
            this.roleRepository = roleRepository;
            this.mapper = mapper;
        }

        /**
         * Creates a default admin user in keycloak and the database on startup, in case none exists.
         */
        @PostConstruct
        public void createAdminUser() {
            if (meditrackRealm.users().count() == 0) {
                log.info("Creating default admin user...");

                Team team = new Team();
                team.setName("admin-team");
                team.setNighttimeRequiredPeople(0);
                team.setDaytimeRequiredPeople(0);
                team = teamRepository.save(team);

                Role role = new Role();
                role.setName("admin-role");
                role.setTeam(team);
                role.setAllowedFlextimeTotal(0);
                role.setAllowedFlextimePerMonth(0);
                role.setDaytimeRequiredPeople(0);
                role.setNighttimeRequiredPeople(0);
                role.setWorkingHours(40);
                role.setMaxWeeklyHours(80);
                role.setMaxConsecutiveShifts(0);
                role = roleRepository.save(role);

                User user = defaultAdminUser();
                user.setTeam(team);
                user.setRole(role);
                userService.create(user, false);
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
