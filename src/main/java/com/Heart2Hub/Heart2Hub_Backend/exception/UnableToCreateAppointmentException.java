package com.Heart2Hub.Heart2Hub_Backend.exception;

public class UnableToCreateAppointmentException extends RuntimeException {
    public UnableToCreateAppointmentException() {
    }

    public UnableToCreateAppointmentException(String msg) {
        super(msg);
    }
}
