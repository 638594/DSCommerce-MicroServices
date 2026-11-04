package com.devluiz.userservice.dto;

import com.devluiz.userservice.entities.Role;
import com.devluiz.userservice.entities.User;

import java.util.List;

/* Dados do usuário que a API devolve. A senha NUNCA sai. */
public record UserDTO(Long id, String name, String email, List<String> roles) {

    public UserDTO(User entity) {
        this(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getRoles().stream().map(Role::getAuthority).sorted().toList()
        );
    }
}
