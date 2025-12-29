package com.example.inventoryservice.exception;

public class ConcurrentUpdateException extends RuntimeException {
    public ConcurrentUpdateException(String message) {
        super(message);
    }
}

