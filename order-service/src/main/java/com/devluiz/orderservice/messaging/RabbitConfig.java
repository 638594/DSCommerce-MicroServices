package com.devluiz.orderservice.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Como o RabbitMQ funciona, em 3 peças:
 *
 *  EXCHANGE  = a "agência dos correios". Quem publica entrega a mensagem aqui,
 *              com uma etiqueta (a routing key), ex.: "order.created".
 *  QUEUE     = a "caixa postal" de UM serviço. As mensagens ficam guardadas
 *              nela até o serviço ler, mesmo que ele esteja desligado.
 *  BINDING   = a regra "mensagens com a etiqueta X vão para a fila Y".
 *
 * Regra que vamos seguir: quem PUBLICA só conhece a exchange.
 * Quem CONSOME cria a própria fila e o próprio binding.
 */
@Configuration
public class RabbitConfig {

    // Os nomes ficam em constantes para não ter erro de digitação espalhado pelo código
    public static final String EXCHANGE = "dscommerce.events";
    public static final String ORDER_CREATED_KEY = "order.created";
    public static final String PAYMENT_CONFIRMED_KEY = "payment.confirmed";
    public static final String PAYMENT_CONFIRMED_QUEUE = "order-service.payment-confirmed";

    // A exchange compartilhada por todos os serviços. Declarar de novo não duplica:
    // se ela já existir no RabbitMQ, o Spring só confere.
    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EXCHANGE);
    }

    // A caixa postal do order-service, para receber "pagamento confirmado".
    // durable = true: a fila (e as mensagens nela) sobrevivem se o RabbitMQ reiniciar.
    @Bean
    public Queue paymentConfirmedQueue() {
        return new Queue(PAYMENT_CONFIRMED_QUEUE, true);
    }

    @Bean
    public Binding paymentConfirmedBinding(Queue paymentConfirmedQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(paymentConfirmedQueue).to(eventsExchange).with(PAYMENT_CONFIRMED_KEY);
    }

    // Faz as mensagens irem e voltarem como JSON (em vez do formato binário do Java).
    // O Spring Boot usa este bean automaticamente para enviar e para receber.
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
