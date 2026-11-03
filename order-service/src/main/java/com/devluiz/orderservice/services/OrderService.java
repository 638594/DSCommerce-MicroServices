package com.devluiz.orderservice.services;

import com.devluiz.orderservice.clients.ProductClient;
import com.devluiz.orderservice.clients.ProductResponse;
import com.devluiz.orderservice.dto.OrderDTO;
import com.devluiz.orderservice.dto.OrderRequest;
import com.devluiz.orderservice.entities.Order;
import com.devluiz.orderservice.entities.OrderItem;
import com.devluiz.orderservice.entities.OrderStatus;
import com.devluiz.orderservice.messaging.OrderCreatedEvent;
import com.devluiz.orderservice.messaging.RabbitConfig;
import com.devluiz.orderservice.repositories.OrderRepository;
import com.devluiz.orderservice.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductClient productClient;

    // NOVO na etapa 3: o "carteiro" do Spring para mandar mensagens ao RabbitMQ
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional(readOnly = true)
    public OrderDTO findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: " + id));
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO insert(OrderRequest request) {
        Order order = new Order();
        order.setMoment(Instant.now());
        order.setStatus(OrderStatus.WAITING_PAYMENT);

        for (OrderRequest.Item itemRequest : request.items()) {
            // Chamada HTTP (síncrona): precisamos do preço AGORA para montar o pedido
            ProductResponse product = productClient.findById(itemRequest.productId());
            OrderItem item = new OrderItem(product.id(), product.name(), product.price(), itemRequest.quantity());
            order.addItem(item);
        }

        order = orderRepository.save(order);

        // Mensagem (assíncrona): só AVISA que o pedido foi criado e segue em frente.
        // Ninguém fica esperando o pagamento acontecer.
        OrderCreatedEvent event = new OrderCreatedEvent(order.getId(), order.getTotal());
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ORDER_CREATED_KEY, event);
        log.info("Mensagem publicada: pedido {} criado, total {}", order.getId(), order.getTotal());

        return new OrderDTO(order);
    }

    // Chamado pelo PaymentConfirmedListener quando chega a mensagem do payment-service
    @Transactional
    public void markAsPaid(Long orderId, Instant paymentMoment) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: " + orderId));
        order.setStatus(OrderStatus.PAID);
        order.setPaymentMoment(paymentMoment);
        orderRepository.save(order);
        log.info("Pedido {} marcado como PAID", orderId);
    }
}
