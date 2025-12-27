package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.entity.Order;
import java.util.UUID;

public interface OrderService {
    Order createOrder(UUID userId, CreateOrderRequest request);
}
