package com.modsen.authservice.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthRequest {
    String username;
    String password;
}
