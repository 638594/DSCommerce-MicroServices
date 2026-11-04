package com.devluiz.paymentservice.services;

import com.devluiz.paymentservice.entities.Payment;
import com.devluiz.paymentservice.messaging.OrderCreatedEvent;
import com.devluiz.paymentservice.messaging.PaymentEventPublisher;
import com.devluiz.paymentservice.repositories.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;

    // O service não conhece o RabbitMQ: só avisa o publisher que algo aconteceu
    private final PaymentEventPublisher paymentEventPublisher;

    private final long simulatedDelaySeconds;

    // O @Value também funciona em parâmetro de construtor
    public PaymentService(PaymentRepository paymentRepository,
                          PaymentEventPublisher paymentEventPublisher,
                          @Value("${payment.simulated-delay-seconds}") long simulatedDelaySeconds) {
        this.paymentRepository = paymentRepository;
        this.paymentEventPublisher = paymentEventPublisher;
        this.simulatedDelaySeconds = simulatedDelaySeconds;
    }

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
        paymentEventPublisher.publishPaymentConfirmed(payment);
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