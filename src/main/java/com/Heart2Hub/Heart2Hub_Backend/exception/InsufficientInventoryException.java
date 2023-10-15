package com.Heart2Hub.Heart2Hub_Backend.exception;

public class InsufficientInventoryException extends RuntimeException{
    public InsufficientInventoryException() {
    }

    public InsufficientInventoryException(String message) {
        super(message);
    }
}
