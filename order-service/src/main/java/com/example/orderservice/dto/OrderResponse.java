package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class OrderResponse {

    private String orderId;
    private String status;
    private BigDecimal totalAmount;
}
