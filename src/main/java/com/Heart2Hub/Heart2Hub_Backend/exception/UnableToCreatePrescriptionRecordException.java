package com.Heart2Hub.Heart2Hub_Backend.exception;

public class UnableToCreatePrescriptionRecordException extends RuntimeException{
    public UnableToCreatePrescriptionRecordException() {
    }

    public UnableToCreatePrescriptionRecordException(String msg) {
        super(msg);
    }
}
