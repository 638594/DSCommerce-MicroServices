package com.devluiz.orderservice.messaging;

import com.devluiz.orderservice.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/*
 * Um @RabbitListener é parecido com um endpoint do controller,
 * só que em vez de esperar uma requisição HTTP, ele espera uma mensagem chegar na fila.
 * O Spring chama este método sozinho, uma vez para cada mensagem.
 */
@Component
public class PaymentConfirmedListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentConfirmedListener.class);

    private final OrderService orderService;

    public PaymentConfirmedListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = RabbitConfig.PAYMENT_CONFIRMED_QUEUE)
    public void onPaymentConfirmed(PaymentConfirmedEvent event) {
        log.info("Mensagem recebida: pagamento confirmado para o pedido {}", event.orderId());
        orderService.markAsPaid(event.orderId(), event.moment());
    }
}
