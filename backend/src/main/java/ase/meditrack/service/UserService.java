package ase.meditrack.service;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static jakarta.ws.rs.core.Response.Status.Family.SUCCESSFUL;

@Service
@Slf4j
public class UserService {
    private final RealmResource meditrackRealm;

    public UserService(RealmResource meditrackRealm) {
        this.meditrackRealm = meditrackRealm;
    }

    @PostConstruct
    private void createAdminUser() {
        if (meditrackRealm.users().count() == 0) {
            log.info("Creating default admin user...");
            this.create(adminUserRepresentation());
        }
    }

    public List<UserRepresentation> findAll() {
        return meditrackRealm.users().list();
    }

    public UserRepresentation findById(UUID id) {
        return meditrackRealm.users().get(String.valueOf(id)).toRepresentation();
    }

    public UserRepresentation create(UserRepresentation user) {
        try (Response response = meditrackRealm.users().create(user)) {
            if (response.getStatusInfo().toEnum().getFamily() != SUCCESSFUL) {
                log.error("Error creating user: {}", response.getStatusInfo().getReasonPhrase());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            String id = CreatedResponseUtil.getCreatedId(response);
            setUserRoles(meditrackRealm, id, user.getRealmRoles());
            return meditrackRealm.users().get(id).toRepresentation();
        }
    }

    public UserRepresentation update(UserRepresentation user) {
        meditrackRealm.users().get(user.getId()).update(user);
        setUserRoles(meditrackRealm, user.getId(), user.getRealmRoles());
        return meditrackRealm.users().get(user.getId()).toRepresentation();
    }

    public void delete(UUID id) {
        try (Response response = meditrackRealm.users().delete(String.valueOf(id))) {
            if (response.getStatusInfo().toEnum().getFamily() != SUCCESSFUL) {
                log.error("Error deleting user: {}", response.getStatusInfo().getReasonPhrase());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    private static void setUserRoles(RealmResource meditrackRealm, String userId, List<String> roles) {
        if (roles == null) return;
        // for some reason keycloak doesn't use the roles in UserRepresentation, so we need to set them explicitly
        List<RoleRepresentation> userRoles = roles.stream().map(role -> meditrackRealm.roles().get(role).toRepresentation()).toList();
        UserResource user = meditrackRealm.users().get(userId);
        RoleScopeResource roleScopeResource = user.roles().realmLevel();
        roleScopeResource.remove(roleScopeResource.listAll());
        user.roles().realmLevel().add(userRoles);
    }

    private static UserRepresentation adminUserRepresentation() {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername("admin");
        userRepresentation.setEnabled(true);
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue("admin");
        credentialRepresentation.setTemporary(true);
        userRepresentation.setCredentials(List.of(credentialRepresentation));
        userRepresentation.setRealmRoles(List.of("admin"));
        return userRepresentation;
    }
}
