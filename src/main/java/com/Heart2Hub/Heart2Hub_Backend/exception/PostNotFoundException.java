package com.Heart2Hub.Heart2Hub_Backend.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException() {
    }

    public PostNotFoundException(String message) {
        super(message);
    }
}
