package com.devluiz.orderservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/*
 * A MAIOR diferença em relação ao monolito está aqui.
 *
 * No monolito: o item tinha um @ManyToOne para Product (o objeto inteiro).
 * Aqui: o item guarda só o ID do produto + uma "fotografia" do nome e do preço
 * no momento da compra.
 *
 * Por quê?
 * 1. A tabela de produtos está em OUTRO banco. Não dá pra fazer JOIN com ela.
 * 2. Regra de negócio: se o produto ficar mais caro amanhã, o pedido de hoje
 *    tem que continuar com o preço que o cliente pagou.
 */
@Entity
@Table(name = "tb_order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public OrderItem() {
    }

    public OrderItem(Long productId, String productName, BigDecimal price, Integer quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public BigDecimal getSubTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
