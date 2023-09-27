package com.Heart2Hub.Heart2Hub_Backend.exception;

public class UnableToChangePasswordException extends RuntimeException{
    public UnableToChangePasswordException() {
    }

    public UnableToChangePasswordException(String msg) {
        super(msg);
    }
}
