package com.Heart2Hub.Heart2Hub_Backend.exception;

public class UnableToCreateMedicationOrderException extends RuntimeException {
    public UnableToCreateMedicationOrderException() {
    }

    public UnableToCreateMedicationOrderException(String msg) {
        super(msg);
    }
}
