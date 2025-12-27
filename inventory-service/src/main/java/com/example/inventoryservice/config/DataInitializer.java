package com.example.inventoryservice.config;

import com.example.inventoryservice.entity.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) {

        if (inventoryRepository.count() == 0) {
            inventoryRepository.save(
                    Inventory.builder()
                            .productId("PRODUCT-1")
                            .quantity(10)
                            .build()
            );
        }
    }
}

