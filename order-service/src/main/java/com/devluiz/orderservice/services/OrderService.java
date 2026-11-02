package com.devluiz.orderservice.services;

import com.devluiz.orderservice.clients.ProductClient;
import com.devluiz.orderservice.clients.ProductResponse;
import com.devluiz.orderservice.dto.OrderDTO;
import com.devluiz.orderservice.dto.OrderRequest;
import com.devluiz.orderservice.entities.Order;
import com.devluiz.orderservice.entities.OrderItem;
import com.devluiz.orderservice.entities.OrderStatus;
import com.devluiz.orderservice.repositories.OrderRepository;
import com.devluiz.orderservice.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductClient productClient;

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
            // Aqui acontece a chamada HTTP para o product-service (porta 8081).
            // No monolito isso era productRepository.getReferenceById(...).
            ProductResponse product = productClient.findById(itemRequest.productId());

            // Nome e preço vêm do product-service, nunca do cliente
            OrderItem item = new OrderItem(product.id(), product.name(), product.price(), itemRequest.quantity());
            order.addItem(item);
        }

        order = orderRepository.save(order);
        return new OrderDTO(order);
    }
}
