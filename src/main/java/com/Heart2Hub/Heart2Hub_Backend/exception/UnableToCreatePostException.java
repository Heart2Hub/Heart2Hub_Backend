package com.Heart2Hub.Heart2Hub_Backend.exception;

public class UnableToCreatePostException extends RuntimeException {
    public UnableToCreatePostException() {
    }

    public UnableToCreatePostException(String message) {
        super(message);
    }
}
