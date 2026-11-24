package com.devluiz.userservice.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

/*
 * O PAR DE CHAVES RSA: o coração da autenticação entre microsserviços.
 *
 * - Chave PRIVADA: só o user-service tem. Serve para ASSINAR o token.
 * - Chave PÚBLICA: qualquer serviço pode ter. Serve só para CONFERIR a assinatura.
 *
 * ALTERADO na etapa 7b: a chave privada agora pode vir de um ARQUIVO (jwt.private-key-location).
 * - Com o arquivo: a chave é sempre a mesma, e os tokens continuam valendo depois de um reinício.
 * - Sem o arquivo (rodando no IntelliJ, por exemplo): gera um par novo a cada inicialização,
 *   como antes. É só para desenvolvimento: um aviso aparece no log.
 *
 * O arquivo NUNCA vai para o Git. Ele é montado no container pelo docker-compose.
 */
@Configuration
public class KeyConfig {

    private static final Logger log = LoggerFactory.getLogger(KeyConfig.class);

    @Bean
    public RSAKey rsaKey(@Value("${jwt.private-key-location:}") String privateKeyLocation,
                         ResourceLoader resourceLoader) throws GeneralSecurityException, IOException, JOSEException {

        RSAPrivateCrtKey privateKey;
        if (privateKeyLocation.isBlank()) {
            log.warn("jwt.private-key-location não definido: gerando um par de chaves temporário. "
                    + "Os tokens deixam de valer quando o serviço reiniciar.");
            privateKey = generatePrivateKey();
        } else {
            privateKey = loadPrivateKey(resourceLoader.getResource(privateKeyLocation));
        }

        // A chave privada RSA guarda o módulo e o expoente público:
        // com eles, a chave pública é recalculada, sem precisar de um segundo arquivo
        RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent()));

        return new RSAKey.Builder(publicKey)
                .privateKey((RSAPrivateKey) privateKey)
                // "kid" = key id, vai no cabeçalho do token. Calculado a partir da própria chave:
                // mesma chave = mesmo kid, a cada inicialização
                .keyIDFromThumbprint()
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

    /*
     * Lê um arquivo PEM no formato PKCS#8, o que o comando
     * "openssl genpkey -algorithm RSA" gera:
     *   -----BEGIN PRIVATE KEY-----
     *   (texto em Base64)
     *   -----END PRIVATE KEY-----
     */
    private RSAPrivateCrtKey loadPrivateKey(Resource resource) throws IOException, GeneralSecurityException {
        String pem = resource.getContentAsString(StandardCharsets.UTF_8);
        String base64 = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] der = Base64.getDecoder().decode(base64);
        return (RSAPrivateCrtKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(der));
    }

    private RSAPrivateCrtKey generatePrivateKey() throws GeneralSecurityException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return (RSAPrivateCrtKey) generator.generateKeyPair().getPrivate();
    }
}
