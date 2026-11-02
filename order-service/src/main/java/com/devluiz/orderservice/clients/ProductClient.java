package com.devluiz.orderservice.clients;

import com.devluiz.orderservice.services.exceptions.InvalidOrderException;
import com.devluiz.orderservice.services.exceptions.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/*
 * Esta classe é o "Postman" do order-service: ela faz a chamada HTTP
 * GET http://localhost:8081/products/{id} e transforma o JSON em ProductResponse.
 *
 * O resto do order-service não sabe que existe HTTP no meio do caminho:
 * ele só chama productClient.findById(id).
 */
@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(@Value("${product-service.url}") String productServiceUrl) {

        // Tempo máximo de espera. Se o product-service estiver travado,
        // o order-service desiste em 3 segundos em vez de travar junto.
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));

        this.restClient = RestClient.builder()
                .baseUrl(productServiceUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public ProductResponse findById(Long productId) {
        try {
            return restClient.get()
                    .uri("/products/{id}", productId)
                    .retrieve()
                    .body(ProductResponse.class);
        }
        catch (HttpClientErrorException.NotFound e) {
            // O product-service respondeu 404: o produto não existe
            throw new InvalidOrderException("Produto não existe: " + productId);
        }
        catch (ResourceAccessException e) {
            // Nem conseguiu conversar: product-service desligado, travado ou endereço errado
            throw new ServiceUnavailableException("Catálogo de produtos indisponível. Tente novamente em instantes.");
        }
    }
}
