package com.Heart2Hub.Heart2Hub_Backend.exception;

public class PatientNotFoundException extends RuntimeException{
    public PatientNotFoundException() {
    }

    public PatientNotFoundException(String msg) {
        super(msg);
    }
}
