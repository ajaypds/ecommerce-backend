package com.example.paymentservice.grpc;

import com.example.commonproto.payment.PaymentRequest;
import com.example.commonproto.payment.PaymentResponse;
import com.example.commonproto.payment.PaymentServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
@Slf4j
public class PaymentGrpcService
        extends PaymentServiceGrpc.PaymentServiceImplBase {

    @Override
    public void processPayment(
            PaymentRequest request,
            StreamObserver<PaymentResponse> responseObserver) {

        log.info("Processing payment for orderId={}, amount={}",
                request.getOrderId(), request.getAmount());

        // Mock rule: fail if amount > 1000
        boolean success = request.getAmount() <= 1000;

        if(!success){
            log.info("Payment failed due to amount exceeding limit for orderId={}", request.getOrderId());
        }

        PaymentResponse response = PaymentResponse.newBuilder()
                .setSuccess(success)
                .setTransactionId(UUID.randomUUID().toString())
                .setMessage(success ? "PAYMENT_SUCCESS" : "PAYMENT_FAILED")
                .build();

        log.info(success ?
                "Payment successful for orderId={}" :
                "Payment failed for orderId={}", request.getOrderId());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
