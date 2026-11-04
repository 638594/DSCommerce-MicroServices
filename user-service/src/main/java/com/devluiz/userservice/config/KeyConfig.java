package com.devluiz.userservice.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/*
 * O PAR DE CHAVES RSA: o coração da autenticação entre microsserviços.
 *
 * - Chave PRIVADA: só o user-service tem. Serve para ASSINAR o token.
 * - Chave PÚBLICA: qualquer serviço pode ter. Serve só para CONFERIR a assinatura.
 *
 * Com a pública dá para conferir, mas NÃO dá para gerar token.
 * Por isso os outros serviços podem validar tokens sem nunca terem o poder de criar um.
 * (No monolito era HS256: uma senha única que servia para as duas coisas.)
 *
 * Simplificação desta etapa: o par é gerado toda vez que o serviço sobe.
 * Consequência: reiniciar o user-service invalida os tokens antigos (é só fazer login de novo).
 * Em produção, a chave privada viria de um cofre de segredos ou variável de ambiente.
 */
@Configuration
public class KeyConfig {

    @Bean
    public RSAKey rsaKey() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();

        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                // "kid" = key id. Vai no cabeçalho do token e diz qual chave assinou
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    // Gera (assina) tokens com a chave privada. Usado pelo TokenService.
    @Bean
    public JwtEncoder jwtEncoder(RSAKey rsaKey) {
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));
        return new NimbusJwtEncoder(jwkSource);
    }

    // Confere tokens com a chave pública. Usado aqui no GET /users/me.
    @Bean
    public JwtDecoder jwtDecoder(RSAKey rsaKey) throws JOSEException {
        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
    }
}
