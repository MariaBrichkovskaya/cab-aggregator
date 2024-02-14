package com.modsen.authservice.service;


import com.modsen.authservice.config.KeycloakProvider;
import com.modsen.authservice.dto.request.AuthRequest;
import com.modsen.authservice.dto.request.RegistrationRequest;
import com.modsen.authservice.exception.NotFoundException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static com.modsen.authservice.utils.AuthUtils.*;

@Service
@Slf4j
public class KeycloakAdminClientService {
    @Value("${keycloak.realm}")
    public String realm;

    private final KeycloakProvider kcProvider;


    public KeycloakAdminClientService(KeycloakProvider keycloakProvider) {
        this.kcProvider = keycloakProvider;
    }

    public AccessTokenResponse login(AuthRequest loginRequest) {
        Keycloak keycloak = kcProvider.newKeycloakBuilderWithPasswordCredentials(loginRequest.getUsername(), loginRequest.getPassword()).build();

        AccessTokenResponse accessTokenResponse;
        try {
            accessTokenResponse = keycloak.tokenManager().getAccessToken();
            log.info(SUCCESSFUL_LOGIN_MESSAGE);
            return accessTokenResponse;
        } catch (NotAuthorizedException exception) {
            log.error(NOT_FOUND_MESSAGE);
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
    }

    public Response createKeycloakUser(RegistrationRequest user) {
        UsersResource usersResource = kcProvider.getInstance().realm(realm).users();
        System.err.println(usersResource.list());
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());

        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(user.getUsername());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setEmail(user.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);
        System.err.println(usersResource.create(kcUser).getStatus());
        return usersResource.create(kcUser);

    }

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }


}

