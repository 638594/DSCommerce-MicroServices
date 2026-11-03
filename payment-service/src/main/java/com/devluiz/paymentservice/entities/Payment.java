package com.devluiz.paymentservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

/*
 * No monolito, Payment tinha um @OneToOne com Order.
 * Aqui o pedido mora em outro serviço, então guardamos só o orderId.
 *
 * unique = true: no máximo UM pagamento por pedido. Isso protege contra
 * a mesma mensagem chegar duas vezes (acontece em mensageria, ex.: depois de uma queda).
 */
@Entity
@Table(name = "tb_payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long orderId;

    private BigDecimal amount;
    private Instant moment;

    public Payment() {
    }

    public Payment(Long orderId, BigDecimal amount, Instant moment) {
        this.orderId = orderId;
        this.amount = amount;
        this.moment = moment;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getMoment() {
        return moment;
    }
}
