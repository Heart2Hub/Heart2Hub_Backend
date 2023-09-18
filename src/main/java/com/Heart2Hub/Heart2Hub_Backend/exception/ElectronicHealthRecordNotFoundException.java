package com.Heart2Hub.Heart2Hub_Backend.exception;

public class ElectronicHealthRecordNotFoundException extends RuntimeException {
    public ElectronicHealthRecordNotFoundException() {
    }

    public ElectronicHealthRecordNotFoundException(String msg) {
        super(msg);
    }
}
