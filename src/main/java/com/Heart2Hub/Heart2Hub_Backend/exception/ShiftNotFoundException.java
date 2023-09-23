package com.Heart2Hub.Heart2Hub_Backend.exception;

public class ShiftNotFoundException extends RuntimeException{
    public ShiftNotFoundException() {
    }

    public ShiftNotFoundException(String msg) {
        super(msg);
    }
}
