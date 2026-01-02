package com.example.orderservice.grpc;

import com.example.commonproto.inventory.InventoryServiceGrpc;
import com.example.commonproto.payment.PaymentServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLException;
import java.io.File;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class PaymentClientConfig {

    private final CertProperties certs;

    @Value("${grpc.client.paymentService.address}")
    private String paymentServiceAddress;
    @Value("${grpc.client.paymentService.port}")
    private Integer paymentServicePort;

    @Bean
    public ManagedChannel paymentChannel() throws SSLException {
        SslContext sslContext = GrpcSslContexts.forClient()
                .keyManager(new File(certs.getCertificateChain()), new File(certs.getPrivateKey()))
                .trustManager(new File(certs.getTrustCertCollection()))
                .build();

        ManagedChannel channel = NettyChannelBuilder
                .forAddress(paymentServiceAddress, paymentServicePort)
                .sslContext(sslContext)
                .negotiationType(io.grpc.netty.shaded.io.grpc.netty.NegotiationType.TLS)
                .build();
        log.info("Payment gRPC channel created with TLS");
        return channel;
    }

    @Bean
    public PaymentServiceGrpc.PaymentServiceBlockingStub paymentStub(ManagedChannel paymentChannel) {
        return PaymentServiceGrpc.newBlockingStub(paymentChannel);
    }
}
