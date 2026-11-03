package com.devluiz.paymentservice.messaging;

import java.math.BigDecimal;

/*
 * Cópia do OrderCreatedEvent do order-service.
 * O pacote é diferente, mas os NOMES DOS CAMPOS são iguais,
 * e é isso que importa: a mensagem viaja como JSON { "orderId": ..., "total": ... }.
 */
public record OrderCreatedEvent(Long orderId, BigDecimal total) {
}
