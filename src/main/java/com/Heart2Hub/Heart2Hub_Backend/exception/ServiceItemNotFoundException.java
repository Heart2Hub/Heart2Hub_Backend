package com.Heart2Hub.Heart2Hub_Backend.exception;

public class ServiceItemNotFoundException extends RuntimeException{
    public ServiceItemNotFoundException() {
    }

    public ServiceItemNotFoundException(String msg) {
        super(msg);
    }
}
