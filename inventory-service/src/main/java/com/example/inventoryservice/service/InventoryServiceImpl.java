package com.example.inventoryservice.service;

import com.example.inventoryservice.exception.ConcurrentUpdateException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.example.inventoryservice.entity.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public Inventory getInventory(String productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    @Transactional
    public void reduceStock(String productId, int quantity) {
        try{
            Inventory inventory = getInventory(productId);

            if (inventory.getQuantity() < quantity) {
                throw new RuntimeException("Insufficient stock");
            }

            inventory.setQuantity(inventory.getQuantity() - quantity);

            // version is checked automatically here
            inventoryRepository.save(inventory);

            log.info("Stock updated for productId={}, remaining={}, version={}",
                    productId, inventory.getQuantity(), inventory.getVersion());
        } catch(ObjectOptimisticLockingFailureException e){
            throw new ConcurrentUpdateException("Inventory updated concurrently, please retry");
        }
    }
}
