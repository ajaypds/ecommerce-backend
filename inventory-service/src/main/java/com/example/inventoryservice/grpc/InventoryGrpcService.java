package com.example.inventoryservice.grpc;

import com.example.commonproto.inventory.*;
import com.example.inventoryservice.entity.Inventory;
import com.example.inventoryservice.service.InventoryService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class InventoryGrpcService
        extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final InventoryService inventoryService;

    @Override
    public void checkStock(
            StockRequest request,
            StreamObserver<StockResponse> responseObserver) {

        Inventory inventory =
                inventoryService.getInventory(request.getProductId());

        StockResponse response = StockResponse.newBuilder()
                .setAvailable(inventory.getQuantity() > 0)
                .setQuantity(inventory.getQuantity())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void reduceStock(
            ReduceStockRequest request,
            StreamObserver<ReduceStockResponse> responseObserver) {

        inventoryService.reduceStock(
                request.getProductId(),
                request.getQuantity()
        );

        ReduceStockResponse response =
                ReduceStockResponse.newBuilder()
                        .setSuccess(true)
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

