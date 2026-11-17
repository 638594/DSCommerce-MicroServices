package com.devluiz.orderservice.controllers;

import com.devluiz.orderservice.dto.OrderDTO;
import com.devluiz.orderservice.dto.OrderRequest;
import com.devluiz.orderservice.security.CurrentUser;
import com.devluiz.orderservice.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // GET /orders -> os pedidos do usuário logado, paginados
    @GetMapping
    public ResponseEntity<Page<OrderDTO>> findMine(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(sort = "moment", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.findMine(CurrentUser.from(jwt), pageable));
    }

    // @AuthenticationPrincipal Jwt: o Spring já conferiu o token e entrega o conteúdo dele
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> findById(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(orderService.findById(id, CurrentUser.from(jwt)));
    }

    @PostMapping
    public ResponseEntity<OrderDTO> insert(@Valid @RequestBody OrderRequest request, @AuthenticationPrincipal Jwt jwt) {
        OrderDTO dto = orderService.insert(request, CurrentUser.from(jwt));
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }
}
