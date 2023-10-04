package com.Heart2Hub.Heart2Hub_Backend.exception;

public class UnableToCreateMedicationException extends RuntimeException{
    public UnableToCreateMedicationException() {
    }

    public UnableToCreateMedicationException(String msg) {
        super(msg);
    }
}
