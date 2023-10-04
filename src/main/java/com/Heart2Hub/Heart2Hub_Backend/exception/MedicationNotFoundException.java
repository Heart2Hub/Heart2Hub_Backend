package com.Heart2Hub.Heart2Hub_Backend.exception;

public class MedicationNotFoundException extends RuntimeException {

    public MedicationNotFoundException() {
    }

    public MedicationNotFoundException(String msg) {
        super(msg);
    }
}
