package com.devluiz.userservice.services;

import com.devluiz.userservice.entities.Role;
import com.devluiz.userservice.entities.User;
import com.devluiz.userservice.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * A "ponte" entre a nossa entidade User e o Spring Security.
 *
 * No login, o Spring chama loadUserByUsername(email). Aqui buscamos o usuário
 * no banco e devolvemos no formato que o Spring entende (UserDetails).
 * Assim a entidade User não precisa implementar nada do Spring Security.
 */
@Service
public class AuthUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AuthUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                // authorities(...) usa os nomes como estão ("ROLE_ADMIN"), sem acrescentar prefixo
                .authorities(user.getRoles().stream().map(Role::getAuthority).toArray(String[]::new))
                .build();
    }
}
