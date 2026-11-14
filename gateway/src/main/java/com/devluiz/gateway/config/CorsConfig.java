package com.devluiz.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * CORS: a regra do NAVEGADOR que impede um site (ex.: seu front em localhost:5173)
 * de chamar uma API em outro endereço, a não ser que a API diga "pode".
 *
 * O Postman não é navegador, então não é afetado. Isso só importa quando existir um front-end.
 *
 * Como o front-end só fala com o gateway, a liberação fica SÓ aqui,
 * e não espalhada pelos quatro serviços.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] allowedOrigins;

    public CorsConfig(@Value("${cors.allowed-origins}") String[] allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type");
    }
}
