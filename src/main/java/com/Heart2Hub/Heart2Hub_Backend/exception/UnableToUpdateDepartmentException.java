package com.Heart2Hub.Heart2Hub_Backend.exception;

public class UnableToUpdateDepartmentException extends RuntimeException{
    public UnableToUpdateDepartmentException() {
    }

    public UnableToUpdateDepartmentException(String msg) {
        super(msg);
    }
}
