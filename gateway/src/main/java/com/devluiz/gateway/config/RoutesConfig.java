package com.devluiz.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

/*
 * A tabela de rotas do gateway: "requisições com este caminho vão para aquele serviço".
 *
 * Cada rota tem 3 partes:
 *  - path(...)     -> QUAIS requisições ela pega (qualquer método: GET, POST, PUT...)
 *  - http()        -> O QUE fazer com elas: repassar por HTTP, sem mudar nada
 *  - before(uri()) -> PARA ONDE repassar
 *
 * O caminho é repassado igual: GET localhost:8080/products/1 vira GET localhost:8081/products/1.
 * Os headers também vão junto, inclusive o "Authorization: Bearer <token>".
 *
 * O gateway NÃO confere o token. Quem confere é cada serviço, como na etapa 4b.
 */
@Configuration
public class RoutesConfig {

    @Bean
    public RouterFunction<ServerResponse> userRoutes(@Value("${services.user.url}") String userUrl) {
        return route("user-service")
                // Login e dados do usuário moram no mesmo serviço: uma rota com dois caminhos
                .route(path("/auth/**").or(path("/users/**")), http())
                .before(uri(userUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productRoutes(@Value("${services.product.url}") String productUrl) {
        return route("product-service")
                .route(path("/products/**"), http())
                .before(uri(productUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderRoutes(@Value("${services.order.url}") String orderUrl) {
        return route("order-service")
                .route(path("/orders/**"), http())
                .before(uri(orderUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> paymentRoutes(@Value("${services.payment.url}") String paymentUrl) {
        return route("payment-service")
                .route(path("/payments/**"), http())
                .before(uri(paymentUrl))
                .build();
    }
}
