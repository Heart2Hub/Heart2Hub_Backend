package com.Heart2Hub.Heart2Hub_Backend.exception;

public class ShiftPreferenceNotFoundException extends RuntimeException{
    public ShiftPreferenceNotFoundException() {
    }

    public ShiftPreferenceNotFoundException(String msg) {
        super(msg);
    }
}
