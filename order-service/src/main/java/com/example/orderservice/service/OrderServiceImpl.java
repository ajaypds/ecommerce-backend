package com.example.orderservice.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.commonproto.payment.PaymentResponse;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.exception.BadRequestException;
import com.example.orderservice.grpc.InventoryClient;
import com.example.orderservice.grpc.PaymentClient;
import com.example.orderservice.repository.OrderRepository;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

        private final OrderRepository orderRepository;
        private final InventoryClient inventoryClient;
        private final PaymentClient paymentClient;
        private final MeterRegistry meterRegistry;

        @Override
        public Order createOrder(UUID userId, CreateOrderRequest request) {

                log.info("Creating order for userId={}, productId={}",
                                userId, request.getProductId());

                boolean available = inventoryClient.isStockAvailable(
                                request.getProductId(),
                                request.getQuantity());

                if (!available) {
                        log.info("Insufficient stock for productId={}", request.getProductId());
                        meterRegistry.counter("orders", "status", "failed", "reason", "insufficient_stock").increment();
                        throw new BadRequestException("Insufficient stock");
                }
                log.info("Stock available for productId={}", request.getProductId());

                Order order = Order.builder()
                                .userId(userId)
                                .productId(request.getProductId())
                                .quantity(request.getQuantity())
                                .totalAmount(BigDecimal.valueOf(800)) // mock price
                                .status(OrderStatus.CREATED)
                                .createdAt(Instant.now())
                                .build();

                order = orderRepository.save(order);

                // Call payment
                PaymentResponse paymentResponse = paymentClient.processPayment(
                                order.getId().toString(),
                                order.getTotalAmount().doubleValue());

                if (!paymentResponse.getSuccess()) {
                        log.info("Payment failed for orderId={}", order.getId());
                        order.setStatus(OrderStatus.FAILED);
                        meterRegistry.counter("orders", "status", "failed", "reason", "payment_rejected").increment();
                        orderRepository.save(order);
                        throw new BadRequestException("Payment failed");
                }
                log.info("Payment successful for orderId={}", order.getId());

                inventoryClient.reduceStock(
                                request.getProductId(),
                                request.getQuantity());

                order.setStatus(OrderStatus.CREATED);
                log.info("Order created successfully with orderId={}", order.getId());
                meterRegistry.counter("orders", "status", "created", "reason", "payment_successful").increment();

                return orderRepository.save(order);
        }
}
