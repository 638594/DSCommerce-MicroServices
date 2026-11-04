package com.devluiz.paymentservice.messaging;

import com.devluiz.paymentservice.entities.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
 * Único lugar do payment-service que sabe PUBLICAR no RabbitMQ.
 * O PaymentService só diz "o pagamento foi aprovado"; esta classe decide
 * como isso vira mensagem (formato, exchange, routing key).
 */
@Component
public class PaymentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public PaymentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPaymentConfirmed(Payment payment) {
        PaymentConfirmedEvent event = new PaymentConfirmedEvent(payment.getOrderId(), payment.getId(), payment.getMoment());
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.PAYMENT_CONFIRMED_KEY, event);
        log.info("Mensagem publicada: pagamento confirmado para o pedido {}", payment.getOrderId());
    }
}