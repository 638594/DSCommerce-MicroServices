package com.devluiz.userservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) throws Exception {

        http
                // API REST com token: não usa sessão nem cookie, então CSRF não se aplica
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Qualquer um pode tentar fazer login
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        // A chave PÚBLICA é pública mesmo: os outros serviços vão buscá-la aqui
                        .requestMatchers(HttpMethod.GET, "/.well-known/jwks.json").permitAll()
                        .requestMatchers("/error").permitAll()
                        // Todo o resto exige token
                        .anyRequest().authenticated()
                )
                // NOVO na etapa 6b: em vez de responder sozinho, o Spring Security
                // repassa o 401 e o 403 para o ControllerExceptionHandler
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> resolver.resolveException(req, res, null, e))
                        .accessDeniedHandler((req, res, e) -> resolver.resolveException(req, res, null, e)))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint((req, res, e) -> resolver.resolveException(req, res, null, e))
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    // Senhas são comparadas com BCrypt (o mesmo do monolito, por isso os hashes do import.sql funcionam)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Quem confere email + senha no login: busca o usuário (UserDetailsService) e compara a senha (PasswordEncoder)
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    // Ensina o Spring a ler os papéis do token: eles estão no campo "roles", já com o prefixo ROLE_
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("roles");
        authorities.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }
}
