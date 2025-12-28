package com.example.orderservice.controller;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse createOrder(
//            @RequestHeader("X-User-Id") String userId,
            Authentication authentication,
            @Valid @RequestBody CreateOrderRequest request) {

        UUID userId = UUID.fromString((String) authentication.getPrincipal());

        Order order = orderService.createOrder(userId, request);

        return new OrderResponse(
                order.getId().toString(),
                order.getStatus().name(),
                order.getTotalAmount()
        );
    }
}
