package com.devluiz.userservice.controllers;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/*
 * Publica a CHAVE PÚBLICA no formato JWKS (JSON Web Key Set).
 *
 * Na etapa 4b, o product-service e o order-service vão buscar a chave aqui
 * para conferir os tokens. "/.well-known/jwks.json" é o endereço padrão de mercado
 * para isso: bibliotecas de outros sistemas já sabem procurar nele.
 *
 * toPublicJWK() remove a parte privada. A chave privada NUNCA sai deste serviço.
 */
@RestController
public class JwksController {

    private final RSAKey rsaKey;

    public JwksController(RSAKey rsaKey) {
        this.rsaKey = rsaKey;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> keys() {
        return new JWKSet(rsaKey.toPublicJWK()).toJSONObject();
    }
}
