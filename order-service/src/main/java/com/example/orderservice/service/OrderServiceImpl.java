package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.exception.BadRequestException;
import com.example.orderservice.grpc.InventoryClient;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    @Override
    public Order createOrder(UUID userId, CreateOrderRequest request) {

        log.info("Creating order for userId={}, productId={}",
                userId, request.getProductId());

        boolean available = inventoryClient.isStockAvailable(
                request.getProductId(),
                request.getQuantity()
        );

        if (!available) {
            throw new BadRequestException("Insufficient stock");
        }

        inventoryClient.reduceStock(
                request.getProductId(),
                request.getQuantity()
        );

        Order order = Order.builder()
                .userId(userId)
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .totalAmount(BigDecimal.valueOf(100)) // mock
                .status(OrderStatus.CREATED)
                .createdAt(Instant.now())
                .build();

        return orderRepository.save(order);
    }
}

