package com.modsen.authservice.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequest {
    String username;
    String password;
    String email;
}
