package com.modsen.rideservice.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.modsen.rideservice.util.SecurityUtil.*;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    private final JwtAuthConverterProperties properties;

    public JwtAuthConverter(JwtAuthConverterProperties properties) {
        this.properties = properties;
    }

    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()).collect(Collectors.toSet());
        User user = extractUserInfo(jwt);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        return authToken;
    }

    private User extractUserInfo(Jwt jwt) {
        String email = jwt.getClaim(EMAIL_KEY);
        String phone = jwt.getClaim(PHONE_KEY);
        UUID id = UUID.fromString(jwt.getClaim(UUID_KEY));
        String username = jwt.getClaim(USERNAME_KEY_TOKEN);
        String name = jwt.getClaim(NAME_KEY_TOKEN);
        String surname = jwt.getClaim(SURNAME_KEY_TOKEN);
        return User.builder()
                .phone(phone)
                .surname(surname)
                .name(name)
                .id(id)
                .email(email)
                .username(username)
                .build();
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim(RESOURCE_ACCESS);
        Map<String, Object> resource;
        Collection<String> resourceRoles;
        if (resourceAccess == null
                || (resource = (Map<String, Object>) resourceAccess.get(properties.getResourceId())) == null
                || (resourceRoles = (Collection<String>) resource.get(RESOURCE)) == null) {
            return Set.of();
        }
        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                .collect(Collectors.toSet());
    }
}
