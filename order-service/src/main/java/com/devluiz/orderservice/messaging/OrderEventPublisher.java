package com.devluiz.orderservice.messaging;

import com.devluiz.orderservice.entities.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
 * Único lugar do order-service que sabe PUBLICAR no RabbitMQ.
 * O OrderService só diz "o pedido foi criado"; esta classe decide
 * como isso vira mensagem (formato, exchange, routing key).
 */
@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(order.getId(), order.getTotal());
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ORDER_CREATED_KEY, event);
        log.info("Mensagem publicada: pedido {} criado, total {}", order.getId(), order.getTotal());
    }
}