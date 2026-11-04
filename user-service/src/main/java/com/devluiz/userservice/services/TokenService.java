package com.devluiz.userservice.services;

import com.devluiz.userservice.entities.Role;
import com.devluiz.userservice.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/*
 * Monta e assina o token JWT.
 *
 * Um JWT tem 3 partes separadas por ponto: cabeçalho.conteúdo.assinatura
 * - cabeçalho: qual algoritmo assinou (RS256) e qual chave (kid)
 * - conteúdo ("claims"): os dados abaixo. NÃO é criptografado: qualquer um consegue ler.
 *   Por isso nunca coloque senha ou dado sensível aqui.
 * - assinatura: garante que ninguém alterou o conteúdo. Se mudar uma letra, a assinatura não bate.
 */
@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final long expirationSeconds;

    public TokenService(JwtEncoder jwtEncoder,
                        @Value("${jwt.expiration-seconds}") long expirationSeconds) {
        this.jwtEncoder = jwtEncoder;
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("user-service")                       // quem emitiu
                .subject(user.getEmail())                     // de quem é o token
                .issuedAt(now)                                // quando foi emitido
                .expiresAt(now.plusSeconds(expirationSeconds))// quando expira
                // Os outros serviços vão usar o userId para saber QUEM fez a requisição,
                // sem precisar consultar o banco de usuários
                .claim("userId", user.getId())
                .claim("roles", roles(user))
                .build();

        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    private List<String> roles(User user) {
        return user.getRoles().stream().map(Role::getAuthority).sorted().toList();
    }
}
