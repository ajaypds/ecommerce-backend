package com.example.orderservice.grpc;

import org.springframework.stereotype.Component;

import com.example.commonproto.inventory.InventoryServiceGrpc;
import com.example.commonproto.inventory.ReduceStockRequest;
import com.example.commonproto.inventory.StockRequest;
import com.example.commonproto.inventory.StockResponse;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;

@Component
@RequiredArgsConstructor
public class InventoryClient {

    @GrpcClient("inventory-service")
    private InventoryServiceGrpc.InventoryServiceBlockingStub stub;

    public boolean isStockAvailable(String productId, int quantity) {

        StockRequest request = StockRequest.newBuilder()
                .setProductId(productId)
                .build();

        StockResponse response = stub.checkStock(request);

        return response.getAvailable()
                && response.getQuantity() >= quantity;
    }

    public void reduceStock(String productId, int quantity) {

        ReduceStockRequest request = ReduceStockRequest.newBuilder()
                .setProductId(productId)
                .setQuantity(quantity)
                .build();

        stub.reduceStock(request);
    }
}
