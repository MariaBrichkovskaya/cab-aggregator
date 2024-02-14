package com.modsen.authservice.controller;

import com.modsen.authservice.dto.request.AuthRequest;
import com.modsen.authservice.dto.request.RegistrationRequest;
import com.modsen.authservice.service.KeycloakAdminClientService;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {
    private final KeycloakAdminClientService kcAdminClient;

    @PostMapping(value = "/registration")
    public ResponseEntity<Response> createUser(@RequestBody RegistrationRequest user) {
        Response createdResponse = kcAdminClient.createKeycloakUser(user);
        return ResponseEntity.ok(createdResponse);

    }

    @PostMapping("/login")
    public AccessTokenResponse login(@NotNull @RequestBody AuthRequest loginRequest) {
        return kcAdminClient.login(loginRequest);
    }

    @PostMapping("/refresh")
    public Response refresh() {
        return Response.ok().build();
    }
}
