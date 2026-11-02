package com.devluiz.orderservice.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/*
 * Ainda não tem cliente (User): o pedido ganha o "clientId" na etapa 3,
 * quando o user-service existir. Pagamento entra na etapa 5.
 */
@Entity
@Table(name = "tb_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant moment;

    // STRING grava "WAITING_PAYMENT" no banco, em vez de um número (0, 1, 2...).
    // Assim, mudar a ordem do enum no futuro não bagunça os pedidos antigos.
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // cascade ALL: salvar o pedido já salva os itens junto
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
    }

    public void addItem(OrderItem item) {
        item.setOrder(this);
        items.add(item);
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(OrderItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() {
        return id;
    }

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
