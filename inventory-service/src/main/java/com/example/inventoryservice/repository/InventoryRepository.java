package com.example.inventoryservice.repository;

import java.util.Optional;
import java.util.UUID;
import com.example.inventoryservice.entity.Inventory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository
        extends JpaRepository<Inventory, UUID> {

    Optional<Inventory> findByProductId(String productId);
}

