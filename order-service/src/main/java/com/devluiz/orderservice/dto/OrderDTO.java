package com.devluiz.orderservice.dto;

import com.devluiz.orderservice.entities.Order;
import com.devluiz.orderservice.entities.OrderItem;
import com.devluiz.orderservice.entities.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/* O que a API DEVOLVE ao consultar um pedido. */
public record OrderDTO(
        Long id,
        Long clientId,
        Instant moment,
        OrderStatus status,
        Instant paymentMoment,
        List<Item> items,
        BigDecimal total
) {

    public OrderDTO(Order entity) {
        this(
                entity.getId(),
                entity.getClientId(),
                entity.getMoment(),
                entity.getStatus(),
                entity.getPaymentMoment(),
                entity.getItems().stream().map(Item::new).toList(),
                entity.getTotal()
        );
    }

    public record Item(Long productId, String name, BigDecimal price, Integer quantity, BigDecimal subTotal) {

        public Item(OrderItem item) {
            this(item.getProductId(), item.getProductName(), item.getPrice(),
                    item.getQuantity(), item.getSubTotal());
        }
    }
}
