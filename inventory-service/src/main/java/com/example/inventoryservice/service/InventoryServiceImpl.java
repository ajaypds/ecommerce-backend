package com.example.inventoryservice.service;

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

        Inventory inventory = getInventory(productId);

        if (inventory.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);

        log.info("Stock reduced for productId={}, remaining={}",
                productId, inventory.getQuantity());
    }
}
