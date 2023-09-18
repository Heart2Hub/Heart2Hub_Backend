package com.Heart2Hub.Heart2Hub_Backend.exception;

public class UnableToCreateShiftException extends RuntimeException{
    public UnableToCreateShiftException() {
    }

    public UnableToCreateShiftException(String msg) {
        super(msg);
    }
}
