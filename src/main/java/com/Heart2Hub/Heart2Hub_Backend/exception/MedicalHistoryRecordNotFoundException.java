package com.Heart2Hub.Heart2Hub_Backend.exception;

public class MedicalHistoryRecordNotFoundException extends RuntimeException {
    public MedicalHistoryRecordNotFoundException() {
    }

    public MedicalHistoryRecordNotFoundException(String msg) {
        super(msg);
    }
}
