package com.Heart2Hub.Heart2Hub_Backend.exception;

public class UnableToCreatePatientException extends RuntimeException{
    public UnableToCreatePatientException() {
    }

    public UnableToCreatePatientException(String msg) {
        super(msg);
    }
}
