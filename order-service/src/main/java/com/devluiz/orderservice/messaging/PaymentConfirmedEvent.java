package com.devluiz.orderservice.messaging;

import java.time.Instant;

/* A mensagem que o order-service RECEBE do payment-service. */
public record PaymentConfirmedEvent(Long orderId, Long paymentId, Instant moment) {
}
