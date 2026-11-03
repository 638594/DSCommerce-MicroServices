package com.devluiz.paymentservice.services;

import com.devluiz.paymentservice.entities.Payment;
import com.devluiz.paymentservice.messaging.OrderCreatedEvent;
import com.devluiz.paymentservice.messaging.PaymentConfirmedEvent;
import com.devluiz.paymentservice.messaging.RabbitConfig;
import com.devluiz.paymentservice.repositories.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${payment.simulated-delay-seconds}")
    private long simulatedDelaySeconds;

    @Transactional
    public void process(OrderCreatedEvent event) {

        // Mensagem repetida? Então esse pedido já foi pago: ignora.
        if (paymentRepository.existsByOrderId(event.orderId())) {
            log.warn("Pedido {} já tem pagamento. Mensagem ignorada.", event.orderId());
            return;
        }

        // Aqui, num sistema real, entraria a chamada ao gateway de pagamento
        // (cartão, Pix...). Vamos só fingir que demora e sempre aprova.
        simulateProcessing();

        Payment payment = paymentRepository.save(new Payment(event.orderId(), event.total(), Instant.now()));
        log.info("Pagamento {} aprovado para o pedido {}", payment.getId(), event.orderId());

        // Avisa quem estiver interessado (hoje, o order-service) que o pagamento saiu
        PaymentConfirmedEvent confirmed = new PaymentConfirmedEvent(event.orderId(), payment.getId(), payment.getMoment());
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.PAYMENT_CONFIRMED_KEY, confirmed);
        log.info("Mensagem publicada: pagamento confirmado para o pedido {}", event.orderId());
    }

    @Transactional(readOnly = true)
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    private void simulateProcessing() {
        try {
            Thread.sleep(simulatedDelaySeconds * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
