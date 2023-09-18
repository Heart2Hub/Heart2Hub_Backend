package com.Heart2Hub.Heart2Hub_Backend.exception;

public class StaffDisabledException extends RuntimeException {
    public StaffDisabledException() {
    }

    public StaffDisabledException(String msg) {
        super(msg);
    }
}
