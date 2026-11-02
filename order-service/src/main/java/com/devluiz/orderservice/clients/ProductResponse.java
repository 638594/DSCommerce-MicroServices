package com.devluiz.orderservice.clients;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/*
 * Os campos do JSON do product-service que o order-service USA.
 *
 * Repare: NÃO é a entidade Product. O order-service não tem (nem deve ter)
 * a classe Product. Ele só conhece o "formato da resposta" do outro serviço.
 *
 * ignoreUnknown = true: o product-service manda também description, imgUrl,
 * categories... Esses campos são ignorados. Assim, se o product-service ganhar
 * campos novos no futuro, o order-service não quebra.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductResponse(Long id, String name, BigDecimal price) {
}
