package com.devluiz.paymentservice.controllers;

import com.devluiz.paymentservice.entities.Payment;
import com.devluiz.paymentservice.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
 * Só para você conferir no Postman quais pagamentos foram feitos.
 * Repare que NÃO existe POST: ninguém cria pagamento por HTTP.
 * Pagamentos só nascem a partir das mensagens do RabbitMQ.
 */
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<List<Payment>> findAll() {
        return ResponseEntity.ok(paymentService.findAll());
    }
}
