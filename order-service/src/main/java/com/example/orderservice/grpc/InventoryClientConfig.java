package com.example.orderservice.grpc;

import com.example.commonproto.inventory.InventoryServiceGrpc;
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
public class InventoryClientConfig {

    private final CertProperties certs;

    @Value("${grpc.client.inventoryService.address}")
    private String inventoryServiceAddress;
    @Value("${grpc.client.inventoryService.port}")
    private Integer inventoryServicePort;

    @Bean
    public ManagedChannel inventoryChannel() throws SSLException {
//        log.info(certs.toString());
        SslContext sslContext = GrpcSslContexts.forClient()
                .keyManager(new File(certs.getCertificateChain()), new File(certs.getPrivateKey()))
                .trustManager(new File(certs.getTrustCertCollection()))
                .build();
        ManagedChannel channel = NettyChannelBuilder
                .forAddress(inventoryServiceAddress, inventoryServicePort)
                .sslContext(sslContext)
                .negotiationType(io.grpc.netty.shaded.io.grpc.netty.NegotiationType.TLS)
                .build();
        log.info("Created secure gRPC channel to inventory-service:9090");
        return channel;
    }

    @Bean
    public InventoryServiceGrpc.InventoryServiceBlockingStub inventoryStub(ManagedChannel inventoryChannel) {
        return InventoryServiceGrpc.newBlockingStub(inventoryChannel);
    }
}
