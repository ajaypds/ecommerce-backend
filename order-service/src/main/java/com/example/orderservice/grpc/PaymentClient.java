package com.example.orderservice.grpc;

import com.example.commonproto.payment.PaymentRequest;
import com.example.commonproto.payment.PaymentResponse;
import com.example.commonproto.payment.PaymentServiceGrpc;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    @GrpcClient("payment-service")
    private PaymentServiceGrpc.PaymentServiceBlockingStub stub;

    public PaymentResponse processPayment(
            String orderId,
            double amount) {

        PaymentRequest request = PaymentRequest.newBuilder()
                .setOrderId(orderId)
                .setAmount(amount)
                .setCurrency("USD")
                .build();

        return stub.processPayment(request);
    }
}
