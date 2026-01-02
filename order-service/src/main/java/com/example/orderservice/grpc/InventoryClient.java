package com.example.orderservice.grpc;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.commonproto.inventory.InventoryServiceGrpc;
import com.example.commonproto.inventory.ReduceStockRequest;
import com.example.commonproto.inventory.StockRequest;
import com.example.commonproto.inventory.StockResponse;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryClient {

//    @GrpcClient("inventoryService")
    @Autowired
    private InventoryServiceGrpc.InventoryServiceBlockingStub stub;

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fallbackStock")
    @Retry(name = "inventoryService")
    public boolean isStockAvailable(String productId, int quantity) {

        StockRequest request = StockRequest.newBuilder()
                .setProductId(productId)
                .build();

        StockResponse response = stub.withDeadlineAfter(4, TimeUnit.SECONDS)
                                .checkStock(request);

        return response.getAvailable()
                && response.getQuantity() >= quantity;
    }

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fallbackReduce")
    @Retry(name = "inventoryService")
    public void reduceStock(String productId, int quantity) {

        ReduceStockRequest request = ReduceStockRequest.newBuilder()
                .setProductId(productId)
                .setQuantity(quantity)
                .build();

        stub.withDeadlineAfter(4, TimeUnit.SECONDS)
                .reduceStock(request);
    }

    /* ---------- FALLBACKS ---------- */

    public boolean fallbackStock(
            String productId,
            int quantity,
            Throwable ex) {

        // Fail fast
        log.info("Inventory service unavailable, cannot verify stock for productId={}", productId);
        log.error("Inventory service unavailable for productId={}, cause={}", productId, ex.toString(), ex);
        return false;
    }

    public void fallbackReduce(
            String productId,
            int quantity,
            Throwable ex) {
        log.info("Inventory service unavailable, cannot reduce stock for productId={}", productId);
        throw new RuntimeException("Inventory service unavailable");
    }
}
