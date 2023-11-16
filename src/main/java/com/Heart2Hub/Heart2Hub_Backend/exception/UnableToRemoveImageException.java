package com.Heart2Hub.Heart2Hub_Backend.exception;

public class UnableToRemoveImageException extends RuntimeException{

    public UnableToRemoveImageException() {
    }

    public UnableToRemoveImageException(String message) {
        super(message);
    }
}
