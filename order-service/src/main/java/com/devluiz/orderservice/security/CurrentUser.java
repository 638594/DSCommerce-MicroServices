package com.devluiz.orderservice.security;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public record CurrentUser(Long id, boolean admin) {

    public static CurrentUser from(Jwt jwt) {
        Number userId = jwt.getClaim("userId");
        List<String> roles = jwt.getClaimAsStringList("roles");
        boolean admin = roles != null && roles.contains("ROLE_ADMIN");
        return new CurrentUser(userId.longValue(), admin);
    }
}
