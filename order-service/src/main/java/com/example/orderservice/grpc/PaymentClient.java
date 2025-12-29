package com.example.orderservice.grpc;

import com.example.commonproto.payment.PaymentRequest;
import com.example.commonproto.payment.PaymentResponse;
import com.example.commonproto.payment.PaymentServiceGrpc;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    @GrpcClient("payment-service")
    private PaymentServiceGrpc.PaymentServiceBlockingStub stub;

    @CircuitBreaker(name = "paymentService", fallbackMethod = "fallbackPayment")
    @Retry(name = "paymentService")
    public PaymentResponse processPayment(
            String orderId,
            double amount) {

        PaymentRequest request = PaymentRequest.newBuilder()
                .setOrderId(orderId)
                .setAmount(amount)
                .setCurrency("USD")
                .build();

        return stub.withDeadlineAfter(2, TimeUnit.SECONDS).processPayment(request);
    }

    /* ---------- FALLBACK ---------- */
    public PaymentResponse fallbackPayment(
            String orderId,
            double amount,
            Throwable ex) {

        return PaymentResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Payment service unavailable")
                .build();
    }
}
