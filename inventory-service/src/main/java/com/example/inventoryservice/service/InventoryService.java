package com.example.inventoryservice.service;

import com.example.inventoryservice.entity.Inventory;

public interface InventoryService {

    Inventory getInventory(String productId);

    void reduceStock(String productId, int quantity);
}
