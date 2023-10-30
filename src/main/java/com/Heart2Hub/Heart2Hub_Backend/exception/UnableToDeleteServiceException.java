package com.Heart2Hub.Heart2Hub_Backend.exception;

public class UnableToDeleteServiceException extends RuntimeException {
    public UnableToDeleteServiceException() {
    }

    public UnableToDeleteServiceException(String msg) {
        super(msg);
    }
}
