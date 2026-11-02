package com.devluiz.orderservice.repositories;

import com.devluiz.orderservice.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
