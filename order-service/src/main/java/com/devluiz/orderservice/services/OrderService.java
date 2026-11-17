package com.devluiz.orderservice.services;

import com.devluiz.orderservice.clients.ProductClient;
import com.devluiz.orderservice.clients.ProductResponse;
import com.devluiz.orderservice.dto.OrderDTO;
import com.devluiz.orderservice.dto.OrderRequest;
import com.devluiz.orderservice.entities.Order;
import com.devluiz.orderservice.entities.OrderItem;
import com.devluiz.orderservice.entities.OrderStatus;
import com.devluiz.orderservice.messaging.OrderEventPublisher;
import com.devluiz.orderservice.repositories.OrderRepository;
import com.devluiz.orderservice.security.CurrentUser;
import com.devluiz.orderservice.services.exceptions.ForbiddenException;
import com.devluiz.orderservice.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.time.Instant;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    // O service não conhece o RabbitMQ: só avisa o publisher que algo aconteceu
    private final OrderEventPublisher orderEventPublisher;

    public OrderService(OrderRepository orderRepository,
                        ProductClient productClient,
                        OrderEventPublisher orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Transactional(readOnly = true)
    public OrderDTO findById(Long id, CurrentUser user) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: " + id));

        if (!user.admin() && !order.getClientId().equals(user.id())){
            throw new ForbiddenException("Acesso negado: este pedido pertence a outro cliente.");
        }
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO insert(OrderRequest request, CurrentUser user) {
        Order order = new Order();
        order.setClientId(user.id());
        order.setMoment(Instant.now());
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
        // Continua dentro do @Transactional: se o RabbitMQ estiver fora, o pedido é desfeito.
        orderEventPublisher.publishOrderCreated(order);

        return new OrderDTO(order);
    }

    // Chamado pelo PaymentConfirmedListener quando chega a mensagem do payment-service
    @Transactional
    public void markAsPaid(Long orderId, Instant paymentMoment) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: " + orderId));
        // Só um pedido que está esperando pagamento pode virar PAID.
        // Não lança exceção: uma exceção faria a mensagem voltar para a fila em loop.
        if (order.getStatus() != OrderStatus.WAITING_PAYMENT) {
            log.warn("Pedido {} está {}. Confirmação de pagamento ignorada.", orderId, order.getStatus());
            return;
        }
        order.setStatus(OrderStatus.PAID);
        order.setPaymentMoment(paymentMoment);
        orderRepository.save(order);
        log.info("Pedido {} marcado como PAID", orderId);
    }
}