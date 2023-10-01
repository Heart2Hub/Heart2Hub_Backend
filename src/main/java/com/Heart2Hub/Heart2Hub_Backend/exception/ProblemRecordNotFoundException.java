package com.Heart2Hub.Heart2Hub_Backend.exception;

public class ProblemRecordNotFoundException extends RuntimeException {
    public ProblemRecordNotFoundException() {
    }

    public ProblemRecordNotFoundException(String msg) {
        super(msg);
    }
}
