package com.Heart2Hub.Heart2Hub_Backend.exception;

public class UnableToDeleteDepartmentException extends RuntimeException {
    public UnableToDeleteDepartmentException() {
    }

    public UnableToDeleteDepartmentException(String msg) {
        super(msg);
    }
}
