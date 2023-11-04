package com.Heart2Hub.Heart2Hub_Backend.exception;

public class ClaimErrorException extends RuntimeException{
    public ClaimErrorException() {
    }

    public ClaimErrorException(String message) {
        super(message);
    }
}
