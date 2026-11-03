package com.devluiz.paymentservice.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Mesma ideia do RabbitConfig do order-service, ao contrário:
 * - o payment-service RECEBE "order.created" (então cria a própria fila para isso)
 * - e PUBLICA "payment.confirmed" (então só precisa conhecer a exchange)
 */
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "dscommerce.events";
    public static final String ORDER_CREATED_KEY = "order.created";
    public static final String PAYMENT_CONFIRMED_KEY = "payment.confirmed";
    public static final String ORDER_CREATED_QUEUE = "payment-service.order-created";

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(ORDER_CREATED_QUEUE, true);
    }

    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(eventsExchange).with(ORDER_CREATED_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
