package com.Heart2Hub.Heart2Hub_Backend.exception;

public class DepartmentNotFoundException extends RuntimeException {
    public DepartmentNotFoundException() {
    }

    public DepartmentNotFoundException(String msg) {
        super(msg);
    }
}
