package com.medico.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.UUID;

public record UserPrincipal(
    UUID id,
    String username,
    String email,
    String firstName,
    String lastName,
    Collection<? extends GrantedAuthority> authorities
) {
    public static UserPrincipal fromJwt(Jwt jwt, Collection<? extends GrantedAuthority> authorities) {
        return new UserPrincipal(
            UUID.fromString(jwt.getSubject()),
            jwt.getClaimAsString("preferred_username"),
            jwt.getClaimAsString("email"),
            jwt.getClaimAsString("given_name"),
            jwt.getClaimAsString("family_name"),
            authorities
        );
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean hasRole(String role) {
        return authorities.stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role.toUpperCase()));
    }
}

