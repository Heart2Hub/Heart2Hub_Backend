package com.Heart2Hub.Heart2Hub_Backend.exception;

public class UnableToAssignAppointmentException extends RuntimeException {
    public UnableToAssignAppointmentException() {
    }

    public UnableToAssignAppointmentException(String msg) {
        super(msg);
    }
}
