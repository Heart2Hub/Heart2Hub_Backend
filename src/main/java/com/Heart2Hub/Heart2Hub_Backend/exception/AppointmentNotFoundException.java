package com.Heart2Hub.Heart2Hub_Backend.exception;

public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException() {
    }

    public AppointmentNotFoundException(String msg) {
        super(msg);
    }
}
