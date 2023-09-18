package com.Heart2Hub.Heart2Hub_Backend.exception;

public class FacilityNotFoundException extends RuntimeException {
    public FacilityNotFoundException() {
    }

    public FacilityNotFoundException(String msg) {
        super(msg);
    }
}
