package com.Heart2Hub.Heart2Hub_Backend.exception;

public class UnableToCreateMedicalHistoryRecordException extends RuntimeException {
    public UnableToCreateMedicalHistoryRecordException() {
    }

    public UnableToCreateMedicalHistoryRecordException(String msg) {
        super(msg);
    }
}
