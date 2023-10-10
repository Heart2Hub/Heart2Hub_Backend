package com.Heart2Hub.Heart2Hub_Backend.exception;

public class UnableToCreateServiceItemException extends RuntimeException{
    public UnableToCreateServiceItemException() {
    }

    public UnableToCreateServiceItemException(String msg) {
        super(msg);
    }
}
