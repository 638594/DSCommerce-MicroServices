package com.devluiz.paymentservice.messaging;

import com.devluiz.paymentservice.services.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/*
 * Fica "escutando" a fila payment-service.order-created.
 * Para cada pedido criado no order-service, o Spring chama este método.
 *
 * Se o payment-service estiver DESLIGADO quando o pedido for criado,
 * a mensagem espera na fila. Quando ele ligar, processa tudo o que acumulou.
 */
@Component
public class OrderCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

    private final PaymentService paymentService;

    public OrderCreatedListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RabbitListener(queues = RabbitConfig.ORDER_CREATED_QUEUE)
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Mensagem recebida: pedido {} criado, total {}", event.orderId(), event.total());
        paymentService.process(event);
    }
}
