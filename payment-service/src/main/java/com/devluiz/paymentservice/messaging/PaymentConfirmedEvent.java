package com.devluiz.paymentservice.messaging;

import java.time.Instant;

/* A mensagem que o payment-service PUBLICA quando o pagamento é aprovado. */
public record PaymentConfirmedEvent(Long orderId, Long paymentId, Instant moment) {
}
