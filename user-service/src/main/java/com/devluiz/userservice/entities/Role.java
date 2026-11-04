package com.devluiz.userservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/*
 * Diferença em relação ao monolito: Role NÃO implementa GrantedAuthority.
 * A entidade é só dado do negócio. Quem traduz isso para o Spring Security
 * é o AuthUserDetailsService.
 */
@Entity
@Table(name = "tb_role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ex.: "ROLE_CLIENT", "ROLE_ADMIN"
    private String authority;

    public Role() {
    }

    public Long getId() {
        return id;
    }

    public String getAuthority() {
        return authority;
    }
}
