package com.Heart2Hub.Heart2Hub_Backend.exception;

public class StaffNotFoundException extends RuntimeException{
    public StaffNotFoundException() {
    }

    public StaffNotFoundException(String msg) {
        super(msg);
    }
}
