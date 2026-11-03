package com.devluiz.orderservice.messaging;

import java.math.BigDecimal;

/*
 * A mensagem que o order-service PUBLICA quando um pedido é criado.
 * Vai pelo RabbitMQ como JSON: { "orderId": 5, "total": 1431.0 }
 *
 * O payment-service tem uma cópia deste record com os MESMOS nomes de campo.
 * Cada serviço tem a sua cópia de propósito: nenhum depende do código do outro,
 * só do formato do JSON.
 */
public record OrderCreatedEvent(Long orderId, BigDecimal total) {
}
